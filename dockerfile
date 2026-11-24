FROM docker.io/library/maven:3.9-eclipse-temurin-26@sha256:b2c1ad85954592f9928e84327c65201f308ad9b5d8ed7d5b823717c97bf23fbb AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B package

FROM gcr.io/distroless/java25-debian13:nonroot@sha256:4618e7a1a1f5177222985347e1ffd503076f6be5837b89c8fd02800bd8551bde

# No shell in distroless: extra JVM flags go in JAVA_TOOL_OPTIONS, which the JVM reads itself.
ARG JAVA_OPTS=""
ENV JAVA_TOOL_OPTIONS=$JAVA_OPTS

WORKDIR /app
COPY --from=builder /app/target/*-jar-with-dependencies.jar /app/quotesrest.jar

EXPOSE 80

ENTRYPOINT ["java", "-jar", "/app/quotesrest.jar"]
