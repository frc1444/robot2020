package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.first1444.frc.robot2020.subsystems.BallShooter;

public class MotorBallShooter implements BallShooter {
    private final TalonSRX talon;

    private double speed;

    public MotorBallShooter() {
        this.talon = new TalonSRX(22);
        talon.configFactoryDefault(RobotConstants.INIT_TIMEOUT);
        talon.setInverted(InvertType.InvertMotorOutput);
        // we'll probably also have to invert the sensor phase too
    }

    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public void run() {
        final double speed = this.speed;
        this.speed = 0;
        talon.set(ControlMode.PercentOutput, speed);
    }
}
