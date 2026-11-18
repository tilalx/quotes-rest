FROM docker.io/library/maven:3.9.16-eclipse-temurin-25@sha256:93b8a14ea2f412782e4e842651273b4d903e35cc496284f178fbbe2d67d00976 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B package

FROM gcr.io/distroless/java25-debian13:nonroot@sha256:1c4ced761d17961c900cba60c33c8c3c46edbdadaa9d5ef9806eb5a6824fd406

# No shell in distroless: extra JVM flags go in JAVA_TOOL_OPTIONS, which the JVM reads itself.
ARG JAVA_OPTS=""
ENV JAVA_TOOL_OPTIONS=$JAVA_OPTS

WORKDIR /app
COPY --from=builder /app/target/*-jar-with-dependencies.jar /app/quotesrest.jar

EXPOSE 80

ENTRYPOINT ["java", "-jar", "/app/quotesrest.jar"]
