package br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>()) {
            try (KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>()) {
                String newOrderTopic = "ECOMMERCE_NEW_ORDER";
                String sendEmailTopic = "ECOMMERCE_SEND_EMAIL";

                for (int i = 0; i < 10; i++) {
                    String userId = UUID.randomUUID().toString();
                    String orderId = UUID.randomUUID().toString();
                    BigDecimal amount = BigDecimal.valueOf(Math.random() * 5000 + 1);

                    String email = "Thank you for your order! We are processing it!";
                    Order order = new Order(userId, orderId, amount);

                    orderDispatcher.send(newOrderTopic, userId, order);

                    emailDispatcher.send(sendEmailTopic, userId, email);
                }
            }
        }
    }

}
