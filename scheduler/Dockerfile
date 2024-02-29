FROM eclipse-temurin:21.0.2_13-jre-alpine

COPY build/libs/maps-scheduler.jar /maps-scheduler.jar

ENV JAVA_OPTS="-Xms20m -Xmx20m"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /maps-scheduler.jar"]