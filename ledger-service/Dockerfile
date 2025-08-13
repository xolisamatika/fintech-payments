FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew :ledger-service:bootJar --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/ledger-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
