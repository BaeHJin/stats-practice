package front.listener;

import front.data.UserStatsPayload;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserStatsProducer {

    KafkaTemplate<String, UserStatsPayload> template;

    public UserStatsProducer(KafkaTemplate<String, UserStatsPayload> template) {

        this.template = template;

    }

    @Value("${kafka.topic}")
    String topic;


    @EventListener
    public void publish(UserStatsPayload payload) {
        template.send(topic, payload);
    }


}
