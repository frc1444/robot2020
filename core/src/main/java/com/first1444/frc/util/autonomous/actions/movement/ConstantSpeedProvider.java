package com.first1444.frc.util.autonomous.actions.movement;

import com.first1444.sim.api.Vector2;

public class ConstantSpeedProvider implements SpeedProvider {
    private final double speed;

    public ConstantSpeedProvider(double speed) {
        this.speed = speed;
    }

    @Override
    public double getSpeed(Vector2 currentAbsolutePosition) {
        return speed;
    }

    @Override
    public String toString() {
        return "ConstantSpeedProvider(" +
            "speed=" + speed +
            ')';
    }
}
