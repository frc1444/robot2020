package com.first1444.frc.robot2020.autonomous;

import com.first1444.frc.util.valuemap.ValueKey;
import com.first1444.frc.util.valuemap.ValueType;

public enum AutonomousConfigKey implements ValueKey {
    /** The amount of time in seconds to wait before autonomous starts.*/
    WAIT_TIME("Wait Time", ValueType.DOUBLE, 0.0)
    ;

    private final String name;
    private final ValueType type;
    private final Object defaultValue;

    AutonomousConfigKey(String name, ValueType type, Object defaultValue){
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

