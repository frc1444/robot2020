package com.first1444.frc.robot2020.subsystems;

public interface Intake {
    /**
     * This must be called continuously
     * @param speed A value from -1 to 1. Normally in range -1 to 0. Negative values suck the ball in
     */
    void setSpeed(double speed);

    void setDesiredState(State stage);
    State getDesiredState();

    State getCurrentState();

    enum State {
        OUT, IN
    }
}
