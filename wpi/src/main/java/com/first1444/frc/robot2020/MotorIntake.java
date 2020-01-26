package com.first1444.frc.robot2020;

import com.first1444.frc.robot2020.subsystems.Intake;

public class MotorIntake implements Intake {
    @Override
    public void setIntakeSpeed(double speed) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIndexerSpeed(double speed) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBallCount() {
        return 0;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException();
    }
}
