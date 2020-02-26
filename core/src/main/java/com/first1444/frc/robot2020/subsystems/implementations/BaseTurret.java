package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.Turret;
import com.first1444.sim.api.Rotation2;

import static java.util.Objects.requireNonNull;

public abstract class BaseTurret implements Turret {
    private DesiredState desiredState = DesiredState.NEUTRAL;
    private Rotation2 trim = Rotation2.ZERO;

    @Override
    public void setDesiredTrim(Rotation2 trim) {
        this.trim = trim;
    }

    @Override
    public void setDesiredState(DesiredState desiredState) {
        this.desiredState = requireNonNull(desiredState);
    }

    @Override
    public final void run() {
        DesiredState desiredState = this.desiredState;
        Rotation2 desiredRotation = desiredState.getDesiredRotation();
        if(desiredRotation != null){
            desiredState = DesiredState.createDesiredRotation(desiredRotation.plus(trim));
        }
        run(desiredState);
        if(desiredState.getRawSpeedClockwise() != null){
            this.desiredState = DesiredState.NEUTRAL;
        }
    }
    protected abstract void run(DesiredState desiredState);
}
