package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.first1444.frc.robot2020.setpoint.PIDController;
import com.first1444.frc.robot2020.subsystems.balltrack.BallTracker;
import com.first1444.frc.robot2020.subsystems.implementations.BaseIntake;
import com.first1444.sim.api.Clock;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

public class MotorIntake extends BaseIntake {
    private static final double ANTI_JAM_PERIOD = 1.2;
    private static final CANSparkMaxLowLevel.MotorType INDEXER_TYPE = CANSparkMaxLowLevel.MotorType.kBrushless;
    private static final CANSparkMaxLowLevel.MotorType FEEDER_TYPE = CANSparkMaxLowLevel.MotorType.kBrushless;

    private final Clock clock;
    private final BallTracker ballTracker;
    private final DashboardMap dashboardMap;

    private final WPI_VictorSPX intakeMotor;
    private final CANSparkMax indexerMotor;
    private final CANSparkMax feederMotor;

    private final SensorArray sensorArray;

    private double currentVelocity = 0;
    private boolean wasIntakeSensor = false;
    private Double lastUpdate = null;

    public MotorIntake(Clock clock, BallTracker ballTracker, DashboardMap dashboardMap) {
        this.clock = clock;
        this.ballTracker = ballTracker;
        this.dashboardMap = dashboardMap;
        intakeMotor = new WPI_VictorSPX(RobotConstants.CAN.INTAKE);
        indexerMotor = new CANSparkMax(RobotConstants.CAN.INDEXER, INDEXER_TYPE);
        feederMotor = new CANSparkMax(RobotConstants.CAN.FEEDER, FEEDER_TYPE);
        sensorArray = new SensorArray();

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
        boolean intake = control == Control.INTAKE_AND_ACTIVE_STORE || control == Control.FEED_ALL_AND_INTAKE;
        if(intake){
            if(intakeSpeed == null) {
                intakeSpeed = 1.0;
            }
        }
        if(indexerSpeed == null) {
            if (control == Control.FEED_ALL_AND_INTAKE) {
                feederSpeed = 1.0;
                double timestamp = clock.getTimeSeconds();
                Double lastShootTime = ballTracker.getLastShootTimestamp();
                if (ballTracker.getBallCount() >= 3 && sensorArray.isTransferSensor() && (lastShootTime == null || timestamp - lastShootTime > 2.0)) { // more than two balls and we haven't shot recently
                    double result = (timestamp % ANTI_JAM_PERIOD) / ANTI_JAM_PERIOD;
                    if (result < .25) {
                        indexerSpeed = -1.0;
                    } else if (result > .3 && result < .95) {
                        indexerSpeed = 1.0;
                    } else {
                        indexerSpeed = 0.0;
                    }
                } else {
                    indexerSpeed = 1.0;
                }
            } else if(intake){
                indexerSpeed = 1.0;
            }
        }
        if(control == Control.ACTIVE_STORE || control == Control.STORE){
            int ballCount = ballTracker.getBallCount();
            if(ballCount >= 1){
                if(!sensorArray.isFeederSensor()){
                    if(feederSpeed == null) {
                        feederSpeed = 1.0;
                    }
                    if(indexerSpeed == null) {
                        indexerSpeed = 1.0;
                    }
                } else if(ballCount >= 2){
                    if(!sensorArray.isTransferSensor()){
                        if(indexerSpeed == null) {
                            indexerSpeed = 1.0;
                        }
                    }
                }
            }
        }
        if(control == Control.ACTIVE_STORE || control == Control.INTAKE_AND_ACTIVE_STORE){
            if(indexerSpeed == null){
                indexerSpeed = 1.0;
            }
        }
        intakeMotor.set((intakeSpeed == null ? 0 : intakeSpeed) * 1.0);
        indexerMotor.set((indexerSpeed == null ? 0 : indexerSpeed) * .5);
        feederMotor.set((feederSpeed == null ? 0 : feederSpeed) * 1.0);

        updateBallEnter(indexerSpeed == null ? 0 : indexerSpeed);
    }

    private void updateBallEnter(double speed) {
        double timestamp = clock.getTimeSeconds();
        Double lastTimestamp = lastUpdate;
        lastUpdate = timestamp;
        if(lastTimestamp != null){
            double delta = timestamp - lastTimestamp;

            double currentVelocity = this.currentVelocity;
            double error = speed - currentVelocity;
            double change = Math.signum(error) * delta / .2;
            currentVelocity += change;
            if(Math.signum(error) != Math.signum(speed - currentVelocity)){
                currentVelocity = speed;
            }
            this.currentVelocity = currentVelocity;

            final boolean isIntake = sensorArray.isIntakeSensor();
            final boolean wasSensor = wasIntakeSensor;
            wasIntakeSensor = isIntake;
            if(isIntake && !wasSensor){
                if(currentVelocity >= 0){ // we're intaking
                    if(currentVelocity > .2 || ballTracker.getBallCount() < 5) {
                        ballTracker.addBall();
                        System.out.println("Added ball");
                    } else {
                        System.out.println("Not adding ball. Hopefully we made the right choice");
                    }
                }
            } else if(!isIntake && wasSensor){
                if(currentVelocity <= 0){ // we're spitting out
                    ballTracker.removeBall();
                    System.out.println("Split out a ball");
                }
            }
        }
    }
}
