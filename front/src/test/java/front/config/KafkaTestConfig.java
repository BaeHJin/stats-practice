package front.config;

import front.data.UserStatsPayload;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaTestConfig {

    @Value("${kafka.hosts}")
    private String hosts;

    @Value("${kafka.groupId}")
    private String groupId;


    @Bean
    public ConsumerFactory<String, UserStatsPayload> consumerFactory() {

        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), new JsonDeserializer<>(UserStatsPayload.class));

    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, UserStatsPayload> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;

    }


    private Map<String, Object> consumerConfigs() {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, StatsPartitioner.class);
        // See https://github.com/SpringOnePlatform2016/grussell-spring-kafka/blob/master/s1p-kafka/src/main/java/org/s1p/JsonConfiguration.java#L66
        return props;

    }

}

