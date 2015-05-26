Parse.Cloud.define("findPotentialUsers", function(request, response) {
  var otherUser = request.params.otherUser;
  var excludedIds = request.params.excludedIds;
  var limit = request.params.limit;
  var location = request.params.location;
  var searchDistance = request.params.searchDistance;
  
  if(!limit || !location || !searchDistance)
    response.error("findPotentialUsers: Incorrect params given");
    
  var locationQuery = new Parse.Query(Parse.User);
  locationQuery.limit(limit);
  locationQuery.notContainedIn("objectId", excludedIds);
  locationQuery.withinMiles("location", location, searchDistance);

  locationQuery.find({
      success: function(results) {
          response.success(results)
      },
      error: function(err) {
          response.error("Found no user(s) within range: " + err);
      }
  });
});

Parse.Cloud.afterSave("ParseMessage", function(request) {
  var senderId = request.object.get("senderId");
  var recipientId = request.object.get("recipientId");
  var messageText = request.object.get("messageText");
  if(!senderId || !recipientId || !messageText) return;
  
  // get the sender name, then send a push notification to recipient titled with the name
  var senderQuery = new Parse.Query(Parse.User);
  senderQuery.get(senderId, {
    success: function(user) {
      var pushQuery = new Parse.Query(Parse.Installation);
      pushQuery.equalTo('userId', recipientId);
      Parse.Push.send({
        where: pushQuery,
        data: {
          title: "New message from " + user.get("name"),
          alert: messageText
        }
      }, {
        success: function() {
          console.log("Message push (" + senderId + " -> " + recipientId + ")");
        },
        error: function(err) {
          console.log(err);
        }
      });
    },
    error: function(user, err) {
      console.log(err);
    }
  });
});

Parse.Cloud.afterDelete(Parse.User, function(request) {
  query = new Parse.Query("Match");
  //query.equalTo("first_id", 
});

function getMatchByPair(firstId, secondId, options) {
  var query = new Parse.Query("Match");
  var ids = [ firstId, secondId ];
  query.containedIn("first_id", ids);
  query.containedIn("second_id", ids);
  query.first(options);
}