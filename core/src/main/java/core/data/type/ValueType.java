package core.data.type;

import java.util.function.ToLongFunction;

public enum ValueType {
    INT(value -> value.longValue()),
    DOUBLE(value -> Math.round(value.doubleValue()*1000));

    private ToLongFunction<Number> f;

    ValueType(ToLongFunction<Number> f) {this.f = f;}

    public ToLongFunction<Number> getValue(){
        return f;
    }


}
