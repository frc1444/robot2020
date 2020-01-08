package com.first1444.frc.robot2020;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
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

    private final String name;
    private final int quadCountsPerRevolution;

    private final BaseMotorController drive;
    private final TalonSRX steer;
    private final ValueMap<ModuleConfig> moduleConfig;

    private boolean quickReverseAllowed = true;
    private boolean isDisabled = false;
    private double speed = 0;
    private double targetPositionDegrees = 0;

    public TalonSwerveModule(
        String name, int driveID, int steerID, int quadCountsPerRevolution,
        MutableValueMap<PidKey> drivePid, MutableValueMap<PidKey> steerPid,
        MutableValueMap<ModuleConfig> moduleConfig, DashboardMap dashboardMap) {
        this.name = name;
        this.quadCountsPerRevolution = quadCountsPerRevolution;

        drive = new TalonSRX(driveID);
        steer = new TalonSRX(steerID);
        System.out.println("encoder " + name + " " + steer.getSensorCollection().getAnalogInRaw());
        this.moduleConfig = moduleConfig;

        drive.configFactoryDefault(Constants.INIT_TIMEOUT);
        steer.configFactoryDefault(Constants.INIT_TIMEOUT);

        drive.setNeutralMode(NeutralMode.Brake);
        steer.setNeutralMode(NeutralMode.Coast); // to make them easier to reposition when the robot is on
        drive.configClosedLoopPeriod(Constants.SLOT_INDEX, CLOSED_LOOP_TIME, Constants.INIT_TIMEOUT);

        steer.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, Constants.PID_INDEX, Constants.INIT_TIMEOUT);
        steer.configSetParameter(ParamEnum.eFeedbackNotContinuous, 0, 0, 0, Constants.INIT_TIMEOUT);
        steer.setSensorPhase(true);
        steer.configClosedLoopPeriod(Constants.SLOT_INDEX, CLOSED_LOOP_TIME, Constants.INIT_TIMEOUT);

        drivePid.addListener((key) -> CTREUtil.applyPID(drive, drivePid, Constants.LOOP_TIMEOUT));
        steerPid.addListener((key) -> CTREUtil.applyPID(steer, steerPid, Constants.LOOP_TIMEOUT));
        CTREUtil.applyPID(drive, drivePid, Constants.INIT_TIMEOUT);
        CTREUtil.applyPID(steer, steerPid, Constants.INIT_TIMEOUT);

        moduleConfig.addListener(option -> {
            updateEncoderOffset(moduleConfig);
        });
        updateEncoderOffset(moduleConfig);
        dashboardMap.getLiveWindow().add(name, (title, dashboard) -> new ActiveComponentMultiplexer(title, Collections.singletonList(
                new PropertyActiveComponent("", dashboard.get("raw analog encoder"), ValueProperty.createGetOnly(() -> BasicValue.makeDouble(steer.getSensorCollection().getAnalogInRaw())))
        )));
    }
    private void updateEncoderOffset(ValueMap<ModuleConfig> config){
        final int min = (int) config.getDouble(ModuleConfig.MIN_ENCODER_VALUE);
        final int max = (int) config.getDouble(ModuleConfig.MAX_ENCODER_VALUE);
        final int difference = max - min;
        final int currentPosition = (steer.getSensorCollection().getAnalogInRaw() - (int)config.getDouble(ModuleConfig.ABS_ENCODER_OFFSET)) * getCountsPerRevolution() / difference;

        steer.setSelectedSensorPosition(
                currentPosition,
                Constants.PID_INDEX, Constants.LOOP_TIMEOUT
        );
    }

    @Override
    public void run() {
        if(isDisabled){
            steer.set(ControlMode.Disabled, 0);
            drive.set(ControlMode.Disabled, 0);
            return;
        }
        final double speedMultiplier;

        { // steer code
            final int wrap = getCountsPerRevolution(); // in encoder counts
            final int current = steer.getSelectedSensorPosition(Constants.PID_INDEX);
            final int desired = (int) Math.round(targetPositionDegrees * wrap / 360.0); // in encoder counts

            if(quickReverseAllowed){
                final int newPosition = (int) MathUtil.minChange(desired, current, wrap / 2.0) + current;
                if(MathUtil.minDistance(newPosition, desired, wrap) < .001){ // check if equal
                    speedMultiplier = 1;
                } else {
                    speedMultiplier = -1;
                }
                steer.set(ControlMode.Position, newPosition); // taking .6 ms to 1.7 ms
            } else {
                speedMultiplier = 1;
                final int newPosition = (int) MathUtil.minChange(desired, current, wrap) + current;
                steer.set(ControlMode.Position, newPosition);
            }
        }

        { // speed code
            if(VELOCITY_CONTROL){
                final double velocity = speed * speedMultiplier * Constants.CIMCODER_COUNTS_PER_REVOLUTION
                        * Constants.MAX_SWERVE_DRIVE_RPM / (double) Constants.CTRE_UNIT_CONVERSION;
                drive.set(ControlMode.Velocity, velocity); // taking .015 ms
            } else {
                drive.set(ControlMode.PercentOutput, speed * speedMultiplier);
            }
            speed = 0;
        }
    }


    @Override
    public void setTargetSpeed(double speed) {
        this.speed = speed;
        isDisabled = false;
    }


    @Override
    public double getDistanceTraveledMeters() {
        final double currentDistance = drive.getSelectedSensorPosition(Constants.PID_INDEX) // takes a long time - .9 ms to 5 ms
                * WHEEL_CIRCUMFERENCE_INCHES / (double) Constants.SWERVE_DRIVE_ENCODER_COUNTS_PER_REVOLUTION;
        return inchesToMeters(currentDistance);
    }

    @Override
    public void setTargetAngle(@NotNull Rotation2 rotation2) {
        setTargetAngleDegrees(rotation2.getDegrees());
    }

    @Override
    public void setTargetAngleDegrees(double positionDegrees) {
        this.targetPositionDegrees = positionDegrees;
        isDisabled = false;
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
        final int encoderPosition = steer.getSelectedSensorPosition(Constants.PID_INDEX);
        final int totalCounts = getCountsPerRevolution();
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
    private int getCountsPerRevolution(){
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
                isDisabled = true;
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
