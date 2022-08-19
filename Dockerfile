FROM gcr.io/distroless/java17
COPY GringottsTool.jar /usr/local/lib/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xmx128m","-Xms64m","-jar","/usr/local/lib/app.jar"]
