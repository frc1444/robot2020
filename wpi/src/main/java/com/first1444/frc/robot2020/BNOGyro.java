package com.first1444.frc.robot2020;

import edu.wpi.first.wpilibj.interfaces.Gyro;

public class BNOGyro implements Gyro {

    private final BNO055 bno055;
    private double gyroHeadingOffset = 0.0;

    public BNOGyro(BNO055 bno055) {
        this.bno055 = bno055;
    }

    @Override
    public double getRate() {
        //Not currently implemented
        throw new UnsupportedOperationException("The BNO055 probably supports this, but we haven't implemented it yet!");
    }

    @Override
    public double getAngle() {
        return bno055.getEulerData().heading - gyroHeadingOffset;
    }

    @Override
    public void close() {
        bno055.close();
    }

    @Override
    public void calibrate() {
    }

    @Override
    public void reset() {
        gyroHeadingOffset = bno055.getEulerData().heading;
    }
}
