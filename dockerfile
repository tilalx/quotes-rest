FROM openjdk:17-jdk
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/quotes-1.0-jar-with-dependencies.jar quotesrest.jar
EXPOSE 80
ENTRYPOINT exec java $JAVA_OPTS -jar quotesrest.jar