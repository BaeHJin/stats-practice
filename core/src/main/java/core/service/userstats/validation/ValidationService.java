package core.service.userstats.validation;

import core.data.Stats;
import core.data.type.PermissionType;
import core.data.type.StatusType;
import core.exception.UserStatsValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ValidationService {

    public Boolean validation(Stats stats, Number value, PermissionType type){

        try {

            checkPermissionType(stats.getPermissionType(), type);
            checkStatusType(stats.getStatus());
            checkChangeValue(stats.getMaxChangeValue(), stats.getMinChangeValue(), stats.getValueType().getValue().applyAsLong(value));

            return true;

        } catch (UserStatsValidationException e){
            log.warn(e +
                    "\n stats id : " + stats.getId().getStatsId() + ", service id : " + stats.getId().getServiceId() +
                    "\n StatusType : " + stats.getStatus() +
                    "\n MinChangeValue : " + stats.getMinChangeValue() + ", MaxChangeValue : " + stats.getMaxChangeValue() +
                    "\n request value : {}", value);
            throw e;
        }

    }

    private void checkPermissionType(PermissionType type, PermissionType requestType){

        if(!type.equals(PermissionType.ALL) && !type.equals(requestType))
            throw new UserStatsValidationException("Can not update. Because of the PermissionType");

    }

    private void checkStatusType(StatusType type){

        if(!StatusType.OK.equals(type))
            throw new UserStatsValidationException("Can not update. Because of the StatusType");

    }


    private void checkChangeValue(Long maxChangeValue, Long minChangeValue, Long value){

        if(maxChangeValue < value)
            throw new UserStatsValidationException("Exceed the maximum change value.");

        if(minChangeValue > value)
            throw new UserStatsValidationException("Fall short of the minimum change value.");

    }


}

