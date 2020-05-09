package core.service.userstats.apply;

import core.data.Stats;
import core.data.UserStats;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface ApplyValue {

    Long applyValue(Stats stats, Number requestValue, Optional<UserStats> prevUserStats);
    Long checkValue(Long maxValue, Long minValue, Long defaultValue, Optional<UserStats> prevUserStats, Long value);



}
