package com.first1444.frc.robot2020.autonomous.options;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

import static java.util.Arrays.asList;
import static java.util.Collections.*;

public enum AutonomousType {
    TEST_MODE("TEST (Be careful)", "TEST", emptyList()),
    DO_NOTHING(
            "Do nothing", "Does nothing",
            singleton(BasicMovementType.STILL)
    ),
    MOVE(
            "Move", "Moves in the specified direction",
            asList(BasicMovementType.BACKWARD, BasicMovementType.FORWARD)
    ),
    MOVE_AND_SPIN(
            "Move and Spin", "Moves in the specified direction and spins to get intake down",
            asList(BasicMovementType.BACKWARD, BasicMovementType.FORWARD)
    ),
    MOVE_TURN_SHOOT(
            "Move Turn Shoot", "Moves, turn, then shoots",
            EnumSet.of(BasicMovementType.BACKWARD, BasicMovementType.STILL)
    ),
    TRENCH_AUTO(
            "Trench Auto", "Starts on right side of field, moves toward trench, spins and gets balls, then shoots",
            emptyList()
    ),
    CENTER_RV(
            "Center RV", "Goes for two of the three center balls in the RV zone",
            emptyList()
    ),
    ;

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
