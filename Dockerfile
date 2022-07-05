FROM openjdk:11.0.15-jre as runner
WORKDIR runner
COPY **/target/app.jar runner/
CMD java -jar runner/app.jar 
