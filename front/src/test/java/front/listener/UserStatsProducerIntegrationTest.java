package front.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.data.type.UpdateType;
import core.service.userstats.UserStatsService;
import front.config.KafkaConfig;
import front.data.User;
import front.data.UserStatsPayload;
import front.data.type.UserType;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import static org.springframework.kafka.test.assertj.KafkaConditions.key;
import static org.springframework.kafka.test.hamcrest.KafkaMatchers.hasValue;

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest(classes = {KafkaConfig.class, UserStatsProducer.class})
public class UserStatsProducerIntegrationTest {

    @Value("${kafka.topic}")
    private String topic;

    @Value("${kafka.hosts}")
    private String hosts;

    @Value("${kafka.groupId}")
    private String groupId;

    public static ObjectMapper mapper;

    @Autowired
    private UserStatsProducer producer;

    @Autowired
    KafkaTemplate<String, UserStatsPayload> template;

    private KafkaMessageListenerContainer<String, UserStatsPayload> container;

    private BlockingQueue<ConsumerRecord<String, String>> consumerRecords;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka;

    @BeforeClass
    public static void before(){
        mapper = new ObjectMapper();
    }

    @Before
    public void setUp() {
        embeddedKafka = new EmbeddedKafkaRule(1, true, 10, topic);
        consumerRecords = new LinkedBlockingQueue<>();

        String testGroupId = groupId + "-test-group";
        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps(hosts, testGroupId, "false");
        DefaultKafkaConsumerFactory<String, UserStatsPayload> consumer = new DefaultKafkaConsumerFactory<>(consumerProperties);

        ContainerProperties containerProperties = new ContainerProperties(topic);

        container = new KafkaMessageListenerContainer<>(consumer, containerProperties);
        container.setupMessageListener((MessageListener<String, String>) record -> {
            System.out.println("Listened message= " + record.toString());
            consumerRecords.add(record);
        });
        container.start();

        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic());
    }

    @After
    public void tearDown() {
        container.stop();
    }

    @Test
    public void it_should_send_without_key() throws InterruptedException, IOException {

        val givenUser =  new User(new User.UserId(UserType.S, "P10001234", "SGXQ", "0", "90002567"));
        val givenUserStats = new core.service.userstats.UserStatsService.UserStatsResponse("KILLING_ORC", 15L, System.currentTimeMillis());
        val userStats = UserStatsService.PutResponse.builder().userStats(givenUserStats).prevValue(10L).updateType(UpdateType.INCREMENT).build();

        val payload = UserStatsPayload.of(givenUser, userStats, 5L);

        producer.publish(payload);

        ConsumerRecord<String, String> received = consumerRecords.poll(10, TimeUnit.SECONDS);
        System.out.println("received.value() :: "+received.value());
        String json = mapper.writeValueAsString( payload );

        assertThat(received, hasValue(json));
        assertThat(received).has(key(null));
    }
}