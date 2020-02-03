package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.first1444.frc.robot2020.subsystems.implementations.BaseTurret;
import com.first1444.sim.api.Rotation2;

public class MotorTurret extends BaseTurret {

    private final TalonSRX talon;

    public MotorTurret() {
        talon = new TalonSRX(RobotConstants.CAN.TURRET);
        talon.configFactoryDefault(RobotConstants.INIT_TIMEOUT);

    }

    @Override
    protected void run(DesiredState desiredState) {
        Rotation2 rotation = desiredState.getDesiredRotation();
        if(rotation != null){
            // TODO set desired rotation
        } else {
            double speed = desiredState.getRawSpeedCounterClockwise() * .3;
            talon.set(ControlMode.PercentOutput, speed);
        }
    }
}
