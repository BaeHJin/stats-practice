package front.config;

import front.data.UserStatsPayload;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

@Slf4j
public class StatsPartitioner extends DefaultPartitioner implements Partitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {

        if(value instanceof UserStatsPayload) {

            int sizeOfPartitions = cluster.partitionsForTopic(topic).size();

            val tempKey = ((UserStatsPayload) value).getUser().getUserId();

            int hash = tempKey.hashCode();

            if(hash<0) hash *= -1;

            return hash % sizeOfPartitions;

        } else {
            return super.partition(topic, key, keyBytes, value, valueBytes, cluster);
        }
    }

    @Override
    public void close() {
//        throw new UnsupportedOperationException();
    }

    @Override
    public void configure(Map<String, ?> map) {
//        throw new UnsupportedOperationException();
    }



}
