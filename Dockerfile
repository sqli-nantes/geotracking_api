FROM openjdk:8

ENV MONGO_URI=mongodb://mongo-host:27017
ENV MONGO_DB=geotracking-db

RUN useradd -ms /bin/bash web-admin

COPY / /opt/api-src
RUN chown -R web-admin /opt/api-src

WORKDIR /opt/api-src
EXPOSE 8080

USER web-admin

CMD ["./gradlew", "run"]