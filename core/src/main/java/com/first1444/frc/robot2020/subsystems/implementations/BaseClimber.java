package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.Climber;
import com.first1444.sim.api.Clock;

import static java.lang.Math.abs;

public abstract class BaseClimber implements Climber {

    private enum Mode {
        MANUAL,
        STORED_POSITION,
        CLIMBING_POSITION
    }

    protected final Clock clock;

    private double speed = 0.0;
    private Mode mode = Mode.MANUAL;
    private double timeoutTime = 0.0;

    protected BaseClimber(Clock clock) {
        this.clock = clock;
    }

    protected abstract void useSpeed(double speed);
    protected abstract void goToClimbingPosition();
    protected abstract void goToStoredPosition();

    @Override
    public final void setRawSpeed(double speed) {
        if(abs(speed) > 1){
            throw new IllegalArgumentException("speed is out of range! speed=" + speed);
        }
        this.speed = speed;
        mode = Mode.MANUAL;
    }

    @Override
    public final void climbingPosition() {
        mode = Mode.CLIMBING_POSITION;
    }

    @Override
    public final void storedPosition(double timeoutSeconds) {
        mode = Mode.STORED_POSITION;
        timeoutTime = clock.getTimeSeconds() + timeoutSeconds;
    }

    @Override
    public void run() {
        final Mode mode = this.mode;
        final double speed = this.speed;
        this.speed = 0.0;
        if(mode == Mode.MANUAL){
            useSpeed(speed);
        } else if(mode == Mode.STORED_POSITION){
            if(clock.getTimeSeconds() > timeoutTime){
                this.mode = Mode.MANUAL;
            }
            goToStoredPosition();
        } else {
            assert mode == Mode.CLIMBING_POSITION;
            goToClimbingPosition();
        }
    }
}
