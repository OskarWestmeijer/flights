FROM eclipse-temurin:17.0.9_9-jre-alpine

COPY build/libs/app.jar /app.jar
ENV JAVA_OPTS="-Xms250m -Xmx250m"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]