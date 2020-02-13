package com.first1444.frc.robot2020.autonomous.options;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableCollection;

public enum AutonomousType {
    DO_NOTHING(
            "Do nothing", "Does nothing",
            singleton(BasicMovementType.STILL)
    ),
    MOVE(
            "Move", "Moves in the specified direction",
            asList(BasicMovementType.BACKWARD, BasicMovementType.FORWARD)
    ),
    TURN_SHOOT_MOVE(
            "Turn Shoot Move", "Aligns, shoots, then moves",
            EnumSet.allOf(BasicMovementType.class)
    ),
    MOVE_TURN_SHOOT(
            "Move Turn Shoot", "Moves, turn, then shoots",
            EnumSet.allOf(BasicMovementType.class)
    ),
    SHOOT_IMMEDIATE(
            "Shoot Immediate", "Shoots, then moves",
            EnumSet.allOf(BasicMovementType.class)
    );

    private final String displayName;
    private final String description;
    private final Collection<BasicMovementType> basicMovementTypes;

    AutonomousType(String displayName, String description, Collection<BasicMovementType> basicMovementTypes) {
        this.displayName = displayName;
        this.description = description;
        this.basicMovementTypes = enumSetOf(basicMovementTypes);
    }
    private static <T extends Enum<T>> Collection<T> enumSetOf(Collection<T> collection){
        if(collection.isEmpty()){
            return Collections.emptySet();
        }
        return unmodifiableCollection(EnumSet.copyOf(collection));
    }
    public String getDisplayName(){
        return displayName;
    }
    public String getDescription(){
        return description;
    }
    public Collection<BasicMovementType> getBasicMovementTypes(){
        return basicMovementTypes;
    }
}
