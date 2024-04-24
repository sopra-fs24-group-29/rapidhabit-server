# Contributions  

## 25.3-09.04 Week 1 (Including Easter Break)
### Lukas (lguebeli):
- general: Wrote Test for all the new REST Mappings
- As well as the following issues:

| IssueNr | Issue description                                   | Assigned Test                                                                                                                                                                                       |
|:--------|-----------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/16     | Create User-Objects in Database                     |  - POST Mapping "/users" - CODE 204 CREATED (Pass)  <br> - POST Mapping "/users" - CODE 409 CONFLICT (error)
                                                                                         |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/17     | Implement API Endpoint for fetching Assigned Groups | - GET Mapping "/groups" - CODE 200 OK (Pass)  <br> - GET Mapping "/groups" - CODE 401 Unaurthorized (Error)
                                                                                         |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/19     | Implement API Endpoints for Password Change         | - PUT Mapping "/users/password" - CODE 204 NoContent (pass)  <br> - PUT Mapping "/users/password" - CODE 404 NotFound (error)  <br> - PUT Mapping "/users/password" - CODE 401 Unauthorized (error) |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/56     | Implement logout functionality                      | - PUT Mapping "/users/logout" - CODE 200 OK (pass)   <br> - PUT Mapping "/users/logout" - CODE 404 NotFound (error)                                                                                                                                                                                                |

### Simon (SimonHafner):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/52     | Create an API for user authorization | ? |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/18     | Implemented endpoint for group creation | ? |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/23     | Implemented API for joining an existing group | #18, #23, #17, #21 |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/77     | Implemented API for joining an existing group (each user can join only once) | #18, #23, #17, #21 |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/20     | Users which create a group are automatically the administrator of this group | ? |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/55     | Develop a REST API for User Registration and Authentication working with Spring Boot and MongoDB | ? |



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
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/31     | Implement Database Model for Group Habits | ? |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/29     | Implemented DTOs for transferring Habit Data | #93, #94 |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/63     | Implement API Endpoint for Generating Group-Specific Code | ? |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/95     | Implemented API for creating Group Habits | ? |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/96     | Implement Data Model for Group Statistics | ? |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/97     | Implemented Data Model for HabitStreaks, UserScores | ? |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/98     | Implemented REST API for retrieving GetHabitDTOs | ? |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/99     | Implement REST API for retrieving group activity data | ? |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/100     | Implement REST API for retrieving group ranking | ? |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/101     | Implement REST API for retrieving Detailed Habit Info with Daily Map | ? |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/102     | Configure Mongo DB connection in the production environement | ? |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/103     | Implement API endpoint for checking habits ✔️ | ? |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/104     | Implement auto-computation and updates for streaks, scores, and ranks | ? |

Implemented DTOs for transferring Habit Data
| #       |                   |               |
| #       |                   |               |


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
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/105     | Implement Pulse Check (Full-Stack) | ? |
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/106     | Implement Feed (Full-Stack) | ... |


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



## Week 4 24.04 - 30.04
### Lukas (lguebeli):
- general: Wrote Test for all the new REST Mappings
- As well as the following issues:

| IssueNr | Issue description                        | Assigned Test |
|:--------|------------------------------------------|---------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-server/issues/107    | implement API for deleting a group Habit |               |
| #       |                                          |               |
| #       |                                          |               |

### Simon (SimonHafner):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |


### Yannik (YRiederer):

| IssueNr | Issue description |
|:--------|-------------------|
| https://github.com/sopra-fs24-group-29/rapidhabit-client/issues/16       | Design and Implement Update Group Info Component and UI                  |


### Raksmey (rocketraksi):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |

## Week 5 01.05 - 07.05
### Lukas (lguebeli):
- general: Wrote Test for all the new REST Mappings
- As well as the following issues:

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |

### Simon (SimonHafner):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |


### Yannik (YRiederer):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |


### Raksmey (rocketraksi):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |

## Week 6 08.05 - 14.05
### Lukas (lguebeli):
- general: Wrote Test for all the new REST Mappings
- As well as the following issues:

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |

### Simon (SimonHafner):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |


### Yannik (YRiederer):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |


### Raksmey (rocketraksi):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |

## Week 7 15.05 - 21.05
### Lukas (lguebeli):
- general: Wrote Test for all the new REST Mappings
- As well as the following issues:

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |

### Simon (SimonHafner):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |


### Yannik (YRiederer):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |


### Raksmey (rocketraksi):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |

## Week 8 22.05 - 28.05
### Lukas (lguebeli):
- general: Wrote Test for all the new REST Mappings
- As well as the following issues:

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |

### Simon (SimonHafner):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |


### Yannik (YRiederer):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |


### Raksmey (rocketraksi):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |
