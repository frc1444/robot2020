package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.first1444.frc.robot2020.subsystems.implementations.BaseIntake;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

public class MotorIntake extends BaseIntake {
    private static final CANSparkMaxLowLevel.MotorType INDEXER_TYPE = CANSparkMaxLowLevel.MotorType.kBrushless;
    private static final CANSparkMaxLowLevel.MotorType FEEDER_TYPE = CANSparkMaxLowLevel.MotorType.kBrushless;

    private final VictorSPX intakeMotor;
    private final CANSparkMax indexerMotor;
    private final CANSparkMax feederMotor;

    public MotorIntake() {
        intakeMotor = new VictorSPX(RobotConstants.CAN.INTAKE);
        indexerMotor = new CANSparkMax(RobotConstants.CAN.INDEXER, INDEXER_TYPE);
        feederMotor = new CANSparkMax(49, FEEDER_TYPE);

        intakeMotor.configFactoryDefault(RobotConstants.INIT_TIMEOUT);

        indexerMotor.restoreFactoryDefaults();
        indexerMotor.setMotorType(INDEXER_TYPE);

        feederMotor.restoreFactoryDefaults();
        feederMotor.setMotorType(FEEDER_TYPE);
        feederMotor.setInverted(true);
    }

    @Override
    protected void run(double intakeSpeed, double indexerSpeed, double feederSpeed) {
        intakeMotor.set(ControlMode.PercentOutput, intakeSpeed);
        indexerMotor.set(indexerSpeed);
        feederMotor.set(feederSpeed);
    }
}
