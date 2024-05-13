# SoPra FS24 Gruppe 29 - RapidTracker
## Table of contents

- [Introduction](#introduction)
- [Technologies](#technologies)
- [High-level components](#high-level-components)
   * [Users and Groups](#users-and-groups)
   * [Habits](#habits)
   * [Groupchat](#groupchat)
   * [Pulse check](#pulse-check)
   * [AI coach](#ai-coach)
- [Roadmap](#roadmap)
   * [Feature 1 - Adding pre-defined Habits](#feature-1-adding-pre-defined-habits)
   * [Feature 2 - Adding public groups](#feature-2-adding-public-groups)
   * [Feature 3 - Implementing push notifications](#feature-3-implementing-push-notifications)
   * [Feature 4 - Adding color to groups](#feature-4-adding-color-to-groups)
   * [Feature 5 - Changing the authorization principle](#feature-5-changing-the-authorization-principle)
- [Launch & Deployment](#launch-deployment)
   * [Getting started with Spring Boot](#getting-started-with-spring-boot)
   * [Setup this Template with your IDE of choice](#setup-this-template-with-your-ide-of-choice)
   * [IntelliJ](#intellij)
   * [VS Code](#vs-code)
   * [Building with Gradle](#building-with-gradle)
   * [Build](#build)
   * [Run](#run)
   * [Test](#test)
   * [Development Mode](#development-mode)
   * [API Endpoint Testing with Postman](#api-endpoint-testing-with-postman)
   * [Debugging](#debugging)
   * [Testing](#testing)
- [Authors](#authors)
- [Acknowledgement](#acknowledgement)
- [License](#license)

<!-- TOC end -->

<!-- TOC --><a name="introduction"></a>
## Introduction
Maintaining motivation while forming good habits and defeating bad ones can sometimes be a challenge. Often, individuals struggle to establish and sustain good habits due to a lack of support and accountability. Our solution is a collaborative habit tracking app that relies on communal support to foster the motivation. It allows users to form groups to collectively set, monitor, and achieve habitual goals.If all goals are achieved by each member, the group receives streaks. And to increase competition within a given group, every member can earn reward points for each habit completed. This approach harnesses the power of gamification and social accountability to make habit formation more engaging and successful. The aim of the application is to help its users to establish healthy habit routines while relying on the positive effects of group dynamics and support networks.

<!-- TOC --><a name="technologies"></a>
## Technologies
- Springboot
- Gradle
- MongoDb
- Google Cloud
- Sonarcloud

<!-- TOC --><a name="high-level-components"></a>
## High-level components
<!-- TOC --><a name="users-and-groups"></a>
### [Users](src/main/java/ch/uzh/ifi/hase/soprafs24/entity/User.java) and [Groups](src/main/java/ch/uzh/ifi/hase/soprafs24/entity/Group.java)
- Role: A user can create a group in which administrators are automatically added. They can then invite other people to join them and complete the habit together.
- Relation: The habits can be created and managed in a group.

<!-- TOC --><a name="habits"></a>
### [Habits](src/main/java/ch/uzh/ifi/hase/soprafs24/entity/Habit.java)
- Role: Habits can be created with a name, a description, a specific repeat strategy (daily, or on certain days of the week), and a score to be obtained by achieving the habit. The habits can then be checked on/off by all group members.
- Relation: The habits are group-specific and not cross-group.

<!-- TOC --><a name="groupchat"></a>
### [Groupchat](src/main/java/ch/uzh/ifi/hase/soprafs24/service/ChatRoomService.java)
- Role: So that users can support and motivate each other, there is a group chat in which messages can be sent.
- Relation: The group chat is created directly when a group is created and also loads a certain number of past messages each time so that new group members can also read the messages.

<!-- TOC --><a name="pulse-check"></a>
### [Pulse check](src/main/java/ch/uzh/ifi/hase/soprafs24/service/PulseCheckEntryService.java)
- Role: Once a day, a survey should be sent to group members asking them to assess their state of mind. This is then evaluated and feedback is returned to the group.
- Relation: The meetings are group-specific and not cross-group. They also serve to motivate and network the group.

<!-- TOC --><a name="ai-coach"></a>
### [AI coach](src/main/java/ch/uzh/ifi/hase/soprafs24/service/OpenAIService.java)
- Role: Twice a day, the AI coach gives users tips on how they can improve their everyday life to achieve the habits even better in the future.
- Relation: The messages are sent to all users via feed, but are of course highly individualized and further support the user in achieving their goals.

<!-- TOC --><a name="roadmap"></a>
## Roadmap
If we were to continue working on our application after the module, or if a new developer were to join us, we would first add the following features:

<!-- TOC --><a name="feature-1-adding-pre-defined-habits"></a>
### Feature 1 - Adding pre-defined Habits
First, we would create pre-defined habits in the database, which could then be selected by new group administrators when creating new habits. This would support inexperienced users when using the application for the first time and make it easier to get started.

<!-- TOC --><a name="feature-2-adding-public-groups"></a>
### Feature 2 - Adding public groups
Public groups could also be created to network with other people with similar goals and improve their own performance together

We deliberately did not include this feature in our project because we did not want to lose any time or take any avoidable risks due to possible misuse of the application by third parties.

<!-- TOC --><a name="feature-3-implementing-push-notifications"></a>
### Feature 3 - Implementing push notifications
Another step forward would be to receive push notifications for important events. an example of this could be when the pulse check is started, when a new user joins the group or when the AI coach gives feedback on their own habits.

<!-- TOC --><a name="feature-4-adding-color-to-groups"></a>
### Feature 4 - Adding color to groups
A relatively small but very useful update would be to add a color to each group. This could provide a better overview on the group dashboard and improve the overall user experience of the application.

<!-- TOC --><a name="feature-5-changing-the-authorization-principle"></a>
### Feature 5 - Changing the authorization principle
The authorization principle could also be adapted. currently, only the admin of a group can manage the habits and group members. Here it could be considered whether the admin can appoint further admins, or whether the authorizations should be broken down further in order to separate the administration of the habits and the group members.

<!-- TOC --><a name="launch-deployment"></a>
## Launch & Deployment
Before you can start our applications you need to do some steps for the Backend as well as for the frontend as explained below.

<!-- TOC --><a name="getting-started-with-spring-boot"></a>
### Getting started with Spring Boot
-   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
-   Guides: http://spring.io/guides
    -   Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
    -   Building REST services with Spring: https://spring.io/guides/tutorials/rest/

<!-- TOC --><a name="setup-this-template-with-your-ide-of-choice"></a>
### Setup this Template with your IDE of choice
Download your IDE of choice (e.g., [IntelliJ](https://www.jetbrains.com/idea/download/), [Visual Studio Code](https://code.visualstudio.com/), or [Eclipse](http://www.eclipse.org/downloads/)). Make sure Java 17 is installed on your system (for Windows, please make sure your `JAVA_HOME` environment variable is set to the correct version of Java).

<!-- TOC --><a name="intellij"></a>
### IntelliJ
If you consider to use IntelliJ as your IDE of choice, you can make use of your free educational license [here](https://www.jetbrains.com/community/education/#students).
1. File -> Open... -> SoPra server template
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`

<!-- TOC --><a name="vs-code"></a>
### VS Code
The following extensions can help you get started more easily:
-   `vmware.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs24` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

<!-- TOC --><a name="building-with-gradle"></a>
### Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

<!-- TOC --><a name="build"></a>
### Build

```bash
./gradlew build
```

<!-- TOC --><a name="run"></a>
### Run

```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.

<!-- TOC --><a name="test"></a>
### Test

```bash
./gradlew test
```

<!-- TOC --><a name="development-mode"></a>
### Development Mode
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

<!-- TOC --><a name="api-endpoint-testing-with-postman"></a>
### API Endpoint Testing with Postman
We recommend using [Postman](https://www.getpostman.com) to test your API Endpoints.

<!-- TOC --><a name="debugging"></a>
### Debugging
If something is not working and/or you don't know what is going on. We recommend using a debugger and step-through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command), do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug "Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

<!-- TOC --><a name="testing"></a>
### Testing
Have a look here: https://www.baeldung.com/spring-boot-testing

<!-- TOC --><a name="authors"></a>
## Authors
- [Lukas Gübeli](https://github.com/lguebeli) (BackEnd Developer)
- [Yannik Riederer](https://github.com/YRiederer) (FrontEnd Developer)
- [Simon Hafner](https://github.com/SimonHafner) (BackEnd Developer)
- [Raksmey Oum](https://github.com/rocketraksi) (FrontEnd Developer)

See also the list of [contributors](contributions.md) who participated in this project.

<!-- TOC --><a name="acknowledgement"></a>
## Acknowledgement
We'd like to thank [Sijing Qin](https://github.com/SerendipitousJourney), who has been our personal Tutor during the last three months and has provided us with the required guidance and expertise.

<!-- TOC --><a name="license"></a>
## License
This project is licensed under the GNU GPLv3 License - see the [LICENSE.md](LICENSE) file for details
