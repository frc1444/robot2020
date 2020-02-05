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
    private static final int OFFSET_ENCODER_COUNTS = 4155;


    private final TalonSRX talon;

    public MotorTurret(DashboardMap dashboardMap) {
        talon = new TalonSRX(RobotConstants.CAN.TURRET);
        talon.configFactoryDefault(RobotConstants.INIT_TIMEOUT);
        talon.configSelectedFeedbackSensor(TalonSRXFeedbackDevice.CTRE_MagEncoder_Absolute, RobotConstants.PID_INDEX, RobotConstants.INIT_TIMEOUT);

        var sendable = new MutableValueMapSendable<>(PidKey.class);
        var pidConfig = sendable.getMutableValueMap();
        pidConfig.setDouble(PidKey.P, .4);
        pidConfig.setDouble(PidKey.I, .0004);

        CtreUtil.applyPid(talon, pidConfig, RobotConstants.INIT_TIMEOUT);
        pidConfig.addListener(key -> CtreUtil.applyPid(talon, pidConfig, RobotConstants.LOOP_TIMEOUT));

        dashboardMap.getDebugTab().add("Turret PID", new SendableComponent<>(sendable));

    }
    @Override
    protected void run(DesiredState desiredState) {
        Rotation2 rotation = desiredState.getDesiredRotation();
        if(rotation != null){
            double rotationDegrees = rotation.getDegrees();
            double desiredEncoderCounts = degreesToEncoder(rotationDegrees) + OFFSET_ENCODER_COUNTS;
            talon.set(
                    ControlMode.Position,
                    desiredEncoderCounts
            );
        } else {
            double speed = desiredState.getRawSpeedCounterClockwise() * .3;
            talon.set(ControlMode.PercentOutput, speed);
        }
    }
    private double encoderToDegrees(double encoderCounts){
        return encoderCounts / ENCODER_COUNTS_PER_DEGREE;
    }
    private double degreesToEncoder(double degrees){
        return degrees * ENCODER_COUNTS_PER_DEGREE;
    }
    @Override
    public Rotation2 getCurrentRotation() {
        return Rotation2.fromDegrees((talon.getSelectedSensorPosition() - OFFSET_ENCODER_COUNTS) / ENCODER_COUNTS_PER_DEGREE);
    }
}
