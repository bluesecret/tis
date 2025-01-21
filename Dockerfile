FROM openjdk:17-jdk-slim
WORKDIR /app
COPY *.jar app.jar
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED", "-jar", "app.jar"]