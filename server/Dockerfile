FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./

RUN chmod +x ./gradlew

COPY src src

RUN ./gradlew clean shadowJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk update && apk upgrade

COPY --from=build /app/build/libs/*-all.jar app.jar

EXPOSE 3407

CMD [ "java", "-jar", "app.jar" ]