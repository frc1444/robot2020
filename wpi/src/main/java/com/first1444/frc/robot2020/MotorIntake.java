package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.first1444.frc.robot2020.subsystems.balltrack.BallTracker;
import com.first1444.frc.robot2020.subsystems.implementations.BaseIntake;
import com.first1444.sim.api.Clock;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

public class MotorIntake extends BaseIntake {
    private static final CANSparkMaxLowLevel.MotorType INDEXER_TYPE = CANSparkMaxLowLevel.MotorType.kBrushless;
    private static final CANSparkMaxLowLevel.MotorType FEEDER_TYPE = CANSparkMaxLowLevel.MotorType.kBrushless;

    private final Clock clock;
    private final BallTracker ballTracker;

    private final WPI_VictorSPX intakeMotor;
    private final CANSparkMax indexerMotor;
    private final CANSparkMax feederMotor;

    public MotorIntake(Clock clock, BallTracker ballTracker) {
        this.clock = clock;
        this.ballTracker = ballTracker;
        intakeMotor = new WPI_VictorSPX(RobotConstants.CAN.INTAKE);
        indexerMotor = new CANSparkMax(RobotConstants.CAN.INDEXER, INDEXER_TYPE);
        feederMotor = new CANSparkMax(RobotConstants.CAN.FEEDER, FEEDER_TYPE);

        intakeMotor.configFactoryDefault(RobotConstants.INIT_TIMEOUT);
        intakeMotor.setInverted(InvertType.InvertMotorOutput);
        intakeMotor.configOpenloopRamp(.2);

        indexerMotor.restoreFactoryDefaults();
        indexerMotor.setMotorType(INDEXER_TYPE);
        indexerMotor.setOpenLoopRampRate(.2);

        feederMotor.restoreFactoryDefaults();
        feederMotor.setMotorType(FEEDER_TYPE);
        feederMotor.setOpenLoopRampRate(.1);
        feederMotor.setInverted(true);
    }

    @Override
    protected void run(Control control, Double intakeSpeed, Double indexerSpeed, Double feederSpeed) {
        boolean intake = control == Control.INTAKE || control == Control.FEED_ALL_AND_INTAKE || control == Control.STORE_AND_INTAKE;
        if(intake){
            if(intakeSpeed == null) {
                intakeSpeed = 1.0;
            }
        }
        if(indexerSpeed == null) {
            if (control == Control.FEED_ALL || control == Control.FEED_ALL_AND_INTAKE) { // TODO fix
                feederSpeed = 1.0;
                double timestamp = clock.getTimeSeconds();
                Double lastShootTime = ballTracker.getLastShootTimestamp();
                if (/*ballTracker.getBallCount() > 2 && */(lastShootTime == null || timestamp - lastShootTime > 5.0)) { // more than two balls and we haven't shot recently
                    double result = timestamp % 1.0;
                    if (result < .2) {
                        indexerSpeed = -.7;
                    } else if (result > .3 && result < .9) {
                        indexerSpeed = 1.0;
                    }
                } else {
                    indexerSpeed = 1.0;
                }
            } else if(intake){
                indexerSpeed = 1.0;
            }
        }
        if(control == Control.STORE || control == Control.STORE_AND_INTAKE){
            if(feederSpeed == null) {
                feederSpeed = 0.0;
            }
            if(indexerSpeed == null){
                indexerSpeed = 1.0;
            }
        }
        intakeMotor.set((intakeSpeed == null ? 0 : intakeSpeed) * 1.0);
        indexerMotor.set((indexerSpeed == null ? 0 : indexerSpeed) * .5);
        feederMotor.set((feederSpeed == null ? 0 : feederSpeed) * 1.0);
    }
}
