FROM java:8-jdk

RUN apt-get update

VOLUME /root/.m2

COPY . .
EXPOSE 9020

CMD ./gradlew bootRun