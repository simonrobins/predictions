FROM openjdk:8-alpine

COPY target/uberjar/predictions.jar /predictions/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/predictions/app.jar"]
