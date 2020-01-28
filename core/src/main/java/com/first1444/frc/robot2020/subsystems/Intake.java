package com.first1444.frc.robot2020.subsystems;

/**
 * Represents the intake and indexer
 */
public interface Intake extends Runnable {
    /**
     * This must be called continuously
     * @param speed A value from -1 to 1. Normally in range -1 to 0. Positive values suck the ball in
     */
    void setIntakeSpeed(double speed);

    /**
     *
     * @return The number of balls currently stored
     */
    int getBallCount();
}
