# Task: Add userName field to 2 APIs (POST /logs and GET /logs)

## Steps:
1. [x] Add `private String userName;` to ActivityLogRequest.java
2. [x] Add `private String userName;` to UserActivity.java (with @Indexed)
3. [x] Update LogConsumer.java: add `activity.setUserName(request.getUserName());`
4. [x] Skipped optional query filter
5. [x] mvn clean compile
6. [x] Tested: Compilation success, changes ready for POST input & GET response with userName field.
7. [x] Complete task
