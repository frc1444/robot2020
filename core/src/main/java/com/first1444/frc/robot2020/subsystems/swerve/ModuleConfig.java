package com.first1444.frc.robot2020.subsystems.swerve;

import com.first1444.frc.util.valuemap.ValueKey;
import com.first1444.frc.util.valuemap.ValueType;

public enum ModuleConfig implements ValueKey {
    ABS_ENCODER_OFFSET("Absolute Encoder Offset", ValueType.DOUBLE, 0),
    MIN_ENCODER_VALUE("Minimum Encoder Value", ValueType.DOUBLE, 10),
    MAX_ENCODER_VALUE("Maximum Encoder Value", ValueType.DOUBLE, 899)
    ;
    private final String name;
    private final ValueType valueType;
    private final Object defaultValue;

    ModuleConfig(String name, ValueType valueType, Object defaultValue) {
        this.name = name;
        this.valueType = valueType;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }
}
