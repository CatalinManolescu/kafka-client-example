package ccm.example.kafka;

import ccm.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;

@Configuration
public class KafkaExampleProperties {
    @Bean
    public KafkaCustomProperties getKafka() {
        return new KafkaCustomProperties();
    }

    @ConfigurationProperties(prefix = "kafka")
    public static class KafkaCustomProperties extends KafkaProperties {
        public static final Duration PRODUCER_WRITE_TIMEOUT_DEFAULT = Duration.ofSeconds(10);
        public static final Duration CONSUMER_POOL_TIMEOUT_DEFAULT = Duration.ofSeconds(10);
        public static final Duration CONSUMER_SUBSCRIPTION_TIMEOUT_DEFAULT = Duration.ofSeconds(10);

        private ArrayList<String> watchTopics;
        private Duration producerWriteTimeout = PRODUCER_WRITE_TIMEOUT_DEFAULT;
        private Duration consumerPoolTimeout = CONSUMER_POOL_TIMEOUT_DEFAULT;
        private Duration consumerSubscriptionTimeout = CONSUMER_SUBSCRIPTION_TIMEOUT_DEFAULT;
        private Boolean consumerUniqueId = false;

        private String consumerGroup;

        public ArrayList<String> getWatchTopics() {
            return watchTopics;
        }

        public void setWatchTopics(ArrayList<String> watchTopics) {
            this.watchTopics = watchTopics;
        }

        public Duration getProducerWriteTimeout() {
            return producerWriteTimeout;
        }

        public void setProducerWriteTimeout(Duration producerWriteTimeout) {
            this.producerWriteTimeout = producerWriteTimeout;
        }

        public Duration getConsumerPoolTimeout() {
            return consumerPoolTimeout;
        }

        public void setConsumerPoolTimeout(Duration consumerPoolTimeout) {
            this.consumerPoolTimeout = consumerPoolTimeout;
        }

        public Duration getConsumerSubscriptionTimeout() {
            return consumerSubscriptionTimeout;
        }

        public void setConsumerSubscriptionTimeout(Duration consumerSubscriptionTimeout) {
            this.consumerSubscriptionTimeout = consumerSubscriptionTimeout;
        }

        public Boolean getConsumerUniqueId() {
            return consumerUniqueId;
        }

        public void setConsumerUniqueId(Boolean consumerUniqueId) {
            this.consumerUniqueId = consumerUniqueId;
        }

        public String getConsumerGroup() {
            return consumerGroup;
        }

        public void setConsumerGroup(String consumerGroup) {
            this.consumerGroup = consumerGroup;
        }
    }
}
