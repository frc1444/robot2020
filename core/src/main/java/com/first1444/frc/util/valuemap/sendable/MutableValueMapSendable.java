package com.first1444.frc.util.valuemap.sendable;

import com.first1444.dashboard.ActiveComponent;
import com.first1444.dashboard.ActiveComponentMultiplexer;
import com.first1444.dashboard.BasicDashboard;
import com.first1444.dashboard.advanced.Sendable;
import com.first1444.dashboard.advanced.SendableHelper;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
import com.first1444.dashboard.value.implementations.PropertyActiveComponent;
import com.first1444.frc.util.valuemap.MutableValueMap;
import com.first1444.frc.util.valuemap.ValueKey;
import com.first1444.frc.util.valuemap.ValueMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MutableValueMapSendable<T extends Enum<T> & ValueKey> implements Sendable<ActiveComponent> {

    private final MutableValueMap<T> valueMap;

    public MutableValueMapSendable(Class<T> clazz){
        valueMap = new MutableValueMap<>(clazz);
    }

    public MutableValueMap<T> getMutableValueMap(){
        return valueMap;
    }
    public ValueMap<T> getImmutableValueMap(){
        return valueMap.build();
    }

    @NotNull
    @Override
    public ActiveComponent init(@NotNull String s, @NotNull BasicDashboard basicDashboard) {
        new SendableHelper(basicDashboard).setType("RobotPreferences");
        List<ActiveComponent> components = new ArrayList<>();
        for(T key : valueMap.getValueKeys()){
            String name = key.getName();
            switch(key.getValueType()){
                case DOUBLE:
                    components.add(new PropertyActiveComponent(name, basicDashboard.get(name), ValueProperty.create(
                            () -> BasicValue.makeDouble(valueMap.getDouble(key)),
                            (value) -> valueMap.setDouble(key, ((Number)value.getValue()).doubleValue())
                    )));
                    break;
                case STRING:
                    components.add(new PropertyActiveComponent(name, basicDashboard.get(name), ValueProperty.create(
                            () -> BasicValue.makeString(valueMap.getString(key)),
                            (value) -> valueMap.setString(key, ((String)value.getValue()))
                    )));
                    break;
                case BOOLEAN:
                    components.add(new PropertyActiveComponent(name, basicDashboard.get(name), ValueProperty.create(
                            () -> BasicValue.makeBoolean(valueMap.getBoolean(key)),
                            (value) -> valueMap.setBoolean(key, ((Boolean)value.getValue()))
                    )));
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported value type: " + key.getValueType());
            }
        }
        return new ActiveComponentMultiplexer(s, components);
    }
}
