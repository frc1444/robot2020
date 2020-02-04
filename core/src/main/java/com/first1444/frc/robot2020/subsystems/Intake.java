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
     * This must be called continuously
     * <p>
     * This delivers the ball to the feeder
     * @param speed The indexer speed. Positive values moves ball towards shooter
     */
    void setIndexerSpeed(double speed);
    /**
     * This must be called continuously
     * <p>
     * This feeds ball into the shooter
     * @param speed The feeder speed. Positive values deliver ball to shooter
     */
    void setFeederSpeed(double speed);

}
