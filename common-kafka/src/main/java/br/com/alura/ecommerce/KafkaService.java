package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {
    private final KafkaConsumer<String, T> consumer;
    private final ConsumerFunction<T> parse;

    KafkaService(String groupId, String topic, ConsumerFunction<T> parse, Class<T> type, Map<String, String> properties) {
        this(groupId, parse, type, properties);

        consumer.subscribe(Collections.singletonList(topic));
    }

    KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse, Class<T> type, Map<String, String> properties) {
        this(groupId, parse, type, properties);

        consumer.subscribe(topic);
    }

    private KafkaService(String groupId, ConsumerFunction<T> parse, Class<T> type, Map<String, String> properties) {
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(getProperties(type, groupId, properties));
    }

    @Override
    public void close() {
        this.consumer.close();
    }

    void run() {
        while (true) {
            ConsumerRecords<String, T> records = consumer.poll(Duration.ofMillis(100));

            if (!records.isEmpty()) {
                System.out.println(records.count() + " rows found");

                for (ConsumerRecord<String, T> record : records) {
                    parse.consume(record);
                }
            }

        }
    }

    private Properties getProperties(Class<T> type, String groupId, Map<String, String> overrideProperties) {
        Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());

        properties.putAll(overrideProperties);

        return properties;
    }

}
