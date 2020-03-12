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

import static java.util.Objects.requireNonNull;

public class MotorClimber extends BaseClimber {
    private static final double LOCK_ENCODER_COUNTS = 205.0;
    private static final double UNLATCH_STAGE_2_COUNTS = 190.0; // TODO get encoder value
    private static final double RESET_TO_LOW_STATE_COUNTS = 170.0;
    private enum State {
        LOW,
        UNLATCHING,
        FREE_TO_CLIMB
    }
    private final CANSparkMax motor;
    private final CANEncoder encoder;
    private final DigitalInput reverseLimitSwitch;

    private final DigitalInput intakeLimitSwitch;
    private Double lastIntakeLimitSwitchPress = null;
    private State state = State.LOW;

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
        if(isReverseLimitSwitchPressed()){
            encoder.setPosition(0.0);
        }
        if(isIntakeLimitSwitchPressed()){
            lastIntakeLimitSwitchPress = clock.getTimeSeconds();
        }
        double counts = encoder.getPosition();
        if(counts <= RESET_TO_LOW_STATE_COUNTS){
            state = State.LOW;
        } else {
            final State state = requireNonNull(this.state);
            if(state == State.LOW){
                if(counts >= LOCK_ENCODER_COUNTS){
                    this.state = State.UNLATCHING;
                }
            } else if(state == State.UNLATCHING){
                if(counts <= UNLATCH_STAGE_2_COUNTS){
                    this.state = State.FREE_TO_CLIMB;
                }
            }
        }
        super.run();
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
        if(!isReverseLimitSwitchPressed() && isStored()){
            motor.set(-1.0);
        } else {
            motor.set(0.0);
        }
    }

    @Override
    protected void goToClimbingPosition() {
        State state = requireNonNull(this.state);
        if(state == State.LOW){
            motor.set(1.0);
        } else if(state == State.UNLATCHING){
            motor.set(-1.0);
        } else {
            assert state == State.FREE_TO_CLIMB;
            motor.set(1.0);
        }
    }

    @Override
    public boolean isStored() {
        Double lastIntakeLimitSwitchPress = this.lastIntakeLimitSwitchPress;
        return lastIntakeLimitSwitchPress != null && clock.getTimeSeconds() - lastIntakeLimitSwitchPress < 3.0;
    }

    @Override
    public boolean isIntakeDown() {
        return isIntakeLimitSwitchPressed();
    }
}
