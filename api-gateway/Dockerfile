FROM eclipse-temurin:17-jdk-jammy
LABEL authors="yagizgani"
# install curl so that the healthcheck command will work
RUN apt-get update && apt-get install -y netcat  \
 && rm -rf /var/lib/apt/lists/*   \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV JAVA_OPTS "-Xms256m -Xmx512m"
EXPOSE 8080
ENTRYPOINT ["sh","-c","until nc -z redis 6379; do echo '⇢ waiting for redis…'; sleep 2; done; exec java $JAVA_OPTS -jar /app.jar"]
