FROM eclipse-temurin:25.0.1_8-jre-alpine-3.22

COPY build/libs/app.jar /app.jar
COPY src/main/resources/airports.csv /airports.csv

ENV JAVA_OPTS="-Xms250m -Xmx250m"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]