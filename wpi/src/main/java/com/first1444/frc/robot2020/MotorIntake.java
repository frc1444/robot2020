package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.first1444.dashboard.shuffleboard.PropertyComponent;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
import com.first1444.frc.robot2020.subsystems.balltrack.BallTracker;
import com.first1444.frc.robot2020.subsystems.implementations.BaseIntake;
import com.first1444.sim.api.Clock;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

import static java.util.Objects.requireNonNull;

public class MotorIntake extends BaseIntake {
    private static final double ANTI_JAM_PERIOD = 1.2;
    private static final CANSparkMaxLowLevel.MotorType INDEXER_TYPE = CANSparkMaxLowLevel.MotorType.kBrushless;
    private static final CANSparkMaxLowLevel.MotorType FEEDER_TYPE = CANSparkMaxLowLevel.MotorType.kBrushless;
    private enum TransferState {
        IDLE,
        RUN_BOTH,
        FEEDER_ONLY
    }

    private final Clock clock;
    private final BallTracker ballTracker;
    private final DashboardMap dashboardMap;

    private final WPI_VictorSPX intakeMotor;
    private final CANSparkMax indexerMotor;
    private final CANSparkMax feederMotor;

    private final SensorArray sensorArray;

    private TransferState transferState = TransferState.IDLE;
    private Double lastTransferSensorDetect = null;

    private double currentVelocity = 0;
    private boolean wasIntakeSensor = false;
    private Double lastUpdate = null;

    private Integer desiredCount = null;
    private Double desiredCountStartTime = null;

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
        if(control == Control.INTAKE || control == Control.STORE){
            int ballCount = ballTracker.getBallCount();
            if(ballCount >= 1){
                if(!sensorArray.isFeederSensor()){
                    TransferState currentTransferState = transferState;
                    if(currentTransferState == TransferState.FEEDER_ONLY){
                        if(indexerSpeed == null){
                            indexerSpeed = 0.0; // we don't want to run the indexer while we're transferring the ball up
                        }
                        if(feederSpeed == null) {
                            feederSpeed = 1.0;
                        }
                    } else if(currentTransferState == TransferState.RUN_BOTH){
                        if(indexerSpeed == null) {
                            indexerSpeed = 1.0;
                        }
                        if(feederSpeed == null) {
                            feederSpeed = 1.0;
                        }
                        if(!sensorArray.isTransferSensor()){
                            transferState = TransferState.FEEDER_ONLY;
                        }
                    } else {
                        assert currentTransferState == TransferState.IDLE;
                        if(sensorArray.isTransferSensor()){
                            transferState = TransferState.RUN_BOTH;
                        }
                        // next iteration it will do what we want, we don't care right now
                    }
                } else {
                    transferState = TransferState.IDLE;
                    // for Aaron's idea of how this should work, we don't need this code below
//                    if (ballCount >= 2) {
//                        if (!sensorArray.isTransferSensor()) {
//                            if (indexerSpeed == null) {
//                                indexerSpeed = 1.0;
//                            }
//                        }
//                    }
                }
            }
        } else {
            transferState = TransferState.IDLE;
        }
        if(sensorArray.isTransferSensor()){
            lastTransferSensorDetect = clock.getTimeSeconds();
        }
        if (control == Control.FEED_ALL_AND_INTAKE) {
            feederSpeed = 1.0;
            double timestamp = clock.getTimeSeconds();
            Double lastShootTime = ballTracker.getLastShootTimestamp();
            int ballCount = ballTracker.getBallCount();
            Double lastTransferSensorDetect = this.lastTransferSensorDetect;
            if ((lastTransferSensorDetect != null && clock.getTimeSeconds() - lastTransferSensorDetect < .3) && (ballCount >= 4 || (ballCount >= 2 && (lastShootTime == null || timestamp - lastShootTime > 2.0)))) { // more than two balls and we haven't shot recently
                double speed = getAntiJamIndexerSpeed(timestamp);
                if(intakeSpeed == null){
                    intakeSpeed = speed;
                }
                if(indexerSpeed == null) {
                    indexerSpeed = speed;
                }
            } else {
                if(intakeSpeed == null) {
                    intakeSpeed = 1.0;
                }
                if(indexerSpeed == null) {
                    indexerSpeed = 1.0;
                }
            }
        } else if(control == Control.INTAKE){
            if(intakeSpeed == null) {
                intakeSpeed = 1.0;
            }
        }
        if(sensorArray.isIntakeSensor()) {
            if(intakeSpeed == null) {
                intakeSpeed = 1.0;
            }
            if(indexerSpeed == null) {
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

        updateBallEnter(indexerSpeed);
    }

    private void updateBallEnter(double speed) {
        // TODO update this function with new sensor placement
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
        {
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
                Integer lastDesiredCount = this.desiredCount;
                if(lastDesiredCount == null || lastDesiredCount != count){
                    this.desiredCount = count;
                    this.desiredCountStartTime = clock.getTimeSeconds();
                } else {
                    Double desiredCountStartTime = this.desiredCountStartTime;
                    requireNonNull(desiredCountStartTime, "This shouldn't be null");
                    if(clock.getTimeSeconds() - desiredCountStartTime > 1.0){
                        System.out.println("Setting ball count to " + count);
                        ballTracker.setBallCount(count);
                    }
                }
            } else {
                this.desiredCount = null;
                this.desiredCountStartTime = null;
            }
        }
    }
}
