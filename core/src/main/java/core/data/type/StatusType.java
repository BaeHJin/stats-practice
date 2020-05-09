package core.data.type;

import core.data.Stats;
import org.joda.time.DateTime;

import java.util.function.BinaryOperator;

public enum StatusType {
    INACTIVE( (prevStats, paramStats) -> {

        paramStats.setId(prevStats.getId());
        paramStats.setRegId(prevStats.getRegId());
        paramStats.setCreatedDate(prevStats.getCreatedDate());
        paramStats.setUpdatedDate(DateTime.now());
        return paramStats;
    }),

    READY((prevStats, paramStats) -> {

        prevStats.setStatus(paramStats.getStatus());
        prevStats.setDescription(paramStats.getDescription());
        prevStats.setUptId(paramStats.getUptId());
        prevStats.setUpdatedDate(DateTime.now());

        return prevStats;
    }),

    OK((prevStats, paramStats) -> {

        prevStats.setStatus(paramStats.getStatus());
        prevStats.setDescription(paramStats.getDescription());
        prevStats.setUptId(paramStats.getUptId());
        prevStats.setUpdatedDate(DateTime.now());

        return prevStats;
    }),

    EXPIRED((prevStats, paramStats) -> {

        prevStats.setStatus(paramStats.getStatus());
        prevStats.setDescription(paramStats.getDescription());
        prevStats.setUptId(paramStats.getUptId());
        prevStats.setUpdatedDate(DateTime.now());

        return prevStats;
    });

    private BinaryOperator<Stats> f;

    StatusType(BinaryOperator<Stats> f) {
        this.f = f;
    }

    public BinaryOperator<Stats> update(){
        return f;
    }


}
