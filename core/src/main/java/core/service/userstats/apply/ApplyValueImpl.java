package core.service.userstats.apply;

import core.data.Stats;
import core.data.UserStats;
import core.exception.UserStatsUpdateException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

@Slf4j
public abstract class ApplyValueImpl implements ApplyValue {

    BiFunction<Long, Optional<UserStats>, Long> getValue = (defaultValue, prevUserStats) -> {

        if (prevUserStats.isPresent())
            return prevUserStats.get().getValue();

        else
            return defaultValue;
    };

    @Override
    public Long applyValue(Stats stats, Number requestValue, Optional<UserStats> prevUserStats) {

        try {

            val valueType = stats.getValueType();

            Long minValue = valueType.getValue().applyAsLong(stats.getMinValue());
            Long maxValue = valueType.getValue().applyAsLong(stats.getMaxValue());
            Long defaultValue = valueType.getValue().applyAsLong(stats.getDefaultValue());
            Long value = valueType.getValue().applyAsLong(requestValue);

            return checkValue(maxValue, minValue, defaultValue, prevUserStats, value);

        } catch (UserStatsUpdateException e){

            log.warn(e +
                    "\n service id : " + stats.getId().getServiceId() + ", stats id : " + stats.getId().getStatsId() +
                    "\n minValue : " + stats.getMinValue() + ", maxValue : " + stats.getMaxValue() +
                    "\n request value : " + requestValue +
                    " prev Value : "  +
                    ( !prevUserStats.isPresent() ? stats.getDefaultValue() : getValue(stats, prevUserStats.get()) )
            );
            throw e;

        }

    }

    @NotNull
    private Long getValue(final Stats stats, final UserStats prevUserStats) {
        return ObjectUtils.isEmpty(prevUserStats.getValue())?
            stats.getDefaultValue() : stats.getValueType().getValue().applyAsLong(prevUserStats.getValue());
    }

}
