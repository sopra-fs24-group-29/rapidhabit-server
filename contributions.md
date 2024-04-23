# Contributions  

## 25.3-07.04 Week 1 & 2 (Including Easter Break)
### Lukas (lguebeli):
- general: Wrote Test for all the new REST Mappings
- As well as the following issues:

| IssueNr | Issue description                                   | Assigned Test                                                                                                                                                                                       |
|:--------|-----------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| #16     | Create User-Objects in Database                     | - POST Mapping "/users" - CODE 204 CREATED (Pass)  <br> - POST Mapping "/users" - CODE 409 CONFLICT (error)                                                                                         |
| #17     | Implement API Endpoint for fetching Assigned Groups | - GET Mapping "/groups" - CODE 200 OK (Pass)  <br> - GET Mapping "/groups" - CODE 401 Unaurthorized (Error)                                                                                         |
| #19     | Implement API Endpoints for Password Change         | - PUT Mapping "/users/password" - CODE 204 NoContent (pass)  <br> - PUT Mapping "/users/password" - CODE 404 NotFound (error)  <br> - PUT Mapping "/users/password" - CODE 401 Unauthorized (error) |
| #56     | Implement logout functionality                      | - PUT Mapping "/users/logout" - CODE 200 OK (pass)   <br> - PUT Mapping "/users/logout" - CODE 404 NotFound (error)                                                                                                                                                                                                |

### Simon (SimonHafner):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |


### Yannik (YRiederer):

| IssueNr | Issue description |
|:--------|-------------------|
| #4      | Create and configure App routing                 |
| #31     | Create Not Found screen                  |
| #32     | Create Welcome Screen                  |
| #33       |   Create login screen                |               |
| #34       |  Create Register Screen                 |               |



### Raksmey (rocketraksi):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |


## Week 3  08.04 - 14.04
### Lukas (lguebeli):
- general: Wrote Test for all the new REST Mappings for example for Issue #35
- As well as the following issues:

| IssueNr | Issue description                                                   | Assigned Test                                                                                                                                                                                                      |
|:--------|---------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| #21     | Group administrators can update a group name and group description. | - PUT Mapping "/groups/{groupId}" - CODE 200 OK (Pass)  <br> - PUT Mapping "/groups/{groupId}" - CODE 404 NotFound (Pass)  <br> - PUT Mapping "/groups/{groupId}" - CODE 401 Unauthorized (Error)                  |
| #89     | Implement API for Account Deletion                                  | - DELETE Mapping "/users/ID" - CODE 204 No Content (pass)  <br> - DELETE Mapping "/users/ID" - CODE 404 NotFound (error)  <br> - DELETE Mapping "/users/ID" - CODE 401 Unauthorized (error)                        |
| #91     | Users can change their profiledata (eg. Email, first-/lastname)     | - PUT Mapping "/users/update" - CODE 204 No Content (pass)  <br>  - PUT Mapping "/users/update" - CODE 404 Not Found (error)  <br>  - PUT Mapping "/users/update" - CODE 401 Unauthorized (error)                                                                                        |
| #62     | Implement API Endpoint for Group Deletion                           | - DELETE Mapping "/groups/{groupId}" - CODE 204 NoContent (Pass)  <br> - DELETE Mapping "/groups/{groupId}" - CODE 404 NotFound (Error)  <br> - DELETE Mapping "/groups/{groupId}" - CODE 401 Unauthorized (Error) |

### Simon (SimonHafner):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |


### Yannik (YRiederer):

| IssueNr | Issue description |
|:--------|-------------------|
| #7       | Design and Implement User Profile Page Component and UI                  |
| #8       | Implement password change and account deletion feature                   |
| #20       | Design and Implement Join Group Component and UI                  |


### Raksmey (rocketraksi):

| IssueNr | Issue description | Assigned Test |
|:--------|-------------------|---------------|
| #       |                   |               |
| #       |                   |               |
| #       |                   |               |


## Week 4 15.04 - 21.04
### Lukas (lguebeli):
- general: Wrote Test for all the new REST Mappings
- As well as the following issues:

| IssueNr | Issue description                      | Assigned Test                                                                                                                                                                                                                 |
|:--------|----------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| #51     | Add API to delete users from the group | - DELETE Mapping "/groups/{groupId}/users" - CODE 204 NoContent  <br> - DELETE Mapping "/groups/{groupId}/users" - CODE 404 NotFound (Error)  <br> - DELETE Mapping "/groups/{groupId}/users" - CODE 401 Unauthorized (Error) |
| #93     | Created Test for this issue            | - GET Mapping "/groups/{groupId}/habits" - CODE 200 Ok (pass)                                                                                                                                                                 |
| #94     | Created Test for this issue            | - GET Mapping "/groups/{groupId}/habits/{habitId}" - CODE 200 Ok (pass)  <br> - GET Mapping "/groups/{groupId}/habits/{habitId}" - CODE 404 Not Found (Error)                                                                 |

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


## Week 5 22.04 - 28.04
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
