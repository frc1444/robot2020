package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.first1444.dashboard.ActiveComponentMultiplexer;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
import com.first1444.dashboard.value.implementations.PropertyActiveComponent;
import com.first1444.frc.robot2020.subsystems.swerve.ModuleConfig;
import com.first1444.frc.robot2020.subsystems.swerve.SwerveModuleEvent;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.MutableValueMap;
import com.first1444.frc.util.valuemap.ValueMap;
import com.first1444.sim.api.MathUtil;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.drivetrain.swerve.SwerveModule;
import com.first1444.sim.api.event.Event;
import com.first1444.sim.api.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static com.first1444.sim.api.MeasureUtil.inchesToMeters;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class TalonSwerveModule implements SwerveModule {
    private static final int CLOSED_LOOP_TIME = 4;
    private static final double WHEEL_CIRCUMFERENCE_INCHES = 4 * Math.PI;
    private static final boolean VELOCITY_CONTROL = true;
    private static final double SMART_QUICK_REVERSE_RPM_THRESHOLD = RobotConstants.MAX_CIM_RPM / 2.0;

    private final String name;
    private final double quadCountsPerRevolution;

    private final BaseMotorController drive;
    private final SwerveSetup.DriveType driveType;
    private final TalonSRX steer;
    private final ValueMap<ModuleConfig> moduleConfig;

    private boolean quickReverseAllowed = true;
    private boolean isDisabled = false;
    private double speed = 0;
    private double targetPositionDegrees = 0;

    public TalonSwerveModule(
            String name, SwerveSetup.DriveType driveType, int driveId, int steerId, double quadCountsPerRevolution,
            MutableValueMap<PidKey> drivePid, MutableValueMap<PidKey> steerPid,
            MutableValueMap<ModuleConfig> moduleConfig, DashboardMap dashboardMap) {
        this.name = name;
        this.driveType = driveType;
        this.quadCountsPerRevolution = quadCountsPerRevolution;

        if(driveType == SwerveSetup.DriveType.CIM) {
            drive = new TalonSRX(driveId);
        } else if(driveType == SwerveSetup.DriveType.FALCON){
            drive = new TalonFX(driveId);
        } else {
            throw new UnsupportedOperationException("Unknown drive type: " + driveType);
        }
        steer = new TalonSRX(steerId);
        System.out.println("encoder " + name + " " + steer.getSensorCollection().getAnalogInRaw());
        this.moduleConfig = moduleConfig;

        drive.configFactoryDefault(RobotConstants.INIT_TIMEOUT);
        steer.configFactoryDefault(RobotConstants.INIT_TIMEOUT);

        drive.setNeutralMode(NeutralMode.Brake);
        steer.setNeutralMode(NeutralMode.Coast); // to make them easier to reposition when the robot is on
        drive.configClosedLoopPeriod(RobotConstants.SLOT_INDEX, CLOSED_LOOP_TIME, RobotConstants.INIT_TIMEOUT);

        steer.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, RobotConstants.PID_INDEX, RobotConstants.INIT_TIMEOUT);
//        steer.configSetParameter(ParamEnum.eFeedbackNotContinuous, 0, 0, 0, RobotConstants.INIT_TIMEOUT);
        steer.setSensorPhase(true);
        steer.configClosedLoopPeriod(RobotConstants.SLOT_INDEX, CLOSED_LOOP_TIME, RobotConstants.INIT_TIMEOUT);

        drivePid.addListener((key) -> CtreUtil.applyPid(drive, drivePid, RobotConstants.LOOP_TIMEOUT));
        steerPid.addListener((key) -> CtreUtil.applyPid(steer, steerPid, RobotConstants.LOOP_TIMEOUT));
        CtreUtil.applyPid(drive, drivePid, RobotConstants.INIT_TIMEOUT);
        CtreUtil.applyPid(steer, steerPid, RobotConstants.INIT_TIMEOUT);

        moduleConfig.addListener(option -> {
            updateEncoderOffset(moduleConfig);
        });
        updateEncoderOffset(moduleConfig);
        dashboardMap.getLiveWindow().add(name, (title, dashboard) -> new ActiveComponentMultiplexer(title, Collections.singletonList(
                new PropertyActiveComponent("", dashboard.get("raw analog encoder"), ValueProperty.createGetOnly(() -> BasicValue.makeDouble(steer.getSensorCollection().getAnalogInRaw())))
        )));
    }
    private void updateEncoderOffset(ValueMap<ModuleConfig> config){
        final double min = config.getDouble(ModuleConfig.MIN_ENCODER_VALUE);
        final double max = config.getDouble(ModuleConfig.MAX_ENCODER_VALUE);
        final double difference = max - min;
        final double currentPosition = (steer.getSensorCollection().getAnalogInRaw() - config.getDouble(ModuleConfig.ABS_ENCODER_OFFSET)) * getCountsPerRevolution() / difference;

        steer.setSelectedSensorPosition(
                (int) currentPosition,
                RobotConstants.PID_INDEX, RobotConstants.LOOP_TIMEOUT
        );
    }
    private double getDriveCountsPerRevolution(){
        if(driveType == SwerveSetup.DriveType.CIM){
            return RobotConstants.CIMCODER_COUNTS_PER_REVOLUTION;
        } else if(driveType == SwerveSetup.DriveType.FALCON){
            return RobotConstants.FALCON_ENCODER_COUNTS_PER_REVOLUTION;
        } else throw new UnsupportedOperationException("Unknown drive type: " + driveType);
    }

    @Override
    public void run() {
        if(isDisabled){
            steer.set(ControlMode.Disabled, 0);
            drive.set(ControlMode.Disabled, 0);
            return;
        }
        final double speed;
        final double targetPositionDegrees;
        {
            final double rawSpeed = this.speed;
            this.speed = 0;
            final double rawTargetPositionDegrees = this.targetPositionDegrees;
            if (quickReverseAllowed) {
                /*
                if rawSpeed is negative, we want to make it positive if we are using quick reverse because the algorithm below expects speed to be positive
                 */
                if(rawSpeed < 0){
                    speed = -rawSpeed;
                    targetPositionDegrees = rawTargetPositionDegrees + 180;
                } else {
                    speed = rawSpeed;
                    targetPositionDegrees = rawTargetPositionDegrees;
                }
            } else {
                speed = rawSpeed;
                targetPositionDegrees = rawTargetPositionDegrees;
            }
        }

        final double speedMultiplier;
        final double driveCountsPerRevolution = getDriveCountsPerRevolution();
        { // steer code

            final double wrap = getCountsPerRevolution(); // in encoder counts
            final int current = steer.getSelectedSensorPosition(RobotConstants.PID_INDEX);
            final double desired = Math.round(targetPositionDegrees * wrap / 360.0); // in encoder counts

            if(quickReverseAllowed){
                final double velocityRpm = drive.getSelectedSensorVelocity(RobotConstants.PID_INDEX) * RobotConstants.CTRE_UNIT_CONVERSION / driveCountsPerRevolution;
                final double newPosition;
                if(velocityRpm < -SMART_QUICK_REVERSE_RPM_THRESHOLD){ // wheel is going backwards, keep going backwards
                    newPosition = MathUtil.minChange(desired + wrap / 2.0, current, wrap) + current;
                    speedMultiplier = -1;
                } else if(velocityRpm > SMART_QUICK_REVERSE_RPM_THRESHOLD){ // wheel is going forwards, keep going forwards
                    newPosition = MathUtil.minChange(desired, current, wrap) + current;
                    speedMultiplier = 1;
                } else { // we don't care which way wheel goes
                    newPosition = MathUtil.minChange(desired, current, wrap / 2.0) + current;
                    if (MathUtil.minDistance(newPosition, desired, wrap) < .001) { // check if equal
                        speedMultiplier = 1;
                    } else {
                        speedMultiplier = -1;
                    }
                }
                steer.set(ControlMode.Position, newPosition);
            } else {
                speedMultiplier = 1;
                final double newPosition = MathUtil.minChange(desired, current, wrap) + current;
                steer.set(ControlMode.Position, newPosition);
            }
        }

        { // speed code
            if(VELOCITY_CONTROL){

                final double velocity = speed * speedMultiplier * driveCountsPerRevolution
                        * RobotConstants.MAX_CIM_RPM / RobotConstants.CTRE_UNIT_CONVERSION; // maybe we might want to make falcons faster (they can go faster)
                drive.set(ControlMode.Velocity, velocity);
            } else {
                drive.set(ControlMode.PercentOutput, speed * speedMultiplier);
            }
        }
    }


    @Override
    public void setTargetSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public double getDistanceTraveledMeters() {
        final double encoderCountsPerRevolution;
        if(driveType == SwerveSetup.DriveType.CIM){
            encoderCountsPerRevolution = RobotConstants.CIM_SWERVE_DRIVE_ENCODER_COUNTS_PER_REVOLUTION;
        } else if(driveType == SwerveSetup.DriveType.FALCON){
            encoderCountsPerRevolution = RobotConstants.FALCON_SWERVE_DRIVE_ENCODER_COUNTS_PER_REVOLUTION;
        } else throw new UnsupportedOperationException("Unknown drive type: " + driveType);

        final double currentDistance = drive.getSelectedSensorPosition(RobotConstants.PID_INDEX)
                * WHEEL_CIRCUMFERENCE_INCHES / encoderCountsPerRevolution;
        return inchesToMeters(currentDistance);
    }

    @Override
    public void setTargetAngle(@NotNull Rotation2 rotation2) {
        setTargetAngleDegrees(rotation2.getDegrees());
    }

    @Override
    public void setTargetAngleDegrees(double positionDegrees) {
        this.targetPositionDegrees = positionDegrees;
    }

    @Override
    public void setTargetAngleRadians(double angleRadians) {
        setTargetAngleDegrees(toDegrees(angleRadians));
    }

    @NotNull
    @Override
    public Rotation2 getCurrentAngle() {
        return Rotation2.fromDegrees(getCurrentAngleDegrees());
    }

    @Override
    public double getCurrentAngleDegrees() {
        final int encoderPosition = steer.getSelectedSensorPosition(RobotConstants.PID_INDEX);
        final double totalCounts = getCountsPerRevolution();
        return MathUtil.mod(encoderPosition * 360.0 / totalCounts, 360.0);
    }

    @Override
    public double getCurrentAngleRadians() {
        return toRadians(getCurrentAngleDegrees());
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }


    /** @return The number of encoder counts per revolution steer*/
    private double getCountsPerRevolution(){
        return quadCountsPerRevolution;
    }

    @NotNull
    @Override
    public EventHandler getEventHandler() {
        return eventHandler;
    }
    private final EventHandler eventHandler = new EventHandler() {
        private final Set<Event> eventSet = Collections.unmodifiableSet(EnumSet.of(SwerveModuleEvent.RECALIBRATE, SwerveModuleEvent.QUICK_REVERSE_ENABLED, SwerveModuleEvent.DISABLE));
        @Override
        public boolean canHandleEvent(@NotNull Event event) {
            return eventSet.contains(event);
        }

        @Override
        public boolean handleEvent(@NotNull Event event, @Nullable Object o) {
            if(event == SwerveModuleEvent.RECALIBRATE){
                updateEncoderOffset(moduleConfig);
                return true;
            }
            if(event == SwerveModuleEvent.QUICK_REVERSE_ENABLED){
                if(o instanceof Boolean){
                    quickReverseAllowed = (Boolean) o;
                } else {
                    System.err.println(o + " was passed for quick reversed enabled data."); // We could throw an exception, but this isn't something we need to crash the program for
                }
                return true;
            }
            if(event == SwerveModuleEvent.DISABLE){
                if(o instanceof Boolean){
                    isDisabled = (Boolean) o;
                } else {
                    System.err.println(o + " was passed for swerve module event disable data.");
                }
                return true;
            }
            return false;
        }

        @NotNull
        @Override
        public Set<Event> getEvents() {
            return eventSet;
        }
    };

}
