package com.first1444.frc.robot2020;

import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.sensors.Orientation;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.toRadians;

public class BNOOrientation implements Orientation {
    private final BNO055 bno055;

    public BNOOrientation(BNO055 bno055) {
        this.bno055 = bno055;
    }

    @Override
    public double getOrientationDegrees() {
        return -bno055.getEulerData().heading;
    }
    @NotNull
    @Override
    public Rotation2 getOrientation() {
        return Rotation2.fromDegrees(getOrientationDegrees());
    }

    @Override
    public double getOrientationRadians() {
        return toRadians(getOrientationDegrees());
    }

}
