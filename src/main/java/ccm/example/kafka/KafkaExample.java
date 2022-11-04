package ccm.example.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ccm.example.kafka")
public class KafkaExample {
    public static void main(String[] args) {
        SpringApplication.run(KafkaExample.class, args);
    }
}
