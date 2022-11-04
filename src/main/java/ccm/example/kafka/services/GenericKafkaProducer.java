package ccm.example.kafka.services;

import ccm.example.kafka.KafkaExampleProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
public class GenericKafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(GenericKafkaProducer.class);

    private final KafkaExampleProperties kafkaExampleProperties;
    private final Producer<String, String> producer;

    public GenericKafkaProducer(KafkaExampleProperties kafkaExampleProperties) {
        this.kafkaExampleProperties = kafkaExampleProperties;
        StringSerializer serializer = new StringSerializer();
        this.producer = new KafkaProducer<>(kafkaExampleProperties.getKafka().buildProducerProperties(),
                serializer, serializer);
    }

    @PreDestroy
    void shutdown() {
        producer.close();
    }

    public void sendMessage(String topic,String key, String message) {
        try {
            sendKafkaMessage(topic, key, message);
        } catch (ExecutionException e) {
            log.warn("Kafka send message execution failed.", e);
        } catch (TimeoutException | InterruptedException e) {
            log.warn("Kafka send message  timed out.", e);
        } catch (RejectedExecutionException e) {
            log.debug("Ignore send message , already running...");
        }
    }

    private void sendKafkaMessage(String topic, String key, String message) throws InterruptedException, ExecutionException, TimeoutException {
        if (Objects.isNull(key)) {
            key = message;
        }

        if (Objects.isNull(message)) {
            throw new NullPointerException("Message cannot be null");
        }

        producer.send(new ProducerRecord<>(topic, key, message))
                .get(kafkaExampleProperties.getKafka().getProducerWriteTimeout().toMillis(), MILLISECONDS);
        // in real use case this should be set to TRACE
        log.trace("Sent message = [{}][{}]{}", topic, key, message);
    }
}
