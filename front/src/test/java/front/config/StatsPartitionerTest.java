package front.config;

import com.google.common.collect.Lists;
import core.data.type.UpdateType;
import core.service.userstats.UserStatsService;
import front.data.User;
import front.data.UserStatsPayload;
import front.data.type.UserType;
import lombok.val;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.record.InvalidRecordException;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

public class StatsPartitionerTest {

    @InjectMocks
    StatsPartitioner statsPartitioner;

    @Mock
    Cluster cluster;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPartition(){

        //given
        Object key = null;
        byte[] keyBytes = null;
        byte[] valueBytes = null;
        String topic = "topic";

        val givenUser =  new User(new User.UserId(UserType.S, "100", "testGame", null, "1"));
        val givenUserStats = new core.service.userstats.UserStatsService.UserStatsResponse("givenStatsId", 10L, System.currentTimeMillis());
        val userStats = UserStatsService.PutResponse.builder().userStats(givenUserStats).prevValue(5L).updateType(UpdateType.INCREMENT).build();

        Object value = UserStatsPayload.of(givenUser, userStats, 5L);

        List<PartitionInfo> partitionInfo = Lists.newArrayList();

        for(int i = 0; i <10; i++)
            partitionInfo.add(new PartitionInfo(topic, i, null, null, null));

        when(cluster.partitionsForTopic(topic)).thenReturn(partitionInfo);

        //when
        val partition = statsPartitioner.partition(topic, key, keyBytes, value, valueBytes, cluster);

        //then
        assertThat(partition).isBetween(0,9);

    }

    @Test
    public void testPartitionNotUserStatsPayload(){

        //given
        Object key = null;
        byte[] keyBytes = null;
        byte[] valueBytes = null;
        String topic = "topic";

        Object value = "test";

        List<PartitionInfo> partitionInfo = Lists.newArrayList();

        for(int i = 0; i <10; i++)
            partitionInfo.add(new PartitionInfo(topic, i, null, null, null));

        when(cluster.partitionsForTopic(topic)).thenReturn(partitionInfo);

        //when
        val partition = statsPartitioner.partition(topic, key, keyBytes, value, valueBytes, cluster);

        //then
        assertThat(partition).isBetween(0,9);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void testClose() {
        statsPartitioner.close();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testConfigure() {
        statsPartitioner.configure(null);
    }

}
