FROM openjdk:11

WORKDIR /opt

ENV LC_ALL=C
ENV MONGO_URI=mongodb://localhost:27017/admin
ENV RABBIT_HOST=localhost
ENV RABBIT_PORT=5672
ENV SCHEMA_BASE_URI=https://schema.humancellatlas.org

#JMX related variables
ENV JMX_PORT=9091
ENV RMI_HOSTNAME=localhost

ADD gradle ./gradle
ADD src ./src
ADD lombok.config ./lombok.config

COPY gradlew build.gradle ./

RUN ./gradlew --no-daemon  assemble

CMD java \
    -Djava.security.egd=file:/dev/./urandom \
    -jar build/libs/*.jar \
    --spring.data.mongodb.uri=$MONGO_URI \
    --spring.rabbitmq.host=$RABBIT_HOST \
    --spring.rabbitmq.port=$RABBIT_PORT \
    --schema.base-uri=$SCHEMA_BASE_URI

