# GreenCity

## About the project

The main aim of “GreenCity” project is to teach people in a playful and challenging way to have an eco-friendly lifestyle. A user can view on the map places that have some eco-initiatives or suggest discounts for being environmentally aware (for instance, coffee shops that give a discount if a customer comes with their own cup). А user can start doing an environment-friendly habit and track their progress with a habit tracker.

## Where to find front-end part of the project

Here is the front-end part of our project: https://github.com/ita-social-projects/GreenCityClient.

`dev` branch of the back-end corresponds to `dev` branch on the front-end. The same thing with `master` branches.

## How to contribute

You're encouraged to contribute to our project if you've found any issues or missing functionality that you would want to see. Here you can see [the list of issues](https://github.com/ita-social-projects/GreenCity/issues) and here you can create [a new issue](https://github.com/ita-social-projects/GreenCity/issues/new).

Before sending any pull request, please discuss requirements/changes to be implemented using an existing issue or by creating a new one. All pull requests should be done into `dev` branch.

Though there are two GitHub projects ([GreenCity](https://github.com/ita-social-projects/GreenCity) for back-end part and [GreenCityClient](https://github.com/ita-social-projects/GreenCityClient) for front-end part) all of the issues are listed in the first one - [GreenCity](https://github.com/ita-social-projects/GreenCity).

**NOTE: make sure that your code passes checkstyle**.

## Start the project locally

### Required to install

* Java 8
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

3. Go to Editor -> Code Style -> Choose Scheme : Default-> Settings -> Restore Defaults -> Duplicate -> Set the name (e.g GreenCity) -> Settings -> Import Scheme -> CheckStyle Configuration -> and choose configuration file in root of project - `checkstyle.xml` -> OK -> Apply. If you did everything right you can use hotkeys as Ctrl + Alt + L - to formate the code, and Ctrl + Alt + O - to optimize imports.


**P.S. If imports have wrong order after formatting, go to IDE Settings -> Editor -> Java -> Imports -> Import Layout and check whether the order of imports is like this:**

- "import static all others imports",
- "blank line",
- "import all others imports"

**IMPORTANT!** Always before creating pull request, execute command: `mvn checkstyle:check`. Pull requests that do not pass checkstyle will NOT BE approved!



