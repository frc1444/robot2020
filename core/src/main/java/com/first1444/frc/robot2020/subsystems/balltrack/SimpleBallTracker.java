package com.first1444.frc.robot2020.subsystems.balltrack;

public class SimpleBallTracker implements BallTracker {
    private int ballCount;
    @Override
    public int getBallCount() {
        return ballCount;
    }

    @Override
    public void setBallCount(int ballCount) {
        if(ballCount < 0){
            throw new IllegalArgumentException("ballCount is < 0. ballCount=" + ballCount);
        }
        this.ballCount = ballCount;
    }

    @Override
    public void addBall() {
        ballCount++;
    }

    @Override
    public void removeBall() {
        ballCount--;
        if(ballCount < 0){
            ballCount = 0;
        }
    }

    @Override
    public void removeBallTop() {
        removeBall();
    }

    @Override
    public void removeBallBottom() {
        removeBall();
    }
}
