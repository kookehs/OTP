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
