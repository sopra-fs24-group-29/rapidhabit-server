# SoPra FS24 Gruppe 29 - RapidTracker

## Introduction
Maintaining motivation while forming good habits and defeating bad ones can sometimes be a challenge. Often, individuals struggle to establish and sustain good habits due to a lack of support and accountability. Our solution is a collaborative habit tracking app that relies on communal support to foster the motivation. It allows users to form groups to collectively set, monitor, and achieve habitual goals.If all goals are achieved by each member, the group receives streaks. And to increase competition within a given group, every member can earn reward points for each habit completed. This approach harnesses the power of gamification and social accountability to make habit formation more engaging and successful. The aim of the application is to help its users to establish healthy habit routines while relying on the positive effects of group dynamics and support networks.

## Technologies
### Backend
- WIP

### Frontend
#### Vite (instead of Create React App):
Vite is a modern front-end build tool that enhances development efficiency by utilizing native ES modules, which enable instant server starts and real-time updates through hot module replacement.

It outperforms Create React App with significantly faster server start-up times and avoids unnecessary re-bundling. Additionally, its flexible configuration and efficient Rollup-based production builds make Vite an ideal choice for streamlined and high-performance web development.

#### Tailwind CSS:
Tailwind CSS is a utility-first framework that facilitates rapid UI development by allowing custom designs directly in HTML using predefined classes. It enhances responsiveness and reduces the need for custom CSS, significantly speeding up the development process and simplifying maintenance.

#### React with Typescript:
React, combined with strict TypeScript, is a powerful setup for building scalable and maintainable web applications, providing a component-based architecture along with static type checking. This integration enhances development efficiency and reliability by catching errors early in the development process and offering more predictable code behavior.

#### PWA (as a extra mile):
A Progressive Web App PWA built with React utilizes React's component-based architecture to create applications that behave like native apps but are delivered via the web.


## High-level components
- WIP 

## Launch & Deployment
Before you can start our applications you need to do some steps for the Backend as well as for the frontend as explained below.

### Backend Setup:
#### Getting started with Spring Boot
-   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
-   Guides: http://spring.io/guides
    -   Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
    -   Building REST services with Spring: https://spring.io/guides/tutorials/rest/

#### Setup this Template with your IDE of choice
Download your IDE of choice (e.g., [IntelliJ](https://www.jetbrains.com/idea/download/), [Visual Studio Code](https://code.visualstudio.com/), or [Eclipse](http://www.eclipse.org/downloads/)). Make sure Java 17 is installed on your system (for Windows, please make sure your `JAVA_HOME` environment variable is set to the correct version of Java).

#### IntelliJ
If you consider to use IntelliJ as your IDE of choice, you can make use of your free educational license [here](https://www.jetbrains.com/community/education/#students).
1. File -> Open... -> SoPra server template
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`

#### VS Code
The following extensions can help you get started more easily:
-   `vmware.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs24` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

#### Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

#### Build

```bash
./gradlew build
```

#### Run

```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.

#### Test

```bash
./gradlew test
```

#### Development Mode
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

#### API Endpoint Testing with Postman
We recommend using [Postman](https://www.getpostman.com) to test your API Endpoints.

#### Debugging
If something is not working and/or you don't know what is going on. We recommend using a debugger and step-through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command), do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug "Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

#### Testing
Have a look here: https://www.baeldung.com/spring-boot-testing


### Frontend setup:

#### Getting started with Yarn
We use Yarn instead of npm because it provides faster, more reliable package installations.

-   Documentation: https://yarnpkg.com/advanced/error-codes
-   Guides: https://yarnpkg.com/getting-started

#### Setting up the environment
##### 1. Install yarn
```bash
npm install --global yarn
```

##### 2. Install dependencies
```bash
yarn
```

##### 3. Run the development server
```bash
yarn dev
```

## Illustrations
- WIP

## Roadmap
If we were to continue working on our application after the module, or if a new developer were to join us, we would first add the following features:

### Feature 1 - Adding pre-defined Habits
First, we would create pre-defined habits in the database, which could then be selected by new group administrators when creating new habits. This would support inexperienced users when using the application for the first time and make it easier to get started.

### Feature 2 - Adding public groups
Public groups could also be created to network with other people with similar goals and improve their own performance together

We deliberately did not include this feature in our project because we did not want to lose any time or take any avoidable risks due to possible misuse of the application by third parties.

### Feature 3 - Implementing push notifications
Another step forward would be to receive push notifications for important events. an example of this could be when the pulse check is started, when a new user joins the group or when the AI coach gives feedback on their own habits.

### Feature 4 - Adding color to groups
A relatively small but very useful update would be to add a color to each group. This could provide a better overview on the group dashboard and improve the overall user experience of the application.

### Feature 5 - Changing the authorization principle
The authorization principle could also be adapted. currently, only the admin of a group can manage the habits and group members. Here it could be considered whether the admin can appoint further admins, or whether the authorizations should be broken down further in order to separate the administration of the habits and the group members.

## Authors
- [Lukas Gübeli](https://github.com/lguebeli) (BackEnd Developer)
- [Yannik Riederer](https://github.com/YRiederer) (FrontEnd Developer)
- [Simon Hafner](https://github.com/SimonHafner) (BackEnd Developer)
- [Raksmey Oum](https://github.com/rocketraksi) (FrontEnd Developer)


## Acknowledgement
We'd like to thank [Sijing Qin](https://github.com/SerendipitousJourney), who has been our personal Tutor during the last three months and has provided us with the required guidance and expertise. 

## License
This project is licensed under the GNU GPLv3 License - see the [LICENSE.md](LICENSE) file for details