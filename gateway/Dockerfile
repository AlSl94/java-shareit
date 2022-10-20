FROM amazoncorretto:11-al2-jdk
ENV TZ=Europe/Moscow
COPY target/*.jar shareit-gateway.jar
ENTRYPOINT ["java","-jar","/shareit-gateway.jar"]