package com.mrcornman.otp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.mrcornman.otp.adapters.MyntraCategoryExpandableListAdapter;
import com.mrcornman.otp.models.MyntraCategory;

import java.util.HashMap;
import java.util.List;


public class MyActivity extends Activity {

    private static final String PARENT_KEY = "pKey";
    private static final String CHILD_KEY = "cKey";
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Button listButton = (Button)findViewById(R.id.list_activity_btn);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listActivityIntent = new Intent(getBaseContext(), com.mrcornman.otp.ProductListViewActivity.class);
                startActivity(listActivityIntent);
            }
        });

        Button myntraTinderButton = (Button)findViewById(R.id.tinder_button);
        myntraTinderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tinderActivityIntent;
                tinderActivityIntent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(tinderActivityIntent);
            }
        });

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);

//        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
//        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
//
//        Map<String, String> curGroupMap = new HashMap<String, String>();
//        groupData.add(curGroupMap);
//        curGroupMap.put(PARENT_KEY, "Hello");
//        curGroupMap.put(CHILD_KEY, "First Order System Response");
//
//        List<Map<String, String>> children = new ArrayList<Map<String, String>>();
//
//        Map<String, String> curChildMap = new HashMap<String, String>();
//        children.add(curChildMap);
//        curChildMap.put(PARENT_KEY, "World");
//        curChildMap.put(CHILD_KEY, "Second Order System");
//
//        childData.add(children);

        // Set up our adapter
//        expandableListAdapter = new SimpleExpandableListAdapter(this, groupData,
//                android.R.layout.simple_expandable_list_item_1, new String[] {
//                PARENT_KEY, CHILD_KEY }, new int[] {
//                android.R.id.text1, android.R.id.text2 }, childData,
//                android.R.layout.simple_expandable_list_item_2, new String[] {
//                PARENT_KEY, CHILD_KEY }, new int[] {
//                android.R.id.text1, android.R.id.text2 });
        //prepareListData();
        List<MyntraCategory.ProductHeadGroup> myntraCategories = MyntraCategory.generateSampleProductHeadGroups(this);
        expandableListAdapter = new MyntraCategoryExpandableListAdapter(this, myntraCategories);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                ExpandableListAdapter adp = expandableListView.getExpandableListAdapter();
                MyntraCategory.ProductGroup pg = (MyntraCategory.ProductGroup) adp.getChild(groupPosition, childPosition);
                Log.e("checking if click event on group is working", pg.getUniqueGroupLabel());
                return true;
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                if (i == 110) {
                    Log.e("group click listener", "working");
                    return true;
                } else {
                    return false;
                }
                //return true;
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int len = expandableListAdapter.getGroupCount();
                for (int i = 0; i < len; i++) {
                    if (i != groupPosition) {
                        expandableListView.collapseGroup(i);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
