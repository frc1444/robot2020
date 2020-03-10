package com.first1444.frc.robot2020;

import com.first1444.dashboard.shuffleboard.PropertyComponent;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
import com.first1444.frc.robot2020.subsystems.implementations.BaseClimber;
import com.first1444.sim.api.Clock;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.DigitalInput;

public class MotorClimber extends BaseClimber {
    private final CANSparkMax motor;
    private final CANEncoder encoder;
    private final DigitalInput reverseLimitSwitch;

    private final DigitalInput intakeLimitSwitch;

    public MotorClimber(Clock clock, DashboardMap dashboardMap) {
        super(clock);
        motor = new CANSparkMax(RobotConstants.CAN.CLIMBER, CANSparkMaxLowLevel.MotorType.kBrushless);
        reverseLimitSwitch = new DigitalInput(RobotConstants.DIO.CLIMB_REVERSE_LIMIT_SWITCH_NORMALLY_OPEN);
        intakeLimitSwitch = new DigitalInput(RobotConstants.DIO.INTAKE_DOWN_LIMIT_SWITCH_NORMALLY_OPEN);
        motor.restoreFactoryDefaults();
        motor.setIdleMode(CANSparkMax.IdleMode.kBrake);
        motor.setMotorType(CANSparkMaxLowLevel.MotorType.kBrushless);
        motor.setSmartCurrentLimit(200); // default is 80

        encoder = motor.getEncoder();
//        motor.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, true);
//        motor.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, 100000); // TODO get correct value
        dashboardMap.getDebugTab().add("Climb Encoder", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeDouble(encoder.getPosition()))));
        dashboardMap.getDevTab().add("Intake Down Limit", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeBoolean(isIntakeDown()))));
    }
    private boolean isReverseLimitSwitchPressed(){
        //noinspection UnnecessaryLocalVariable
        boolean closed = !reverseLimitSwitch.get();
        return closed; // normally open
    }
    private boolean isIntakeLimitSwitchPressed(){
        //noinspection UnnecessaryLocalVariable
        boolean closed = !intakeLimitSwitch.get();
        return closed; // normally open
    }

    @Override
    public void run() {
        super.run();
        if(isReverseLimitSwitchPressed()){
            encoder.setPosition(0.0);
        }
    }

    @Override
    protected void useSpeed(double speed) {
        if(speed < 0 && isReverseLimitSwitchPressed()){
            motor.set(0.0);
        } else {
            motor.set(speed);
        }
    }

    @Override
    protected void goToStoredPosition() {
        if(!isReverseLimitSwitchPressed() && isIntakeLimitSwitchPressed()){
            motor.set(-1.0);
        } else {
            motor.set(0.0);
        }
    }

    @Override
    protected void goToStartingPosition() {
        motor.set(0.0);
    }

    @Override
    public boolean isStored() {
        return isReverseLimitSwitchPressed();
    }

    @Override
    public boolean isIntakeDown() {
        return isIntakeLimitSwitchPressed();
    }
}
