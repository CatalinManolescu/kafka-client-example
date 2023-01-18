package ccm.example.kafka.controllers;

import ccm.example.kafka.services.GenericReactorKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.UUID;

@RestController
public class ReactorKafkaProducerController {
    private static final Logger log = LoggerFactory.getLogger(ReactorKafkaProducerController.class);

    private final GenericReactorKafkaProducer genericKafkaProducer;

    public ReactorKafkaProducerController(GenericReactorKafkaProducer genericKafkaProducer) {
        this.genericKafkaProducer = genericKafkaProducer;
    }

    @PostMapping("/reactive/kafka/topics/{topic}")
    public void pushMessage(@PathVariable String topic, @RequestBody String message) {
        this.sendMessage(topic, message);
    }

    private void sendMessage(String topic, String message) {
        if (Objects.isNull(message)) {
            message = UUID.randomUUID().toString();
        }
        genericKafkaProducer.sendMessage(topic, this.createKey(message), message);
    }

    private String createKey(String message) {
        try {
            return message + "-" + InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.error("Unable to get host address -> using 'message' as key", e);
        }

        return message;
    }
}
