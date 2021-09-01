FROM openjdk:11
WORKDIR /usr/app
COPY ./build/libs/redis-0.0.1-SNAPSHOT.jar /usr/app/
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "redis-0.0.1-SNAPSHOT.jar"]