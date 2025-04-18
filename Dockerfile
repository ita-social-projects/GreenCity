FROM openjdk:21-slim as runner
WORKDIR runner
COPY **/target/app.jar runner/
CMD java -jar runner/app.jar 
