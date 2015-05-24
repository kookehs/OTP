Parse.Cloud.define("getNear", function(request, response) {
    var userQuery = new Parse.Query(Parse.User);
    userQuery.equalTo("username", request.params.username);

    userQuery.first({
        success: function(user) {
            var locationQuery = new Parse.Query(Parse.User);
            locationQuery.limit(10);
            locationQuery.notEqualTo("username", request.params.username);
            locationQuery.withinMiles("location", user.get("location"), 50);

            locationQuery.find({
                success: function(results) {
                    response.success(results)
                },
                error: function(error) {
                    response.error("Found no user(s) within range");
                }
            });
        },
        error: function(error) {
            response.error("Unable to retrieve nearby user(s)");
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

function getMatchByPair(firstId, secondId, options) {
  var query = new Parse.Query("Match");
  var ids = [ firstId, secondId ];
  query.containedIn("first_id", ids);
  query.containedIn("second_id", ids);
  query.first(options);
}