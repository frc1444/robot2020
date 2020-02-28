package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.Intake;

import static java.util.Objects.requireNonNull;

public abstract class BaseIntake implements Intake {
    private Control control = Control.MANUAL;
    private Double intakeSpeed = null;
    private Double indexerSpeed = null;
    private Double feederSpeed = null;

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
        this.control = requireNonNull(control);
    }

    protected abstract void run(Control control, Double intakeSpeed, Double indexerSpeed, Double feederSpeed);

    @Override
    public final void run() {
        run(control, intakeSpeed, indexerSpeed, feederSpeed);
        intakeSpeed = null;
        indexerSpeed = null;
        feederSpeed = null;
        control = Control.MANUAL;
    }
}
