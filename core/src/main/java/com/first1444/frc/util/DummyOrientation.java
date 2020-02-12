package com.first1444.frc.util;

import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.sensors.Orientation;
import org.jetbrains.annotations.NotNull;

public class DummyOrientation implements Orientation {
    private final Rotation2 orientation;

    public DummyOrientation(Rotation2 orientation) {
        this.orientation = orientation;
    }

    @NotNull
    @Override
    public Rotation2 getOrientation() {
        return orientation;
    }

    @Override
    public double getOrientationDegrees() {
        return orientation.getDegrees();
    }

    @Override
    public double getOrientationRadians() {
        return orientation.getRadians();
    }
}
