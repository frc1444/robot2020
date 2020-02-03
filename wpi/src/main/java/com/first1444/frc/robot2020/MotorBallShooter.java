package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.first1444.dashboard.shuffleboard.PropertyComponent;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
import com.first1444.frc.robot2020.subsystems.BallShooter;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.sendable.MutableValueMapSendable;

import static com.first1444.frc.robot2020.CtreUtil.rpmToNative;

public class MotorBallShooter implements BallShooter {
    private static final boolean VELOCITY_CONTROL = true;
    private final TalonFX talon;
    private final DashboardMap dashboardMap;

    private double rpm;

    public MotorBallShooter(DashboardMap dashboardMap) {
        this.talon = new TalonFX(RobotConstants.CAN.SHOOTER);
        this.dashboardMap = dashboardMap;
        talon.configFactoryDefault(RobotConstants.INIT_TIMEOUT);
        talon.setInverted(InvertType.InvertMotorOutput);
        talon.setSensorPhase(true);
        talon.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, RobotConstants.PID_INDEX, RobotConstants.INIT_TIMEOUT);

        var sendable = new MutableValueMapSendable<>(PidKey.class);
        var pidConfig = sendable.getMutableValueMap();
        pidConfig.setDouble(PidKey.CLOSED_RAMP_RATE, .25);
        pidConfig.setDouble(PidKey.P, .1);
        pidConfig.setDouble(PidKey.I, .00014);

        CtreUtil.applyPid(talon, pidConfig, RobotConstants.INIT_TIMEOUT);
        pidConfig.addListener(key -> CtreUtil.applyPid(talon, pidConfig, RobotConstants.LOOP_TIMEOUT));

        dashboardMap.getDebugTab().add("Shooter PID", new SendableComponent<>(sendable));

        dashboardMap.getDebugTab().add("Velocity", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeDouble(talon.getSelectedSensorVelocity()))));
        dashboardMap.getDebugTab().add("Encoder", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeDouble(talon.getSelectedSensorPosition()))));
    }

    @Override
    public void setRpm(double rpm) {
        this.rpm = rpm;
    }

    @Override
    public void run() {
        final double rpm = this.rpm;
        this.rpm = 0;
        if(rpm != 0 && VELOCITY_CONTROL){
//            double velocity = rpm * RobotConstants.TALON_FX_ENCODER_COUNTS_PER_REVOLUTION / (double) RobotConstants.CTRE_UNIT_CONVERSION;
            double velocity = rpmToNative(rpm, RobotConstants.TALON_FX_ENCODER_COUNTS_PER_REVOLUTION);
            talon.set(ControlMode.Velocity, velocity);
            dashboardMap.getDebugTab().getRawDashboard().get("Desired Velocity").getStrictSetter().setDouble(velocity);
        } else {
            talon.set(ControlMode.PercentOutput, rpm / BallShooter.MAX_RPM);
        }
    }
}
