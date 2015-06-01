var PUSH_TYPE_MESSAGE = 1;
var PUSH_TYPE_NEW_MATCH = 2;
var PUSH_TYPE_MATCH_LIKED = 3;

Parse.Cloud.job("userMigration", function(request, status) {
  // Set up to modify user data
  Parse.Cloud.useMasterKey();
  var counter = 0;
  // Query for all users
  var query = new Parse.Query(Parse.User);
  query.each(function(user) {
      // Update to plan value passed in
      user.set("plan", request.params.plan);
      if (counter % 100 === 0) {
        // Set the  job's progress status
        status.message(counter + " users processed.");
      }
      counter += 1;
      return user.save();
  }).then(function() {
    // Set the job's success status
    status.success("Migration completed successfully.");
  }, function(error) {
    // Set the job's error status
    status.error("Uh oh, something went wrong.");
  });
});

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
  var match = request.object;
  var firstId = match.get("first_id");
  var secondId = match.get("second_id");
  var followers = match.relation("followers");
  if(!firstId || !secondId || !followers) {
    console.log("Problem with Match afterSave!");
    return;
  }
  
  var isNew = !match.existed();
  if(isNew) {
    // send push notifications to the matched pair
    var pushQuery = new Parse.Query(Parse.Installation);
    var pair = [ firstId, secondId ]
    pushQuery.containedIn('userId', pair);
    Parse.Push.send({
      where: pushQuery,
      data: {
        notificationType: PUSH_TYPE_NEW_MATCH,
        title: "New Seed!",
        alert: "Someone just paired you up!"
      }
    }, {
      success: function() {
        console.log("New match push to: " + pair);
      },
      error: function(err) {
        console.log(err);
      }
    });
  } else {
    // send push notifications to followers of the match
    var followerQuery = followers.query();
    followerQuery.find({
      success: function(results) {
        var resultIds = [];
        for(var i = 0, il = results.length; i < il; i++) {
          resultIds.push(results[i].id);
        }
        var pushQuery = new Parse.Query(Parse.Installation);
        pushQuery.containedIn('userId', resultIds);
        Parse.Push.send({
          where: pushQuery,
          data: {
            notificationType: PUSH_TYPE_MATCH_LIKED,
            title: "More likes!",
            alert: "Someone just liked your seed!"
          }
        }, {
          success: function() {
            console.log("Match liked push to " + resultIds.length + " makers: " + resultIds);
          },
          error: function(err) {
            console.log(err);
          }
        });
      },
      error : function(err) {
        console.log(err);
      }
    });
  }
});

Parse.Cloud.afterSave("ParseMessage", function(request) {
  var senderId = request.object.get("senderId");
  var recipientId = request.object.get("recipientId");
  var messageText = request.object.get("messageText");
  if(!senderId || !recipientId || !messageText) {
    console.log("Problem with Message afterSave!");
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
          notificationType: PUSH_TYPE_MESSAGE,
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