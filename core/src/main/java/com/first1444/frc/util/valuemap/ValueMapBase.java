package com.first1444.frc.util.valuemap;

import java.util.Map;
import java.util.Set;

class ValueMapBase<T extends Enum<T> & ValueKey> implements ValueMap<T>{
    protected final Map<T, Object> map;
    protected final Set<T> valueKeys;

    ValueMapBase(Map<T, Object> map, Set<T> valueKeys) {
        this.map = map;
        this.valueKeys = valueKeys;
    }

    @Override
    public Set<T> getValueKeys() {
        return valueKeys;
    }

    @Override
    public boolean getBoolean(T key) {
        checkKey(key, ValueType.BOOLEAN);
        return (boolean) map.get(key);
    }

    @Override
    public double getDouble(T key) {
        checkKey(key, ValueType.DOUBLE);
        return (double) map.get(key);
    }

    @Override
    public String getString(T key) {
        checkKey(key, ValueType.STRING);
        return (String) map.get(key);
    }

    protected void checkKey(T key, ValueType expected){
        if(key.getValueType() != expected){
            throw new IllegalArgumentException("The passed key isn't a " + expected + " value type! key: " + key);
        }
        if(!valueKeys.contains(key)){
            throw new IllegalStateException("key is not in the value keys! The value keys must be incorrect! valueKeys: " + valueKeys);
        }
    }
}
