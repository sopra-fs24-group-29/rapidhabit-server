# Contributions  

## ❕IMPORTANT: Please note that you can find all commit links within the comment section of the corresponding issues. With our tables, we hope to make your reviewing process as convenient as possible. 😊

## 25.3-09.04 Week 1 (Including Easter Break)
### Lukas (lguebeli):
- general: Wrote Test for all the new REST Mappings
- As well as the following issues:

| IssueNr                                                                                        | Issue description                              | Assigned Test                                                                                                                                                                                                                   |
|:-----------------------------------------------------------------------------------------------|:-----------------------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [#16](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/16)                 | Create User-Objects in Database                | - POST Mapping "/users" - CODE 204 CREATED (Pass) <br> - POST Mapping "/users" - CODE 409 CONFLICT (error) <br> – [bb24323c](https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/bb24323c9bf5be7dd886829b79b4ecc1cb41a13f)   |
| [#17](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/17)                 | Implement API Endpoint for fetching Assigned Groups | - GET Mapping "/groups" - CODE 200 OK (Pass) <br> - GET Mapping "/groups" - CODE 401 Unauthorized (Error)                                                                                                                      |
| [#19](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/19)                 | Implement API Endpoints for Password Change    | - PUT Mapping "/users/password" - CODE 204 NoContent (pass) <br> - PUT Mapping "/users/password" - CODE 404 NotFound (error) <br> - PUT Mapping "/users/password" - CODE 401 Unauthorized (error)                             |
| [#56](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/56)                 | Implement logout functionality                 | - PUT Mapping "/users/logout" - CODE 200 OK (pass) <br> - PUT Mapping "/users/logout" - CODE 404 NotFound (error)                                                                                                              |

          

### Simon (SimonHafner):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/52     | Create an API for user authorization | [a656cf4](https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/a656cf475c417ecacc7ddd7beae3180a09fbc8ff) |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/18     | Implemented endpoint for group creation | [c3816dc](https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/c3816dc0f7a6af7b1ff6a821073511499aa25557) |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/23     | Implemented API for joining an existing group | [c3816dc](https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/c3816dc0f7a6af7b1ff6a821073511499aa25557) |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/77     | Implemented API for joining an existing group (each user can join only once) | [c3816dc](https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/c3816dc0f7a6af7b1ff6a821073511499aa25557) |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/20     | Users which create a group are automatically the administrator of this group | [c3816dc](https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/c3816dc0f7a6af7b1ff6a821073511499aa25557) |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/55     | Develop a REST API for User Registration and Authentication working with Spring Boot and MongoDB | No test required |



### Yannik (YRiederer):

| IssueNr | Issue description |
|:--------|-------------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/4      | Create and configure App routing                 |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/31     | Create Not Found screen                  |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/32     | Create Welcome Screen                  |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/33       |   Create login screen                |               |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/34       |  Create Register Screen                 |               |



### Raksmey (rocketraksi):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/41  | Set up and adapt the codebase to full typescript           |               |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/40  | Set up tailwind for the whole application and adapt current design           |               |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/39   | Set up Manifest for PWA       |               |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/42| Set up Vite instead of create-react      |               |



## Week 2  10.04 - 16.04
### Lukas (lguebeli):
- general: Wrote Test for all the new REST Mappings for example for Issue #35
- As well as the following issues:

| IssueNr | Issue description                                                   | Assigned Test                                                                                                                                                                                                      |
|:--------|---------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/21     | Group administrators can update a group name and group description. | - PUT Mapping "/groups/{groupId}" - CODE 200 OK (Pass)  <br> - PUT Mapping "/groups/{groupId}" - CODE 404 NotFound (Pass)  <br> - PUT Mapping "/groups/{groupId}" - CODE 401 Unauthorized (Error)                  |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/89     | Implement API for Account Deletion                                  | - DELETE Mapping "/users/ID" - CODE 204 No Content (pass)  <br> - DELETE Mapping "/users/ID" - CODE 404 NotFound (error)  <br> - DELETE Mapping "/users/ID" - CODE 401 Unauthorized (error)                        |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/91     | Users can change their profiledata (eg. Email, first-/lastname)     | - PUT Mapping "/users/update" - CODE 204 No Content (pass)  <br>  - PUT Mapping "/users/update" - CODE 404 Not Found (error)  <br>  - PUT Mapping "/users/update" - CODE 401 Unauthorized (error)                                                                                        |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/62     | Implement API Endpoint for Group Deletion                           | - DELETE Mapping "/groups/{groupId}" - CODE 204 NoContent (Pass)  <br> - DELETE Mapping "/groups/{groupId}" - CODE 404 NotFound (Error)  <br> - DELETE Mapping "/groups/{groupId}" - CODE 401 Unauthorized (Error) |

### Simon (SimonHafner):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| [Issue #31](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/31) | Implement Database Model for Group Habits | [7f4a0cb](https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/7f4a0cb88c4f6605cd25b4aeb5d2a45149e5c1fc) |
| [Issue #29](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/29) | Implemented DTOs for transferring Habit Data | [7f4a0cb](https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/7f4a0cb88c4f6605cd25b4aeb5d2a45149e5c1fc) |
| [Issue #63](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/63) | Implement API Endpoint for Generating Group-Specific Code | [7f4a0cb](https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/7f4a0cb88c4f6605cd25b4aeb5d2a45149e5c1fc) |
| [Issue #95](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/95) | Implemented API for creating Group Habits | [7f4a0cb](https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/7f4a0cb88c4f6605cd25b4aeb5d2a45149e5c1fc) |
| [Issue #96](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/96) | Implement Data Model for Group Statistics | Tested on MongoDB Compass |
| [Issue #97](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/97) | Implemented Data Model for HabitStreaks, UserScores | Tested on MongoDB Compass |


### Yannik (YRiederer):

| IssueNr | Issue description |
|:--------|-------------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/7       | Design and Implement User Profile Page Component and UI                  |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/8       | Implement password change and account deletion feature                   |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/20       | Design and Implement Join Group Component and UI                  |


### Raksmey (rocketraksi):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/90    | Create sticky navigation bar|               |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/24  | Group detail overview|               |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/14  | Create Group Compnent |               |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/6  | Group Dashboard |               |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/29 | A lot of fixes that the app works |               |





## Week 3 17.04 - 23.04
### Lukas (lguebeli):
- general: Wrote Test for all the new REST Mappings
- As well as the following issues:

| IssueNr | Issue description                      | Assigned Test                                                                                                                                                                                                                 |
|:--------|----------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/51     | Add API to delete users from the group | - DELETE Mapping "/groups/{groupId}/users" - CODE 204 NoContent  <br> - DELETE Mapping "/groups/{groupId}/users" - CODE 404 NotFound (Error)  <br> - DELETE Mapping "/groups/{groupId}/users" - CODE 401 Unauthorized (Error) |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/93     | Created Test for this issue            | - GET Mapping "/groups/{groupId}/habits" - CODE 200 Ok (pass)                                                                                                                                                                 |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/94     | Created Test for this issue            | - GET Mapping "/groups/{groupId}/habits/{habitId}" - CODE 200 Ok (pass)  <br> - GET Mapping "/groups/{groupId}/habits/{habitId}" - CODE 404 Not Found (Error)                                                                 |

### Simon (SimonHafner):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| [Issue #98](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/98) | Implemented REST API for retrieving GetHabitDTOs | [ece246c](https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/ece246c31190548454fcb679fa7d25fdf8cfb08a) |
| [Issue #99](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/99) | Implement REST API for retrieving group activity data | Tested on MongoDB Compass |
| [Issue #100](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/100) | Implement REST API for retrieving group ranking | [bcb0895d](https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/bcb0895d97fb974df0691ab7a32f889d621c30b2) |
| [Issue #101](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/101) | Implement REST API for retrieving Detailed Habit Info with Daily Map | [ece246c](https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/ece246c31190548454fcb679fa7d25fdf8cfb08a) |
| [Issue #102](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/102) | Configure Mongo DB connection in the production environment | Tested on MongoDB Compass |
| [Issue #103](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/103) | Implement API endpoint for checking habits ✔️ | [1c15a513](https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/1c15a51302dc03dbd72e0601da250f3b4baa2b8c) |


### Yannik (YRiederer):

| IssueNr | Issue description |
|:--------|-------------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/15       | Design and Implement Invite Users Component and UI                   |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/19       | Create an admin page where different data gets displayed, such as the users and the habits of the group                  |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/36       | Users can invite other users to join the group by sending a group specific code                  |


### Raksmey (rocketraksi):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/35
https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/26
https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/14
https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/14
https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/10
https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/13
https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/10
https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/2
https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/9
https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/6
https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/3
https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/5
https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/11
https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/27
https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/21
https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/22
https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/4



## Week 4 24.04 - 30.04 and Week 5 01.05 - 07.05
### Lukas (lguebeli):
- general: Wrote Test for all the new REST Mappings & all the Services (now on 75% Coverage)
- As well as the following issues:

| IssueNr                                                             | Issue description                                                                                         | Assigned Test |
|:--------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|---------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/107 | implement API for deleting a group Habit                                                                  | - DELETE Mapping "/groups/{groupId}/habits/{habitId}" - CODE 204 No Content (Success)            |
| #37                                                                 | Implement an API for receiving chat messages in a specific Group.                                         |               |
| #113                                                                | Implement the Backend so that Users can send chat messages in specific group.                             |               |
| #115                                                                | implement security features that only members of group can fetch previous messages in group specific chat |               |


### Simon (SimonHafner):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| [Issue #104](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/104) | Implement auto-computation and updates for streaks, scores, and ranks | [ece246c](https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/ece246c31190548454fcb679fa7d25fdf8cfb08a) |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/105     | Implement Pulse Check (Full-Stack) | Tested via interface |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/106     | Implement Feed (Full-Stack) | Tested via interface | |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/110     | Implemented Routine Scheduler of daily close-up procesdure | Tested on MongoDB Compass | https://github.com/sopra-fs24-group-29/rapidhabit-server/commit/ece246c31190548454fcb679fa7d25fdf8cfb08a |
| [#109](https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/109)                 | Implement API for submitting Pulse Check Response                 | ...                                                                                                              |


### Yannik (YRiederer):

| IssueNr | Issue description |
|:--------|-------------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/16       | Design and Implement Update Group Info Component and UI                  |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/43      | create a box to invite new people into a group from the habit dashboard                  |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/44       | hide settings field for non admins                  |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/45        | navigation between login and registration                  |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/46        | fix feed design/layout to the rest of the application                  |



### Raksmey (rocketraksi):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #     https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/9  |     Create the chat in the frontend              |               |
| #       |          bug fixes fromm feedback         |               |
| #       |                   |    front end clean-up           |


## Week 6 08.05 - 14.05
### Lukas (lguebeli):
- general: Wrote Test for all the new REST Mappings
- As well as the following issues:

| IssueNr | Issue description                                                                                  | Assigned Test                                                                                                                                                                                                                                                         |
|:--------|----------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| #116    | Create REST-API to retrieve the chat history from the chat                                         | - GET Mapping "/groups/{groupId}/chat" - CODE 200 OK (Pass)  <br> - GET Mapping "/groups/{groupId}/chat" - CODE 401 Unauthorized because invalid Token (Error)  <br> - GET Mapping "/groups/{groupId}/chat" - CODE 401 Unauthorized because user not in group (Error) |
| #117    | Clean Up User controller file with SonarLint (code smells and bad code)                            | None                                                                                                                                                                                                                                                                  |
| #118    | Clean Up group controller file with SonarLint (code smells and bad code)                           | None                                                                                                                                                                                                                                                                  |
| #119    | Clean Up habit controller file with SonarLint (code smells and bad code)                           | None                                                                                                                                                                                                                                                                  |


### Simon (SimonHafner):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |


### Yannik (YRiederer):

| IssueNr | Issue description |
|:--------|-------------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/47       |  feedback fix: display of group and habit description                 |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/48       |  feedback fix: button designs                 |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/49       |  feedback fix: press enter triggers functionalities                 |


### Raksmey (rocketraksi):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #     https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/9  |      Make the chat work in the front end            |               |
| #       |         QA Tasks, Bug Fixes          |               |



## Week 7 15.05 - 21.05
### Lukas (lguebeli):
- general: Wrote Test for all the new REST Mappings
- As well as the following issues:

| IssueNr | Issue description                                                            |
|:--------|------------------------------------------------------------------------------|
| #120    | Clean Up Chat controller file with SonarLint (code smells and bad code)      |
| #121    | Clean Up ALL other controller file with SonarLint (code smells and bad code) |
| #122    | Clean Up User Service file with SonarLint (code smells and bad code)         |
| #123    | Clean Up group Service file with SonarLint (code smells and bad code)        |


### Simon (SimonHafner):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |


### Yannik (YRiederer):

| IssueNr | Issue description |
|:--------|-------------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/50       | input validation for updating group                  |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/51       | input validation for profile update                  |
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/52       | enter key sends chat msg                  |


### Raksmey (rocketraksi):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|

| #       |       Added Validation            |               |
| #       |       Added clear error handleing            |               |

