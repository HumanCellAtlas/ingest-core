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

#Security variables
ENV AUTH_ISSUER=https://humancellatlas.auth0.com/
ENV SVC_AUTH_AUDIENCE=https://dev.data.humancellatlas.org/
ENV USR_AUTH_AUDIENCE=https://dev.data.humancellatlas.org/
ENV GCP_JWK_PROVIDER_BASE_URL=https://www.googleapis.com/service_accounts/v1/jwk/
ENV SECURITY_WHITELIST_DOMAINS=ebi.ac.uk
ENV GCP_PROJECT_WHITELIST=hca-dcp-production.iam.gserviceaccount.com,human-cell-atlas-travis-test.iam.gserviceaccount.com,broad-dsde-mint-dev.iam.gserviceaccount.com,broad-dsde-mint-test.iam.gserviceaccount.com,broad-dsde-mint-staging.iam.gserviceaccount.com

ADD gradle ./gradle
ADD src ./src

COPY gradlew build.gradle ./

RUN ./gradlew --no-daemon  assemble

CMD java \
    -Djava.security.egd=file:/dev/./urandom \
    -jar build/libs/*.jar \
    --spring.data.mongodb.uri=$MONGO_URI \
    --spring.rabbitmq.host=$RABBIT_HOST \
    --spring.rabbitmq.port=$RABBIT_PORT \
    --schema.base-uri=$SCHEMA_BASE_URI

