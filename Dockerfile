FROM eclipse-temurin:21 as runner
WORKDIR runner
COPY **/target/app.jar runner/
CMD java -jar runner/app.jar 
