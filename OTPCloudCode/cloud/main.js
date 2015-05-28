Parse.Cloud.define("findPotentialUsers", function(request, response) {
  var otherId = request.params.otherId;
  var excludedIds = request.params.excludedIds;
  var limit = request.params.limit;
  var location = request.params.location;
  var searchDistance = request.params.searchDistance;
  
  if(!limit || !location || !searchDistance)
    response.error("findPotentialUsers: Incorrect params given");
   
  var otherCallback = function(origin) {
    var query = new Parse.Query(Parse.User);
    query.limit(limit);
    query.notContainedIn("objectId", excludedIds);
    query.withinMiles("location", origin, searchDistance);

    query.find({
      success: function(results) {
        response.success(results);
      },
      error: function(err) {
        response.error("Found no user(s) within range: " + err);
      }
    });
  }
  
  if(otherId) {
    var otherQuery = new Parse.Query(Parse.User);
    otherQuery.get(otherId, {
      success: function(user) {
        otherCallback(user.get("location"));
      },
      error: function(user, err) {
        response.error("There was a problem getting the other user");
      }
    });
  } else {
    otherCallback(location);
  }
});

Parse.Cloud.define("findClientMatches", function(request, response) {
  var clientId = request.params.clientId;
  var limit = request.params.limit;
  
  if(!clientId || !limit)
    response.error("findClientMatches: Incorrect params given");
    
  var firstQuery = new Parse.Query("Match");
  firstQuery.equalTo("first_id", clientId);
  var secondQuery = new Parse.Query("Match");
  secondQuery.equalTo("second_id", clientId);
   
  var query = new Parse.Query.or(firstQuery, secondQuery);
  query.limit(limit);
  query.descending("num_likes");

  query.find({
    success: function(results) {
      response.success(results);
    },
    error: function(err) {
      response.error("Problem finding client matches: " + err);
    }
  });
});

Parse.Cloud.define("findMakerMatches", function(request, response) {
  var makerId = request.params.makerId;
  var limit = request.params.limit;
  
  if(!makerId || !limit)
    response.error("findMakerMatches: Incorrect params given");
   
  var query = new Parse.Query("Match");
  query.limit(limit);
  query.descending("num_likes");
  query.equalTo("followers", makerId);

  query.find({
    success: function(results) {
      response.success(results);
    },
    error: function(err) {
      response.error("Problem finding matches: " + err);
    }
  });
});

Parse.Cloud.afterSave("Match", function(request) {
  var firstId = request.object.get("first_id");
  var secondId = request.object.get("second_id");
  var followers = request.object.relation("followers");
  if(!firstId || !secondId || !followers) {
    console.log("Problem with Match afterSave!");
    return;
  }
  console.log(followers);
  
  // send push notifications to followers of the match
  var followerQuery = followers.query();
  Parse.Push.send({
    where: followerQuery,
    data: {
      title: "Match Activity",
      alert: "Someone just liked your match!"
    }
  }, {
    success: function() {
      console.log("Match push for: " + firstId + " / " + secondId);
    },
    error: function(err) {
      console.log(err);
    }
  });
});

Parse.Cloud.afterSave("ParseMessage", function(request) {
  var senderId = request.object.get("senderId");
  var recipientId = request.object.get("recipientId");
  var messageText = request.object.get("messageText");
  if(!senderId || !recipientId || !messageText) {
    console.log("Problem with Match afterSave!");
    return;
  }
  
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