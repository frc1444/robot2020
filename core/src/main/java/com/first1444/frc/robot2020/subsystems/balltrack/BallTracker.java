package com.first1444.frc.robot2020.subsystems.balltrack;

public interface BallTracker {
    /**
     *
     * @return The number of balls currently stored
     */
    int getBallCount();
    void setBallCount(int ballCount);

    void addBall();
    void removeBall();

    /**
     * Call this to report the removing of a ball near the shooter
     */
    void removeBallTop();
    /**
     * Call this to report the removing of a ball near intake
     */
    void removeBallBottom();
}
