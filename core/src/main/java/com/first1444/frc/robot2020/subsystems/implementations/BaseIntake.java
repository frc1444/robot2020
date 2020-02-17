package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.Intake;

public abstract class BaseIntake implements Intake {
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

    protected abstract void run(double intakeSpeed, double indexerSpeed, double feederSpeed);

    @Override
    public final void run() {
        run(intakeSpeed, indexerSpeed, feederSpeed);
        intakeSpeed = 0;
        indexerSpeed = 0;
        feederSpeed = 0;
    }
}
