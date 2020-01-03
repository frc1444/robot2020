package com.first1444.frc.util.valuemap;

import java.util.Set;

public interface ValueMap<T extends Enum<T> & ValueKey> {
    Set<T> getValueKeys();

    boolean getBoolean(T key);
    double getDouble(T key);
    String getString(T key);
}
