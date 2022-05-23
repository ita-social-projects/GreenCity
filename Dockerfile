FROM tomcat:9-jdk8-temurin as runner

COPY **/target/*.jar /usr/local/tomcat/webapps/
