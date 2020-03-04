package com.first1444.frc.robot2020;

import com.first1444.dashboard.shuffleboard.PropertyComponent;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
import com.first1444.frc.robot2020.subsystems.Climber;
import com.first1444.frc.robot2020.subsystems.implementations.BaseClimber;
import com.first1444.sim.api.Clock;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

public class MotorClimber extends BaseClimber {
    private final CANSparkMax motor;
    private final CANEncoder encoder;

    public MotorClimber(Clock clock, DashboardMap dashboardMap) {
        super(clock);
        motor = new CANSparkMax(RobotConstants.CAN.CLIMBER, CANSparkMaxLowLevel.MotorType.kBrushless);
        motor.restoreFactoryDefaults();
        motor.setMotorType(CANSparkMaxLowLevel.MotorType.kBrushless);
//        motor.setSmartCurrentLimit(20);

        encoder = motor.getEncoder();
        motor.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, true);
        motor.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, 100000); // TODO get correct value
        dashboardMap.getDebugTab().add("Climb Encoder", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeDouble(encoder.getPosition()))));
    }

    @Override
    protected void useSpeed(double speed) {
        motor.set(speed);
    }

    @Override
    protected void goToStoredPosition() {

    }

    @Override
    protected void goToStartingPosition() {

    }

    @Override
    public boolean isStored() {
        return false;
    }
}
