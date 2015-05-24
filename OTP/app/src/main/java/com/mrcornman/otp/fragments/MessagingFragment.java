package com.mrcornman.otp.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.MessageAdapter;
import com.mrcornman.otp.items.models.MatchItem;
import com.mrcornman.otp.services.MessageService;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.Arrays;
import java.util.List;

public class MessagingFragment extends Fragment implements ServiceConnection, MessageClientListener {

    private final static String KEY_RECIPIENT_ID = "recipient_id";

    private EditText messageBodyField;
    private String messageBody;
    private MessageService.MessageServiceInterface messageService;
    private MessageAdapter messageAdapter;
    private ListView messagesList;

    private String recipientId;
    private String currentUserId;
    private MatchItem match;

    public static Fragment newInstance(String recipientId) {
        MessagingFragment fragment = new MessagingFragment();

        // arguments
        Bundle arguments = new Bundle();
        arguments.putString(KEY_RECIPIENT_ID, recipientId);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messaging, container, false);

        recipientId = getArguments().getString(KEY_RECIPIENT_ID);
        currentUserId = ParseUser.getCurrentUser().getObjectId();

        DatabaseHelper.getMatchByPair(recipientId, currentUserId, new GetCallback<MatchItem>() {
            @Override
            public void done(MatchItem matchItem, ParseException e) {
                if(e != null) {
                    Log.e("MessagingFragment", "There was a problem accessing a match");
                    return;
                }
                match = matchItem;
            }
        });

        messagesList = (ListView) view.findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(getActivity());
        messagesList.setAdapter(messageAdapter);
        populateMessageHistory();

        messageBodyField = (EditText) view.findViewById(R.id.messageBodyField);

        view.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getApplicationContext().bindService(new Intent(getActivity(), MessageService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        messageService.removeMessageClientListener(this);
        getActivity().getApplicationContext().unbindService(this);

        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        messageService = (MessageService.MessageServiceInterface) iBinder;
        messageService.addMessageClientListener(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        messageService = null;
    }

    @Override
    public void onMessageFailed(MessageClient client, Message message,
                                MessageFailureInfo failureInfo) {
        Toast.makeText(getActivity(), "Message failed to send.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onIncomingMessage(MessageClient client, Message message) {
        if (message.getSenderId().equals(recipientId)) {
            WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING);
        }
    }

    @Override
    public void onMessageSent(MessageClient client, Message message, String recipientId) {

        final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());

        //only add message to parse database if it doesn't already exist there
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseMessage");
        query.whereEqualTo("sinchId", message.getMessageId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messageList, com.parse.ParseException e) {
                if (e == null) {
                    if (messageList.size() == 0) {
                        ParseObject parseMessage = new ParseObject("ParseMessage");
                        parseMessage.put("senderId", currentUserId);
                        parseMessage.put("recipientId", writableMessage.getRecipientIds().get(0));
                        parseMessage.put("messageText", writableMessage.getTextBody());
                        parseMessage.put("sinchId", writableMessage.getMessageId());
                        parseMessage.saveInBackground();

                        messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING);

                        DatabaseHelper.updateMatchNumMessages(match.getObjectId(), match.getNumMessages() + 1);
                    }
                }
            }
        });
    }

    @Override
    public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {}

    @Override
    public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {}

    //get previous messages from parse & display
    private void populateMessageHistory() {
        String[] userIds = {currentUserId, recipientId};
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseMessage");
        query.whereContainedIn("senderId", Arrays.asList(userIds));
        query.whereContainedIn("recipientId", Arrays.asList(userIds));
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messageList, com.parse.ParseException e) {
                if (e == null) {
                    for (int i = 0; i < messageList.size(); i++) {
                        WritableMessage message = new WritableMessage(messageList.get(i).get("recipientId").toString(), messageList.get(i).get("messageText").toString());
                        if (messageList.get(i).get("senderId").toString().equals(currentUserId)) {
                            messageAdapter.addMessage(message, MessageAdapter.DIRECTION_OUTGOING);
                        } else {
                            messageAdapter.addMessage(message, MessageAdapter.DIRECTION_INCOMING);
                        }
                    }
                }
            }
        });
    }

    private void sendMessage() {
        messageBody = messageBodyField.getText().toString();
        if (messageBody.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }

        messageService.sendMessage(recipientId, messageBody);
        messageBodyField.setText("");
    }
}