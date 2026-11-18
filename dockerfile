FROM docker.io/library/maven:3.9-eclipse-temurin-26@sha256:b2c1ad85954592f9928e84327c65201f308ad9b5d8ed7d5b823717c97bf23fbb AS builder

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
