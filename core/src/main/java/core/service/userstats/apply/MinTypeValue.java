package core.service.userstats.apply;

import core.data.UserStats;
import core.exception.UserStatsUpdateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class MinTypeValue extends ApplyValueImpl {

    @Override
    public Long checkValue(Long maxValue, Long minValue, Long defaultValue, Optional<UserStats> prevUserStats, Long requestValue){

        Long prevValue = getValue.apply(defaultValue, prevUserStats);

        if ( maxValue < requestValue )
            throw new UserStatsUpdateException("The requestValue is greater then maxValue.");

        if ( minValue > requestValue )
            throw new UserStatsUpdateException("The requestValue is less then minValue.");

        if( prevValue <= requestValue )
            throw new UserStatsUpdateException("The requestValue is less then or equal to prevValue.");

        return requestValue;

    }

}