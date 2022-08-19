FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /home/app
COPY src src
COPY pom.xml pom.xml
RUN ls -al
RUN mvn clean package -DskipTests

FROM gcr.io/distroless/java17
COPY --from=build /home/app/target/GringottsTool-0.0.1-SNAPSHOT.jar /usr/local/lib/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xmx128m","-Xms64m","-jar","/usr/local/lib/app.jar"]
