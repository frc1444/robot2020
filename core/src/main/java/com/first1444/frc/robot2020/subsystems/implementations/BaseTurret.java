package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.Turret;
import com.first1444.sim.api.Rotation2;

public abstract class BaseTurret implements Turret {
    private DesiredState desiredState = DesiredState.NEUTRAL;

    @Override
    public final void setDesiredRotation(Rotation2 rotation) {
        desiredState = DesiredState.createDesiredRotation(rotation);
    }

    @Override
    public final void setRawSpeed(double speed) {
        desiredState = DesiredState.createRawSpeed(speed);
    }

    @Override
    public final void run() {
        run(desiredState);
        if(desiredState.getRawSpeed() != null){
            desiredState = DesiredState.NEUTRAL;
        }
    }
    protected abstract void run(DesiredState desiredState);
}
