package com.first1444.frc.robot2020.autonomous.options;

public final class AutonomousSettings {
    private final AutonomousType autonomousType;
    private final BasicMovementType basicMovementType;

    public AutonomousSettings(AutonomousType autonomousType, BasicMovementType basicMovementType) {
        this.autonomousType = autonomousType;
        this.basicMovementType = basicMovementType;
    }

    public AutonomousType getAutonomousType() { return autonomousType; }
    public BasicMovementType getBasicMovementType(){ return basicMovementType; }

}
