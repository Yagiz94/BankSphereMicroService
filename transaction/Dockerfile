FROM eclipse-temurin:17-jdk-jammy
LABEL authors="yagizgani"
# install curl so that the healthcheck command will work
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV JAVA_OPTS "-Xms256m -Xmx512m"
EXPOSE 8083
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app.jar"]
