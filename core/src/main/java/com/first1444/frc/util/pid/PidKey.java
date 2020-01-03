package com.first1444.frc.util.pid;

import com.first1444.frc.util.valuemap.ValueKey;
import com.first1444.frc.util.valuemap.ValueType;

public enum PidKey implements ValueKey {
    P("p", ValueType.DOUBLE, 0.0),
    I("i", ValueType.DOUBLE, 0.0),
    D("d", ValueType.DOUBLE, 0.0),
    F("f", ValueType.DOUBLE, 0.0),

    CLOSED_RAMP_RATE("closed ramp rate", ValueType.DOUBLE, 0.0)

    ;

    private final String name;
    private final ValueType type;
    private final Object defaultValue;

    PidKey(String name, ValueType type, Object defaultValue){
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public ValueType getValueType() {
        return type;
    }
    public Object getDefaultValue(){
        return defaultValue;
    }
}
