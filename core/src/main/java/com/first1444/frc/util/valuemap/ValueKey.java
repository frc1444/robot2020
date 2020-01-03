package com.first1444.frc.util.valuemap;

public interface ValueKey {
    /**
     * @return The name of the key
     */
    String getName();

    /**
     * @return The value type representing the type of the default value and stored values
     */
    ValueType getValueType();
    /**
     * @return A String, Boolean or Number representing the default value
     */
    Object getDefaultValue();
}
