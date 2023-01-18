package ccm.example.kafka.services;

import ccm.example.kafka.KafkaExampleProperties;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import javax.annotation.PreDestroy;
import java.time.format.DateTimeFormatter;

@Component
public class GenericReactorKafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(GenericReactorKafkaProducer.class);

    private final KafkaExampleProperties kafkaExampleProperties;

    private final KafkaSender<String, String> sender;
    private final DateTimeFormatter dateFormat;

    public GenericReactorKafkaProducer(KafkaExampleProperties kafkaExampleProperties) {
        this.kafkaExampleProperties = kafkaExampleProperties;

        SenderOptions<String, String> senderOptions = SenderOptions.create(
                kafkaExampleProperties.getKafka().buildProducerProperties());

        sender = KafkaSender.create(senderOptions);
        dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS z dd MMM yyyy");
    }


    public void sendMessage(String topic, String key, String message) {
        sender.createOutbound().send(Mono.just(SenderRecord.create(new ProducerRecord<>(topic, key, message), key)))
                .then()
                .doOnError(e -> log.error("Kafka message send failed.", e))
                .doOnSuccess(s -> {
                    log.trace("Sent message = [{}][{}]{}", topic, key, message);
                })
                .subscribe();
    }
    @PreDestroy
    void shutdown() {
        sender.close();
    }
}
