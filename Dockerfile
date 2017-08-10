FROM openjdk:8

ENV MONGO_URI=mongodb://mongo-host:27017
ENV MONGO_DB=geotracking-db

ARG SRC_PATH=/opt/api-src

RUN useradd -ms /bin/bash web-admin

COPY / $SRC_PATH
RUN chown -R web-admin $SRC_PATH \
    && chgrp -R 0 $SRC_PATH  \
    && chmod -R g+rw $SRC_PATH
    

WORKDIR $SRC_PATH
EXPOSE 8080

USER web-admin

CMD ["./gradlew", "run"]