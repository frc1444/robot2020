package com.first1444.frc.robot2020;

import com.first1444.frc.robot2020.subsystems.implementations.BaseIntake;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

public class MotorIntake extends BaseIntake {
    private final CANSparkMax indexerMotor;

    public MotorIntake() {
        indexerMotor = new CANSparkMax(40, CANSparkMaxLowLevel.MotorType.kBrushless);
    }

    @Override
    protected void run(double intakeSpeed, double indexerSpeed, double feederSpeed) {
        indexerMotor.set(indexerSpeed);
    }
}
