package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.Intake;

public abstract class BaseIntake implements Intake {
    private Control state = Control.MANUAL;
    private double intakeSpeed;
    private double indexerSpeed;
    private double feederSpeed;

    @Override
    public final void setIntakeSpeed(double speed) {
        this.intakeSpeed = speed;
    }

    @Override
    public final void setIndexerSpeed(double speed) {
        this.indexerSpeed = speed;
    }

    @Override
    public final void setFeederSpeed(double speed) {
        this.feederSpeed = speed;
    }
    @Override
    public void setControl(Control control) {
        this.state = control;
    }

    protected abstract void run(Control control, double intakeSpeed, double indexerSpeed, double feederSpeed);

    @Override
    public final void run() {
        run(state, intakeSpeed, indexerSpeed, feederSpeed);
        intakeSpeed = 0;
        indexerSpeed = 0;
        feederSpeed = 0;
        state = Control.MANUAL;
    }
}
