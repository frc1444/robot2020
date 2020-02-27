package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.first1444.frc.robot2020.subsystems.implementations.BaseIntake;
import com.first1444.sim.api.Clock;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

public class MotorIntake extends BaseIntake {
    private static final CANSparkMaxLowLevel.MotorType INDEXER_TYPE = CANSparkMaxLowLevel.MotorType.kBrushless;
    private static final CANSparkMaxLowLevel.MotorType FEEDER_TYPE = CANSparkMaxLowLevel.MotorType.kBrushless;

    private final Clock clock;

    private final WPI_VictorSPX intakeMotor;
    private final CANSparkMax indexerMotor;
    private final CANSparkMax feederMotor;

    public MotorIntake(Clock clock) {
        this.clock = clock;
        intakeMotor = new WPI_VictorSPX(RobotConstants.CAN.INTAKE);
        indexerMotor = new CANSparkMax(RobotConstants.CAN.INDEXER, INDEXER_TYPE);
        feederMotor = new CANSparkMax(RobotConstants.CAN.FEEDER, FEEDER_TYPE);

        intakeMotor.configFactoryDefault(RobotConstants.INIT_TIMEOUT);
        intakeMotor.setInverted(InvertType.InvertMotorOutput);
        intakeMotor.configOpenloopRamp(.3);

        indexerMotor.restoreFactoryDefaults();
        indexerMotor.setMotorType(INDEXER_TYPE);
        indexerMotor.setOpenLoopRampRate(.3);

        feederMotor.restoreFactoryDefaults();
        feederMotor.setMotorType(FEEDER_TYPE);
        feederMotor.setOpenLoopRampRate(.3);
        feederMotor.setInverted(true);
    }

    @Override
    protected void run(Control control, double intakeSpeed, double indexerSpeed, double feederSpeed) {
        if(control == Control.INTAKE || control == Control.FEED_ALL_AND_INTAKE || control == Control.STORE_AND_INTAKE){
            intakeSpeed = 1.0;
        }
        if(control == Control.FEED_ALL || control == Control.FEED_ALL_AND_INTAKE){
            feederSpeed = 1.0;
            double timestamp = clock.getTimeSeconds();
            double result = timestamp % 1.0;
            if(result < .2){
                indexerSpeed = -.7;
            } else if(result > .3 && result < .9) {
                indexerSpeed = 1.0;
            }
        }
        if(control == Control.STORE || control == Control.STORE_AND_INTAKE){
            feederSpeed = 0.0;
            indexerSpeed = 1.0;
        }
        intakeMotor.set(intakeSpeed * 1.0);
        indexerMotor.set(indexerSpeed * .5);
        feederMotor.set(feederSpeed * 1.0);
    }
}
