package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.Turret;

import static java.util.Objects.requireNonNull;

public abstract class BaseTurret implements Turret {
    private DesiredState desiredState = DesiredState.NEUTRAL;

    @Override
    public void setDesiredState(DesiredState desiredState) {
        this.desiredState = requireNonNull(desiredState);
    }

    @Override
    public final void run() {
        run(desiredState);
        if(desiredState.getRawSpeedClockwise() != null){
            desiredState = DesiredState.NEUTRAL;
        }
    }
    protected abstract void run(DesiredState desiredState);
}
