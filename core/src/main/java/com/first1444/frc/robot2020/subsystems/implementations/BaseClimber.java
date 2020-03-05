package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.Climber;
import com.first1444.sim.api.Clock;

import static java.lang.Math.abs;

public abstract class BaseClimber implements Climber {

    private final Clock clock;

    private double speed = 0.0;
    private boolean startingPosition = false;
    protected boolean storedPosition = false;
    private double timeoutTime = 0.0;

    protected BaseClimber(Clock clock) {
        this.clock = clock;
    }

    protected abstract void useSpeed(double speed);
    protected abstract void goToStartingPosition();
    protected abstract void goToStoredPosition();

    @Override
    public final void setRawSpeed(double speed) {
        if(abs(speed) > 1){
            throw new IllegalArgumentException("speed is out of range! speed=" + speed);
        }
        this.speed = speed;
        startingPosition = false;
        storedPosition = false;
    }

    @Override
    public final void startingPosition(double timeoutSeconds) {
        startingPosition = true;
        storedPosition = false;
        timeoutTime = clock.getTimeSeconds() + timeoutSeconds;
    }

    @Override
    public final void storedPosition(double timeoutSeconds) {
        startingPosition = false;
        storedPosition = true;
        timeoutTime = clock.getTimeSeconds() + timeoutSeconds;
    }

    @Override
    public final void run() {
        final double speed = this.speed;
        this.speed = 0.0;
        if(clock.getTimeSeconds() > timeoutTime){
            startingPosition = false;
            storedPosition = false;
        }
        if(startingPosition){
            goToStartingPosition();;
        } else if(storedPosition){
            goToStoredPosition();
        } else {
            useSpeed(speed);
        }
    }
}
