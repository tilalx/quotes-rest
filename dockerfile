FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean install

FROM  eclipse-temurin:21-jre
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS

COPY --from=builder /app/target/*-jar-with-dependencies.jar /app/quotesrest.jar

WORKDIR /app

EXPOSE 80

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar quotesrest.jar"]