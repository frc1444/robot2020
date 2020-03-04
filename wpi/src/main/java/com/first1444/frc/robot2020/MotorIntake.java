package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.first1444.dashboard.shuffleboard.PropertyComponent;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
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

    private boolean transferring = false;
    private Double lastTransferSensorDetect = null;

    private double currentVelocity = 0;
    private boolean wasIntakeSensor = false;
    private Double lastUpdate = null;

    private Double lastActiveTime = null;

    public MotorIntake(Clock clock, BallTracker ballTracker, DashboardMap dashboardMap) {
        this.clock = clock;
        this.ballTracker = ballTracker;
        this.dashboardMap = dashboardMap;
        intakeMotor = new WPI_VictorSPX(RobotConstants.CAN.INTAKE);
        indexerMotor = new CANSparkMax(RobotConstants.CAN.INDEXER, INDEXER_TYPE);
        feederMotor = new CANSparkMax(RobotConstants.CAN.FEEDER, FEEDER_TYPE);
        sensorArray = new SensorArray();
        dashboardMap.getDevTab().add("Intake Sensor", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeBoolean(sensorArray.isIntakeSensor()))));
        dashboardMap.getDevTab().add("Transfer Sensor", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeBoolean(sensorArray.isTransferSensor()))));
        dashboardMap.getDevTab().add("Feeder Sensor", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeBoolean(sensorArray.isFeederSensor()))));

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
    private double getAntiJamIndexerSpeed(double timestamp){
        double result = (timestamp % ANTI_JAM_PERIOD) / ANTI_JAM_PERIOD;
        if (result < .25) {
            return -1.0;
        } else if (result > .3 && result < .95) {
            return 1.0;
        }
        return 0.0;
    }

    @Override
    protected void run(Control control, Double intakeSpeed, Double indexerSpeed, Double feederSpeed) {
        boolean intake = control == Control.INTAKE_AND_ACTIVE_STORE || control == Control.FEED_ALL_AND_INTAKE;
        if(intake){
            if(intakeSpeed == null) {
                intakeSpeed = 1.0;
            }
        }
        if(control == Control.ACTIVE_STORE || control == Control.STORE || control == Control.INTAKE_AND_ACTIVE_STORE){
            int ballCount = ballTracker.getBallCount();
            if(ballCount >= 1){
                if(!sensorArray.isFeederSensor()){
                    if(transferring){
                        if(feederSpeed == null) {
                            feederSpeed = 1.0;
                        }
                        if(sensorArray.isTransferSensor()){
                            if(indexerSpeed == null) {
                                indexerSpeed = 1.0;
                            }
                        }
                    } else {
                        if(sensorArray.isTransferSensor()){
                            transferring = true;
                        }
                        if(indexerSpeed == null) {
                            indexerSpeed = 1.0;
                        }
                    }
                } else {
                    transferring = false;
                    if (ballCount >= 2) {
                        if (!sensorArray.isTransferSensor()) {
                            if (indexerSpeed == null) {
                                indexerSpeed = 1.0;
                            }
                        }
                    }
                }
            }
        } else {
            transferring = false;
        }
        if(sensorArray.isTransferSensor()){
            lastTransferSensorDetect = clock.getTimeSeconds();
        }
        if(indexerSpeed == null) {
            if (control == Control.FEED_ALL_AND_INTAKE) {
                feederSpeed = 1.0;
                double timestamp = clock.getTimeSeconds();
                Double lastShootTime = ballTracker.getLastShootTimestamp();
                int ballCount = ballTracker.getBallCount();
                Double lastTransferSensorDetect = this.lastTransferSensorDetect;
                if ((lastTransferSensorDetect != null && clock.getTimeSeconds() - lastTransferSensorDetect < .3) && (ballCount >= 4 || (ballCount >= 2 && (lastShootTime == null || timestamp - lastShootTime > 2.0)))) { // more than two balls and we haven't shot recently
                    indexerSpeed = getAntiJamIndexerSpeed(timestamp);
                } else {
                    indexerSpeed = 1.0;
                }
            } else if(intake){
                indexerSpeed = 1.0;
            }
        }
        if(control == Control.ACTIVE_STORE || control == Control.INTAKE_AND_ACTIVE_STORE){
            if(indexerSpeed == null){
                indexerSpeed = 1.0;
            }
        }
        if(intakeSpeed == null){
            intakeSpeed = 0.0;
        }
        if(indexerSpeed == null){
            indexerSpeed = 0.0;
        }
        if(feederSpeed == null){
            feederSpeed = 0.0;
        }
        intakeMotor.set(intakeSpeed * 1.0);
        indexerMotor.set(indexerSpeed * .5);
        feederMotor.set(feederSpeed * 1.0);

        boolean idle = intakeSpeed == 0.0 && indexerSpeed == 0.0 && feederSpeed == 0.0;
        if(!idle){
            lastActiveTime = clock.getTimeSeconds();
        }

        updateBallEnter(indexerSpeed);
    }

    private void updateBallEnter(double speed) {
        double timestamp = clock.getTimeSeconds();
        final Double lastTimestamp = lastUpdate;
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
            dashboardMap.getDebugTab().getRawDashboard().get("current indexer velocity").getStrictSetter().setDouble(currentVelocity);

            final boolean isIntake = sensorArray.isIntakeSensor();
            final boolean wasSensor = wasIntakeSensor;
            wasIntakeSensor = isIntake;
            if(isIntake && !wasSensor){
                System.out.println("Intake sensor on");
                if(currentVelocity >= 0){ // we're intaking
                    if(currentVelocity > .2 || ballTracker.getBallCount() < 5) {
                        ballTracker.addBall();
                        System.out.println("Added ball");
                    } else {
                        System.out.println("Not adding ball. Hopefully we made the right choice");
                    }
                }
            } else if(!isIntake && wasSensor){
                System.out.println("Intake sensor off");
                if(currentVelocity <= 0){ // we're spitting out
                    ballTracker.removeBall();
                    System.out.println("Split out a ball");
                }
            }
        }
        Double lastActiveTime = this.lastActiveTime;
        if(lastActiveTime == null || clock.getTimeSeconds() - lastActiveTime > 3.0) {
            int count = 0;
            if (sensorArray.isIntakeSensor()) {
                count++;
            }
            if (sensorArray.isTransferSensor()) {
                count++;
            }
            if (sensorArray.isFeederSensor()) {
                count++;
            }
            if (ballTracker.getBallCount() < count) {
                System.out.println("Setting ball count to " + count);
                ballTracker.setBallCount(count);
            }
        }
    }
}
