package core.data.type;

import lombok.Getter;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.query.Update;

import java.util.function.LongFunction;

@Getter
public enum UpdateType {

    REPLACE("replaceTypeValue", UpdateType::setQuery),
    INCREMENT("incrementTypeValue", UpdateType::incQuery),
    MIN("minTypeValue", UpdateType::setQuery),
    MAX("maxTypeValue", UpdateType::setQuery);

    private String valueServiceName;
    private LongFunction<Update> f;

    private static Update setQuery(Long value){
        return new Update().set("updatedDate", DateTime.now()).set("value", value);
    }

    private static Update incQuery(Long value){
        return new Update().set("updatedDate", DateTime.now()).inc("value", value);
    }

    UpdateType(String valueServiceName, LongFunction<Update> f) {
        this.valueServiceName = valueServiceName;
        this.f = f;
    }

    public LongFunction<Update> setClauses(){
        return f;
    }
}
