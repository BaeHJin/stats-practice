package front.listener;

import core.data.type.UpdateType;
import front.config.StatsPartitioner;
import front.data.type.UserType;
import core.service.userstats.UserStatsService;
import front.config.KafkaConfig;
import front.config.KafkaTestConfig;
import front.data.User;
import front.data.UserStatsPayload;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.util.Lists;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {KafkaConfig.class, KafkaTestConfig.class, UserStatsProducer.class, UserStatsProducerTest.Listener.class, StatsPartitioner.class})
@Slf4j
public class UserStatsProducerTest {

    @Autowired
    private UserStatsProducer producer;

    @Autowired
    private Listener listener;

    @Value("${kafka.topic}")
    String topic;

    @Autowired
    KafkaTemplate<String, UserStatsPayload> template;

    @Test
    public void testPublishRoutePartition(){

        val partition = template.partitionsFor(topic);

        log.debug("topic: {}",  topic);
        log.debug("partition for topic: {}",  partition);
    }

    @Test
    public void testPublish() {

        //given
        val givenStatsId = "KILLING_ORC";

        val givenUserId = "1";
        val givenUser =  new User(new User.UserId(UserType.S, "100", "testGame", null, givenUserId));
        val givenUserStats = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());
        val userStats = UserStatsService.PutResponse.builder().userStats(givenUserStats).prevValue(5L).updateType(UpdateType.INCREMENT).build();

        val givenUserId2 = "2";
        val givenUser2 =  new User(new User.UserId(UserType.S, "200", "testGame", null, givenUserId2));
        val givenUserStats2 = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());
        val userStats2 = UserStatsService.PutResponse.builder().userStats(givenUserStats2).prevValue(5L).updateType(UpdateType.INCREMENT).build();

        val givenUserId3 = "3";
        val givenUser3 =  new User(new User.UserId(UserType.S, "300", "testGame", null, givenUserId3));
        val givenUserStats3 = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());
        val userStats3 = UserStatsService.PutResponse.builder().userStats(givenUserStats3).prevValue(5L).updateType(UpdateType.INCREMENT).build();

        val payload = UserStatsPayload.of(givenUser, userStats, 5L);
        val payload2 = UserStatsPayload.of(givenUser2, userStats2, 10L);
        val payload3 = UserStatsPayload.of(givenUser3, userStats3, 15L);

        //when
        producer.publish(payload);
        producer.publish(payload2);
        producer.publish(payload3);

        val expected = Lists.newArrayList(payload, payload2, payload3);
        // then
        await().atMost(50, SECONDS).until(() -> listener.messages.size() >= 3);

        // todo 왜 안대?
        assertThat(listener.messages.size()).isEqualTo(3);

    }


    @Test
    public void testPublishRoute() {

        //given
        val givenStatsId = "KILLING_ORC";

        val givenUserId = "1";
        val givenUser =  new User(new User.UserId(UserType.S, "100", "testGame", null, givenUserId));

//        givenUser.setUserId(objectId);

        val givenUserStats = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());
        val userStats = UserStatsService.PutResponse.builder().userStats(givenUserStats).prevValue(5L).updateType(UpdateType.INCREMENT).build();

        val givenUserId2 = "2";
        val givenUser2 =  new User(new User.UserId(UserType.S, "200", "testGame", null, givenUserId2));
        givenUser2.setUserId(new ObjectId(new Date(22222)));
        val givenUserStats2 = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());
        val userStats2 = UserStatsService.PutResponse.builder().userStats(givenUserStats2).prevValue(5L).updateType(UpdateType.INCREMENT).build();

        val givenUserId3 = "3";
        val givenUser3 =  new User(new User.UserId(UserType.S, "300", "testGame", null, givenUserId3));
        givenUser3.setUserId(new ObjectId(new Date(33333)));
        val givenUserStats3 = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());
        val userStats3 = UserStatsService.PutResponse.builder().userStats(givenUserStats3).prevValue(5L).updateType(UpdateType.INCREMENT).build();

        val givenUserId4 = "4";
        val givenUser4 =  new User(new User.UserId(UserType.S, "100", "testGame", null, givenUserId4));
        givenUser4.setUserId(new ObjectId(new Date(44444)));
        val givenUserStats4 = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());
        val userStats4 = UserStatsService.PutResponse.builder().userStats(givenUserStats4).prevValue(5L).updateType(UpdateType.INCREMENT).build();

        val givenUserId5 = "5";
        val givenUser5 =  new User(new User.UserId(UserType.S, "200", "testGame", null, givenUserId5));
        givenUser5.setUserId(new ObjectId(new Date(55555)));
        val givenUserStats5 = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());
        val userStats5 = UserStatsService.PutResponse.builder().userStats(givenUserStats5).prevValue(5L).updateType(UpdateType.INCREMENT).build();

        val givenUserId6 = "6";
        val givenUser6 =  new User(new User.UserId(UserType.S, "300", "testGame", null, givenUserId6));
        givenUser6.setUserId(new ObjectId(new Date(66666)));
        val givenUserStats6 = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());
        val userStats6 = UserStatsService.PutResponse.builder().userStats(givenUserStats6).prevValue(5L).updateType(UpdateType.INCREMENT).build();

        val givenUserId7 = "7";
        val givenUser7 =  new User(new User.UserId(UserType.S, "100", "testGame", null, givenUserId7));
        givenUser7.setUserId(new ObjectId(new Date(77777)));
        val givenUserStats7 = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());
        val userStats7 = UserStatsService.PutResponse.builder().userStats(givenUserStats7).prevValue(5L).updateType(UpdateType.INCREMENT).build();

        val givenUserId8 = "8";
        val givenUser8 =  new User(new User.UserId(UserType.S, "200", "testGame", null, givenUserId8));
        givenUser8.setUserId(new ObjectId(new Date(88888)));
        val givenUserStats8 = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());
        val userStats8 = UserStatsService.PutResponse.builder().userStats(givenUserStats8).prevValue(5L).updateType(UpdateType.INCREMENT).build();

        val givenUserId9 = "9";
        val givenUser9 =  new User(new User.UserId(UserType.S, "300", "testGame", null, givenUserId9));
        givenUser9.setUserId(new ObjectId(new Date(99999)));
        val givenUserStats9 = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, 10L, System.currentTimeMillis());
        val userStats9 = UserStatsService.PutResponse.builder().userStats(givenUserStats9).prevValue(5L).updateType(UpdateType.INCREMENT).build();

        val payload = UserStatsPayload.of(givenUser, userStats, 5L);
        val payload2 = UserStatsPayload.of(givenUser2, userStats2, 10L);
        val payload3 = UserStatsPayload.of(givenUser3, userStats3, 15L);

        val payload4 = UserStatsPayload.of(givenUser4, userStats4, 5L);
        val payload5 = UserStatsPayload.of(givenUser5, userStats5, 10L);
        val payload6 = UserStatsPayload.of(givenUser6, userStats6, 15L);

        val payload7 = UserStatsPayload.of(givenUser7, userStats7, 5L);
        val payload8 = UserStatsPayload.of(givenUser8, userStats8, 10L);
        val payload9 = UserStatsPayload.of(givenUser9, userStats9, 15L);

        //when
        producer.publish(payload);
        producer.publish(payload2);
        producer.publish(payload3);
        producer.publish(payload4);
        producer.publish(payload5);
        producer.publish(payload6);
        producer.publish(payload7);
        producer.publish(payload8);
        producer.publish(payload9);
        val expected = Lists.newArrayList(payload, payload2, payload3, payload4, payload5, payload6, payload7, payload8, payload9);

        // then
        await().atMost(50, SECONDS).until(() -> listener.messages.size() >= 9);

        // todo 왜 안대?
        assertThat(listener.messages.size()).isEqualTo(9);

    }

    @Test
    public void testtt(){


        val givenUserId = "1";
        val givenUser =  new User(new User.UserId(UserType.S, "P10001234", "SGXQ", "0", "90002567"));
        val givenUserStats = new core.service.userstats.UserStatsService.UserStatsResponse("KILLING_ORC", 15L, System.currentTimeMillis());
        val userStats = UserStatsService.PutResponse.builder().userStats(givenUserStats).prevValue(10L).updateType(UpdateType.INCREMENT).build();

        val payload = UserStatsPayload.of(givenUser, userStats, 5L);

        //when
        producer.publish(payload);
        producer.publish(payload);
        producer.publish(payload);
        producer.publish(payload);
        producer.publish(payload);

    }

    public static class Listener {

        List<UserStatsPayload> messages = Lists.newArrayList();

        List<UserStatsPayload> failureMessages = Lists.newArrayList();

        @org.springframework.kafka.annotation.KafkaListener(topics = "${kafka.topic}")
        public void listen(UserStatsPayload payload) {

            messages.add(payload);

        }


    }

}



