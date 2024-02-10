FROM eclipse-temurin:21.0.2_13-jre-alpine

COPY build/libs/app.jar /app.jar
COPY src/main/resources/airports.csv /airports.csv

ENV JAVA_OPTS="-Xms250m -Xmx250m"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]