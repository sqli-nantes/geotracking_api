FROM openjdk:8

ENV MONGO_URI=mongodb://mongo-host:27017
ENV MONGO_DB=geotracking-db

WORKDIR /opt/api-src
EXPOSE 8080

CMD ["./gradlew", "run"]