package com.first1444.frc.robot2020.subsystems.balltrack;

import com.first1444.sim.api.Clock;
import org.jetbrains.annotations.Nullable;

public class BallTracker {
    private final Clock clock;
    private int ballCount = 0;
    private Double lastShootTimestamp = null;

    public BallTracker(Clock clock) {
        this.clock = clock;
    }


    /**
     *
     * @return The number of balls currently stored
     */
    public int getBallCount() {
        return ballCount;
    }
    @Nullable
    public Double getLastShootTimestamp() {
        return lastShootTimestamp;
    }

    public void setBallCount(int ballCount) {
        if(ballCount < 0){
            throw new IllegalArgumentException("ballCount is < 0. ballCount=" + ballCount);
        }
        if(ballCount > 5){
            throw new IllegalArgumentException("ballCount > 5. ballCount=" + ballCount);
        }
        this.ballCount = ballCount;
    }

    public void addBall() {
        ballCount++;
    }

    public void removeBall() {
        ballCount--;
        if(ballCount < 0){
            ballCount = 0;
        }
    }
    /**
     * Call this to report the removing of a ball near the shooter
     */
    public void onShootBall() {
        removeBall();
        lastShootTimestamp = clock.getTimeSeconds();
    }

    /**
     * Call this to report the removing of a ball near intake
     */
    public void onSpitOutBall() {
        removeBall();
    }
}
