package front.config;

import core.data.type.UpdateType;
import core.service.userstats.UserStatsService;
import front.data.User;
import front.data.UserStatsPayload;
import front.data.type.UserType;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

public class kafkaConfigTest {

    @Autowired
    KafkaConfig kafkaConfig;
    UserStatsPayload payload;

    @Before
    public void setUp(){

        val givenStatsId = "testStats";

        val givenUserId = "test";
        val givenUser =  new User(new User.UserId(UserType.S, "000", "test", null, givenUserId));
        val givenUserStats = new core.service.userstats.UserStatsService.UserStatsResponse(givenStatsId, -1L, System.currentTimeMillis());
        val userStats = UserStatsService.PutResponse.builder().userStats(givenUserStats).prevValue(1L).updateType(UpdateType.MIN).build();

        payload = UserStatsPayload.of(givenUser, userStats, 1L);

    }


    //integration test가 될수바께없당
    @Test
    public void testKafkaConfig(){

        val kafkaConfig = new KafkaConfig();

        ReflectionTestUtils.setField(kafkaConfig,  "hosts", "10.250.18.38:9092");
        ReflectionTestUtils.setField(kafkaConfig,  "groupId", "stats");


        val template = kafkaConfig.kafkaTemplate();

        val test = template.send("test.stats", payload);


    }


}
