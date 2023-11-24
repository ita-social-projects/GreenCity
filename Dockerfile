FROM openjdk:21.0.1-jre as runner
WORKDIR runner
COPY **/target/app.jar runner/
CMD java -jar runner/app.jar 
