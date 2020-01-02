# GreenCity

## About the project

The main aim of “GreenCity” project is to teach people in a playful and challenging way to have an eco-friendly lifestyle. A user can view on the map places that have some eco-initiatives or suggest discounts for being environmentally aware (for instance, coffee shops that give a discount if a customer comes with their own cup). Also, a user can start doing an environment-friendly habit/goal and track their progress.

## Start the project locally

### Required to install

* Java 8
* maven 3.6.0 or higher
* PostgreSQL 9.5 or higher

### How to run

1. You should create environmental variables that are defined in `application-dev.properties`.

2. You should create database `greencity`.

3. All these variables you can set in Intellij Idea. For instance,

   ```properties
   spring.datasource.url=${DATASOURCE_URL}
   spring.datasource.username=${DATASOURCE_USER}
   spring.datasource.password=${DATASOURCE_PASSWORD}
   spring.mail.username=${EMAIL_ADDRESS}
   spring.mail.password=${EMAIL_PASSWORD}
   ```

   ![env-vars](env-example.png)

## Setup Checkstyle to your Intellij Idea

1. Download Checkstyle plugin, **version 8.22**, for your IDE. Go to - Other Settings/Checkstyle. Click on add configuration File -> Browse -> and choose configuration file in root of project - `checkstyle.xml` -> OK. Also add description for this Checkstyle file, e.g. "GreenCity".

2. Click to activate this Configuration File -> Apply.

3. Go to Editor -> Code Style -> Choose Scheme : Default-> Settings -> Restore Defaults -> Duplicate -> Set the name (e.g GreenCity) -> Settings -> Import Scheme -> CheckStyle Configuration -> and choose configuration file in root of project - `checkstyle.xml` -> OK -> Apply. If you did everything right you can use hotkeys as Ctrl + Alt + L - for Formatting code, and Ctrl + Alt + O - for Optimizing imports.

4. Set running checkstyle before building if for running project you use graphic interface of IDE(green RUN button on top of IDE)

5. Go to Edit Configurations... of running project -> GreenCityApplication -> Menu on bottom of settings("Before launch") -> Plus -> "Run Maven Goal" -> Command line : checkstyle:check -> OK -> OK

Also if you just want to check project you can use command: `mvn checkstyle:check`

**P.S.** **If imports have wrong order after formatting, check in IDE Settings -> Editor -> Java -> Imports -> Import Layout, and check there order of imports, it should be like this:**

- "import static all others imports",
- "blank line",
- "import all others imports"

If your comments symbols "//" plased at first column of line, and our checkstyle is not OK with that than go to: Settings -> Code Style -> Java -> Code Generation -> and remove mark at "Line comment at first column".

**IMPORTANT!** Always before creating pull request execute command: `mvn checkstyle:check`. Pull requests that do not pass checkstyle will NOT BE approved!

## Deployment notes

Deployment process file link: https://github.com/SoftServe-Social-Projects/GreenCity/wiki/Deployment-Process

