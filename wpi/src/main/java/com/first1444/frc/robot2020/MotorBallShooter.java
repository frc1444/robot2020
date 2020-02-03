package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.first1444.dashboard.shuffleboard.PropertyComponent;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
import com.first1444.dashboard.value.implementations.PropertyActiveComponent;
import com.first1444.frc.robot2020.subsystems.BallShooter;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.MutableValueMap;
import com.first1444.frc.util.valuemap.sendable.MutableValueMapSendable;

public class MotorBallShooter implements BallShooter {
    // real max is 6380
    private static final int MAX_RPM = 6300;
    private static final boolean VELOCITY_CONTROL = true;
    private final TalonFX talon;
    private final DashboardMap dashboardMap;

    private double speed;

    public MotorBallShooter(DashboardMap dashboardMap) {
        this.talon = new TalonFX(23);
        this.dashboardMap = dashboardMap;
        talon.configFactoryDefault(RobotConstants.INIT_TIMEOUT);
        talon.setInverted(InvertType.InvertMotorOutput);
        talon.setSensorPhase(true);
        talon.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, RobotConstants.PID_INDEX, RobotConstants.INIT_TIMEOUT);

//        talon.config_kP(RobotConstants.SLOT_INDEX, .003, RobotConstants.INIT_TIMEOUT);
//        talon.config_kI(RobotConstants.SLOT_INDEX, .00001, RobotConstants.INIT_TIMEOUT);
//        talon.config_kF(RobotConstants.SLOT_INDEX, .045, RobotConstants.INIT_TIMEOUT);

        var sendable = new MutableValueMapSendable<>(PidKey.class);
        var pidConfig = sendable.getMutableValueMap();
        pidConfig.setDouble(PidKey.CLOSED_RAMP_RATE, .25);
        pidConfig.setDouble(PidKey.P, .1); // .1
        pidConfig.setDouble(PidKey.I, .00014); // .00018

        CTREUtil.applyPID(talon, pidConfig, RobotConstants.INIT_TIMEOUT);
        pidConfig.addListener(key -> CTREUtil.applyPID(talon, pidConfig, RobotConstants.LOOP_TIMEOUT));

        dashboardMap.getDebugTab().add("Shooter PID", new SendableComponent<>(sendable));

        dashboardMap.getDebugTab().add("Velocity", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeDouble(talon.getSelectedSensorVelocity()))));
        dashboardMap.getDebugTab().add("Encoder", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeDouble(talon.getSelectedSensorPosition()))));
    }

    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public void run() {
        final double speed = this.speed;
        this.speed = 0;
        if(speed != 0 && VELOCITY_CONTROL){
            double velocity = speed * MAX_RPM * RobotConstants.TALON_FX_ENCODER_COUNTS_PER_REVOLUTION / (double) RobotConstants.CTRE_UNIT_CONVERSION;
            talon.set(ControlMode.Velocity, velocity);
            dashboardMap.getDebugTab().getRawDashboard().get("Desired Velocity").getStrictSetter().setDouble(velocity);
        } else {
            talon.set(ControlMode.PercentOutput, speed);
        }
    }
}
