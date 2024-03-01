FROM eclipse-temurin:21.0.2_13-jre-alpine

COPY build/libs/maps-scheduler.jar /maps-scheduler.jar

ENV JAVA_OPTS="-Xms60m -Xmx60m"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /maps-scheduler.jar"]