package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;

public class EmailService {
    public static void main(String[] args) {
        EmailService emailService = new EmailService();
        try (KafkaService<String> service = new KafkaService<>(EmailService.class.getSimpleName(),
                "ECOMMERCE_SEND_EMAIL",
                emailService::parse,
                String.class,
                new HashMap<>())
        ) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, String> record) {
        System.out.println("-----------------------------------------");
        System.out.println("Sending email");
        System.out.println("KEY: " + record.key());
        System.out.println("VALUE: " + record.value());
        System.out.println("PARTITION: " + record.partition());
        System.out.println("OFFSET: " + record.offset());
        System.out.println("-----------------------------------------");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignore
        }

        System.out.println("Email sent");
    }
}
