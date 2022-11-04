package ccm.example.kafka.controllers;

import ccm.example.kafka.KafkaExampleProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class KafkaExampleController {
    private final KafkaExampleProperties properties;

    public KafkaExampleController(KafkaExampleProperties properties) {
        this.properties = properties;
    }

    @GetMapping(path = "/echo")
    public String echo(@RequestParam("q") String q) {
        if (Objects.isNull(q)) {
            return "echo";
        }
        return q;
    }

    @GetMapping(path = "/kafka/config")
    public String config() {
        return """
                {
                    client-id: %s,
                    bootstrap-servers: %s
                }
                """.formatted(
                        properties.getKafka().getClientId(),
                        properties.getKafka().getBootstrapServers());
    }
}
