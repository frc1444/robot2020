package com.first1444.frc.robot2020.autonomous.options;

import static java.util.Objects.requireNonNull;

public final class AutonomousSettings {
    private final AutonomousType autonomousType;
    private final BasicMovementType basicMovementType;

    public AutonomousSettings(AutonomousType autonomousType, BasicMovementType basicMovementType) {
        this.autonomousType = requireNonNull(autonomousType);
        this.basicMovementType = requireNonNull(basicMovementType);
    }

    public AutonomousType getAutonomousType() { return autonomousType; }
    public BasicMovementType getBasicMovementType(){ return basicMovementType; }

}
