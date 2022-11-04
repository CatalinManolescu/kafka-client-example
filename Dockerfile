FROM amazoncorretto:17

ENV SPRING_CONFIG_LOCATION=classpath:/application.yml,file:/opt/app/config/application.yml,file:/opt/app/secrets/secrets.yml

COPY target/kafka-client-example.jar /opt/app/

WORKDIR /opt/app

CMD ["java", "-jar", "kafka-client-example.jar"]