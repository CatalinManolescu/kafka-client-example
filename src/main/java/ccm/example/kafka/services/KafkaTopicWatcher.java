package ccm.example.kafka.services;

import ccm.example.kafka.KafkaExampleProperties;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;

@Component
public class KafkaTopicWatcher {
    private static final Logger log = LoggerFactory.getLogger(KafkaTopicWatcher.class);

    private final KafkaExampleProperties kafkaExampleProperties;
    private final String consumerGroupId;
    private final Consumer<String, String> consumer;
    private final ExecutorService executor;
    private final AtomicBoolean running;

    public KafkaTopicWatcher(KafkaExampleProperties kafkaExampleProperties) {
        this.kafkaExampleProperties = kafkaExampleProperties;

        Map<String, Object> kafkaConsumerPropertiesCopy = new HashMap<>(kafkaExampleProperties.getKafka().buildConsumerProperties());

        this.consumerGroupId = getUniqueConsumerGroupId(kafkaConsumerPropertiesCopy);
        kafkaConsumerPropertiesCopy.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        //kafkaConsumerPropertiesCopy.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        kafkaConsumerPropertiesCopy.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StringDeserializer deserializer = new StringDeserializer();
        this.consumer = new KafkaConsumer<>(kafkaConsumerPropertiesCopy, deserializer, deserializer);

        this.executor = Executors.newSingleThreadExecutor();
        this.running = new AtomicBoolean(true);
    }

    @PostConstruct
    void subscribeAndGetMessage() throws InterruptedException {
        subscribeToTopic(kafkaExampleProperties.getKafka().getWatchTopics());

        executor.submit(() -> {
            while (running.get()) {
                ConsumerRecords<String, String> records = consumer.poll(kafkaExampleProperties.getKafka().getConsumerPoolTimeout());
                StreamSupport.stream(records.spliterator(), false)
                        .forEach(record -> log.info("Read message: {}",record.value()));
            }
        });
    }

    @PreDestroy
    void shutdown() {
        running.set(false);
        executor.shutdownNow();
        consumer.close();
    }

    private String getUniqueConsumerGroupId(Map<String, Object> kafkaConsumerProperties) {
        try {
            if (kafkaExampleProperties.getKafka().getConsumerUniqueId()) {
                return kafkaExampleProperties.getKafka().getConsumerGroup() + "-" +  InetAddress.getLocalHost().getHostName();
            }
            return kafkaExampleProperties.getKafka().getConsumerGroup();
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        }
    }

    private void subscribeToTopic(ArrayList<String> topics) throws InterruptedException {

        final CountDownLatch subscribed = new CountDownLatch(1);

        String stringArray = topics.toString();

        stringArray = stringArray.replace("[", "")
                .replace("]", "")
                .replace(" ", "");
        log.info("Subscribe to topics={}", stringArray);

        consumer.subscribe(topics, new ConsumerRebalanceListener() {

            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                // nothing to do her
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                log.debug("Got partitions = {}", partitions);

                if (!partitions.isEmpty()) {
                    subscribed.countDown();
                }
            }
        });

//        consumer.poll(kafkaExampleProperties.getKafka().getConsumerPoolTimeout());
//        if (!subscribed.await(kafkaExampleProperties.getKafka().getConsumerSubscriptionTimeout().toMillis(), MILLISECONDS)) {
//            throw new BeanInitializationException("Subscription to kafka failed, topic=" + stringArray);
//        }
    }
}
