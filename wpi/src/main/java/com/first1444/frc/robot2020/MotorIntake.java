package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.first1444.frc.robot2020.subsystems.implementations.BaseIntake;
import com.revrobotics.CANError;
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
        feederMotor = new CANSparkMax(RobotConstants.CAN.FEEDER, FEEDER_TYPE);

        intakeMotor.configFactoryDefault(RobotConstants.INIT_TIMEOUT);
        intakeMotor.setInverted(InvertType.InvertMotorOutput);
        intakeMotor.configOpenloopRamp(.3);

        indexerMotor.restoreFactoryDefaults();
        indexerMotor.setMotorType(INDEXER_TYPE);
        indexerMotor.setOpenLoopRampRate(.3);

        CANError error = feederMotor.restoreFactoryDefaults();
        feederMotor.setMotorType(FEEDER_TYPE);
        feederMotor.setOpenLoopRampRate(.3);
        feederMotor.setInverted(true);
        System.out.println("Feeder error: " + error);
    }

    @Override
    protected void run(double intakeSpeed, double indexerSpeed, double feederSpeed) {
        intakeMotor.set(ControlMode.PercentOutput, intakeSpeed * 1.0);
        indexerMotor.set(indexerSpeed * .5);
        feederMotor.set(feederSpeed * 1.0);
    }
}
