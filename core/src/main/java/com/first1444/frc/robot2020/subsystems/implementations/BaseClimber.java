package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.Climber;

import static java.lang.Math.abs;

public abstract class BaseClimber implements Climber {

    private double speed = 0.0;
    private boolean startingPosition = false;
    private boolean storedPosition = false;

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
    public final void startingPosition() {
        startingPosition = true;
        storedPosition = false;
    }

    @Override
    public final void storedPosition() {
        startingPosition = false;
        storedPosition = true;
    }

    @Override
    public final void run() {
        final double speed = this.speed;
        if(startingPosition){
            goToStartingPosition();;
        } else if(storedPosition){
            goToStoredPosition();
        } else {
            useSpeed(speed);
        }
    }
}
