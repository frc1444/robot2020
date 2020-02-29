package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.first1444.dashboard.shuffleboard.ComponentMetadataHelper;
import com.first1444.dashboard.shuffleboard.PropertyComponent;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
import com.first1444.frc.robot2020.setpoint.PIDController;
import com.first1444.frc.robot2020.subsystems.Turret;
import com.first1444.frc.robot2020.subsystems.implementations.BaseTurret;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.sendable.MutableValueMapSendable;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.Rotation2;

import static java.lang.Math.abs;

public class MotorTurret extends BaseTurret {
    private static final double OFFSET_ENCODER_DEGREES = -104;
    private static final double DEADZONE_DEGREES = 1.0;

    private final DashboardMap dashboardMap;

    private final TalonSRX talon;
    /** A REV Through Bore Encoder. The {@link DutyCycleEncoder#getDistancePerRotation()} is set correctly so you should call {@link DutyCycleEncoder#getDistance()} to get raw degrees */
    private final DutyCycleEncoder encoder;
    private final PIDController pidController;

    public MotorTurret(Clock clock, DashboardMap dashboardMap) {
        this.dashboardMap = dashboardMap;
        talon = new TalonSRX(RobotConstants.CAN.TURRET);
        talon.configFactoryDefault(RobotConstants.INIT_TIMEOUT);
        talon.configOpenloopRamp(.25);
        talon.setInverted(InvertType.InvertMotorOutput);

        talon.configVoltageCompSaturation(10.0, RobotConstants.INIT_TIMEOUT);
        talon.enableVoltageCompensation(true); // make sure to configure the saturation voltage before this
        encoder = new DutyCycleEncoder(RobotConstants.DIO.TURRET_ENCODER);
        encoder.setDistancePerRotation(-180);

        pidController = new PIDController(clock, 0, 0, 0);

        final var sendable = new MutableValueMapSendable<>(PidKey.class);
        final var pidConfig = sendable.getMutableValueMap();
        pidConfig.setDouble(PidKey.P, 0.36);
        pidConfig.setDouble(PidKey.I, 0.02);
        pidConfig.setDouble(PidKey.D, 0.04);

        pidController.applyFrom(pidConfig);
        pidConfig.addListener(key -> pidController.applyFrom(pidConfig));

        dashboardMap.getDebugTab().add("Turret PID", new SendableComponent<>(sendable));
        dashboardMap.getDebugTab().add("Turret Raw Degrees", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeDouble(getRotationDegreesRaw()))));
        dashboardMap.getDebugTab().add("Turret Degrees", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeDouble(getRotationDegrees()))));
        dashboardMap.getUserTab().add(
                "Turret Encoder",
                new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeBoolean(encoder.isConnected()))),
                (metadata) -> new ComponentMetadataHelper(metadata).setPosition(2, 4).setSize(1, 1)
        );
    }
    @Override
    protected void run(DesiredState desiredState) {
        Rotation2 rotation = desiredState.getDesiredRotation();
        boolean encoderConnected = encoder.isConnected();
//        if(!encoderConnected){
//            System.out.println("Turret Encoder is not connected!");
//        }
        if(rotation != null){
            if(!encoderConnected){
                talon.set(ControlMode.PercentOutput, 0.0);
            } else {
                double desiredDegrees = rotation.getDegrees();
                double currentDegrees = getRotationDegrees();
                if (abs(desiredDegrees - currentDegrees) < DEADZONE_DEGREES) {
                    talon.set(ControlMode.PercentOutput, 0.0);
                } else {
                    double output = pidController.calculate(currentDegrees, desiredDegrees);
                    setOutputSpeed(output, true);
                }
            }
        } else {
            double speed = desiredState.getRawSpeedCounterClockwise() * .8;
            setOutputSpeed(speed, encoderConnected);
            dashboardMap.getDebugTab().getRawDashboard().get("Turret Desired").getForceSetter().setDouble(speed);
        }
//        dashboardMap.getDebugTab().getRawDashboard().get("Turret Selected Counts").getForceSetter().setDouble(talon.getSelectedSensorPosition());
//        dashboardMap.getDebugTab().getRawDashboard().get("Turret Encoder Counts").getForceSetter().setDouble(talon.getSensorCollection().getPulseWidthPosition());
    }
    private void setOutputSpeed(double speed, boolean limit){
        if(limit) {
            double currentDegrees = getRotationDegrees();
            if ((speed > 0 && currentDegrees > Turret.MAX_ROTATION.getDegrees()) || (speed < 0 && currentDegrees < Turret.MIN_ROTATION.getDegrees())) {
                talon.set(ControlMode.PercentOutput, 0.0);
                return;
            }
        }
        talon.set(ControlMode.PercentOutput, speed);
    }
    private double getRotationDegreesRaw(){
        return encoder.getDistance();
    }
    private double getRotationDegrees(){
        return Math.IEEEremainder(getRotationDegreesRaw() - OFFSET_ENCODER_DEGREES, 360.0);
    }
    @Override
    public Rotation2 getCurrentRotation() {
        return Rotation2.fromDegrees(getRotationDegrees());
    }
}
