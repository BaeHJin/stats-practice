package core.service.stats;

import core.data.Stats;
import core.exception.NotFoundDataException;
import core.repository.StatsRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StatsCacheService {

    private StatsRepository statsRepository;

    public StatsCacheService(StatsRepository statsRepository){
        this.statsRepository = statsRepository;
    }

    @Cacheable("statsCache")
    public Stats get(String statsId, String serviceId) {

        log.debug("cache test");
        val stats = statsRepository.findOne(statsId, serviceId);

        if(stats.isPresent())
            return stats.get();

        else {
            log.error("Not Found Stats - statsId : {}, serviceId : {}", statsId, serviceId);
            throw new NotFoundDataException("Not found stats");
        }

    }

    @Cacheable("statsCacheByService")
    public List<Stats> get(String serviceId) {

        log.debug("cache test");
        val stats = statsRepository.find(serviceId);

        if (stats.isEmpty()){
            log.error("Not Found Stats - serviceId : {}", serviceId);
            throw new NotFoundDataException("Not found stats");

        }else
            return stats;


    }

}
