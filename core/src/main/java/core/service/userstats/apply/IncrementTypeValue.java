package core.service.userstats.apply;

import core.data.UserStats;
import core.exception.UserStatsUpdateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Slf4j
@Service
public class IncrementTypeValue extends ApplyValueImpl {

    @Override
    public Long checkValue(Long maxValue, Long minValue, Long defaultValue, Optional<UserStats> prevUserStats, Long requestValue) {

        if (prevUserStats.isPresent()) {
            return applyValue(maxValue, minValue, prevUserStats.get().getValue(), requestValue);

        } else
            return applyValue(maxValue, minValue, defaultValue, requestValue);

    }

    private Long applyValue(Long maxValue, Long minValue, Long prevValue, Long requestValue){

        Long value = requestValue;

        if(maxValue < prevValue + requestValue)
            value = maxValue-prevValue;

        if(minValue > prevValue + requestValue)
            value = minValue-prevValue;

        if(value.equals(0L))
            throw new UserStatsUpdateException("There is no change.");

        return value;

    }

}
