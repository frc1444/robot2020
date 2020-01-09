package com.first1444.frc.robot2020.subsystems;

public interface BallShooter {
    /**
     * This must be called continuously
     * @param speed A value from -1 to 1, normally in range 0 to 1. A positive value shoots the ball out.
     */
    void setSpeed(double speed);
}
