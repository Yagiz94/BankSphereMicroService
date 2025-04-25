FROM eclipse-temurin:17-jdk-jammy
LABEL authors="yagizgani"
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV JAVA_OPTS "-Xms256m -Xmx512m"
EXPOSE 8081
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app.jar"]
