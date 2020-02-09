package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.frc.robot2020.subsystems.implementations.BaseTurret;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.sendable.MutableValueMapSendable;
import com.first1444.sim.api.Rotation2;

public class MotorTurret extends BaseTurret {
    private static final int ENCODER_COUNTS_90 = 1933;
    private static final double ENCODER_COUNTS_PER_DEGREE = ENCODER_COUNTS_90 / 90.0;
    private static final int OFFSET_ENCODER_COUNTS = 15;

    private final DashboardMap dashboardMap;

    private final TalonSRX talon;

    public MotorTurret(DashboardMap dashboardMap) {
        this.dashboardMap = dashboardMap;
        talon = new TalonSRX(RobotConstants.CAN.TURRET);
        talon.configFactoryDefault(RobotConstants.INIT_TIMEOUT);
        talon.configSelectedFeedbackSensor(TalonSRXFeedbackDevice.CTRE_MagEncoder_Absolute, RobotConstants.PID_INDEX, RobotConstants.INIT_TIMEOUT);
        talon.configOpenloopRamp(.25);

        talon.configVoltageCompSaturation(4.0, RobotConstants.INIT_TIMEOUT);
        talon.enableVoltageCompensation(true); // make sure to configure the saturation voltage before this
        talon.setSelectedSensorPosition((int) Math.IEEEremainder(talon.getSensorCollection().getPulseWidthPosition() - OFFSET_ENCODER_COUNTS, 4096));

        var sendable = new MutableValueMapSendable<>(PidKey.class);
        var pidConfig = sendable.getMutableValueMap();
        pidConfig.setDouble(PidKey.P, 7.0);
        pidConfig.setDouble(PidKey.I, .001);
        pidConfig.setDouble(PidKey.D, .004);

        CtreUtil.applyPid(talon, pidConfig, RobotConstants.INIT_TIMEOUT);
//        pidConfig.addListener(key -> CtreUtil.applyPid(talon, pidConfig, RobotConstants.LOOP_TIMEOUT));

//        dashboardMap.getDebugTab().add("Turret PID", new SendableComponent<>(sendable));

    }
    @Override
    protected void run(DesiredState desiredState) {
//        int rawEncoderCounts = talon.getSensorCollection().getPulseWidthPosition();
        Rotation2 rotation = desiredState.getDesiredRotation();
        if(rotation != null){
            double rotationDegrees = rotation.getDegrees();
            double desiredEncoderCounts = degreesToEncoder(rotationDegrees);
            talon.set(
                    ControlMode.Position,
                    desiredEncoderCounts
            );
//            dashboardMap.getDebugTab().getRawDashboard().get("Turret Desired").getForceSetter().setString("Counts=" + desiredEncoderCounts);
        } else {
            double speed = desiredState.getRawSpeedCounterClockwise() * .8;
            talon.set(ControlMode.PercentOutput, speed);
//            dashboardMap.getDebugTab().getRawDashboard().get("Turret Desired").getForceSetter().setDouble(speed);
        }
//        dashboardMap.getDebugTab().getRawDashboard().get("Turret Selected Counts").getForceSetter().setDouble(talon.getSelectedSensorPosition());
//        dashboardMap.getDebugTab().getRawDashboard().get("Turret Encoder Counts").getForceSetter().setDouble(talon.getSensorCollection().getPulseWidthPosition());
    }
    private double encoderToDegrees(double encoderCounts){
        return encoderCounts / ENCODER_COUNTS_PER_DEGREE;
    }
    private double degreesToEncoder(double degrees){
        return degrees * ENCODER_COUNTS_PER_DEGREE;
    }
    @Override
    public Rotation2 getCurrentRotation() {
        return Rotation2.fromDegrees(talon.getSelectedSensorPosition() / ENCODER_COUNTS_PER_DEGREE);
    }
}
