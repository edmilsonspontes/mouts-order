#FROM openjdk:17-jdk-slim
FROM openjdk:17-jdk

WORKDIR /app

ENV SERVER_PORT=8080

COPY target/order-0.0.1-SNAPSHOT.jar order-api.jar

EXPOSE ${SERVER_PORT}

CMD ["sh", "-c", "java -jar /app/order-api.jar --server.port=${SERVER_PORT}"]
#CMD ["java", "-Xdebug", "-Xrunjdwp:server=y,transport=dt_socket,address=*:5005,suspend=n", "-jar", "/app/order-api.jar"]
