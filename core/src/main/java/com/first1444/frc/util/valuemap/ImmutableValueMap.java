package com.first1444.frc.util.valuemap;

import java.util.*;

class ImmutableValueMap<T extends Enum<T> & ValueKey> extends ValueMapBase<T>{

    public ImmutableValueMap(Class<T> clazz, EnumMap<T, Object> enumMap){
        super(Collections.unmodifiableMap(enumMap.clone()), Collections.unmodifiableSet(EnumSet.allOf(clazz)));
    }
}
