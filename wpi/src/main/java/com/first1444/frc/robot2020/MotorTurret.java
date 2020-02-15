package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.frc.robot2020.setpoint.PIDController;
import com.first1444.frc.robot2020.subsystems.implementations.BaseTurret;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.sendable.MutableValueMapSendable;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.Rotation2;
import edu.wpi.first.wpilibj.DutyCycleEncoder;

public class MotorTurret extends BaseTurret {
    private static final double OFFSET_ENCODER_DEGREES = 32.0; // TODO get this value

    private final DashboardMap dashboardMap;

    private final TalonSRX talon;
    private final DutyCycleEncoder encoder;
    private final PIDController pidController;

    public MotorTurret(Clock clock, DashboardMap dashboardMap) {
        this.dashboardMap = dashboardMap;
        talon = new TalonSRX(RobotConstants.CAN.TURRET);
        talon.configFactoryDefault(RobotConstants.INIT_TIMEOUT);
        talon.configOpenloopRamp(.25);

        talon.configVoltageCompSaturation(10.0, RobotConstants.INIT_TIMEOUT); // TODO see what the best value for new motor is
        talon.enableVoltageCompensation(true); // make sure to configure the saturation voltage before this
        encoder = new DutyCycleEncoder(0);
        encoder.setDistancePerRotation(180); // TODO make negative if we need to

        pidController = new PIDController(clock, 0, 0, 0);

        final var sendable = new MutableValueMapSendable<>(PidKey.class);
        final var pidConfig = sendable.getMutableValueMap();
        pidConfig.setDouble(PidKey.P, 0.0);
        pidConfig.setDouble(PidKey.I, 0.0);
        pidConfig.setDouble(PidKey.D, 0.0);

        pidConfig.addListener(key -> pidController.setPID(pidConfig.getDouble(PidKey.P), pidConfig.getDouble(PidKey.I), pidConfig.getDouble(PidKey.D)));

        dashboardMap.getDebugTab().add("Turret PID", new SendableComponent<>(sendable));
    }
    @Override
    protected void run(DesiredState desiredState) {
        Rotation2 rotation = desiredState.getDesiredRotation();
        if(rotation != null){
            double desiredDegrees = rotation.getDegrees();
            double currentDegrees = getRotationDegrees();
            double output = pidController.calculate(currentDegrees, desiredDegrees);
            talon.set(ControlMode.PercentOutput, output);
//            dashboardMap.getDebugTab().getRawDashboard().get("Turret Desired").getForceSetter().setString("Counts=" + desiredEncoderCounts);
        } else {
            double speed = desiredState.getRawSpeedCounterClockwise() * .8;
            talon.set(ControlMode.PercentOutput, speed);
            dashboardMap.getDebugTab().getRawDashboard().get("Turret Desired").getForceSetter().setDouble(speed);
        }
//        dashboardMap.getDebugTab().getRawDashboard().get("Turret Selected Counts").getForceSetter().setDouble(talon.getSelectedSensorPosition());
//        dashboardMap.getDebugTab().getRawDashboard().get("Turret Encoder Counts").getForceSetter().setDouble(talon.getSensorCollection().getPulseWidthPosition());
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
