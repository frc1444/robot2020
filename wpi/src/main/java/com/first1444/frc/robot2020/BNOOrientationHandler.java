package com.first1444.frc.robot2020;

import com.first1444.sim.api.sensors.Orientation;
import com.first1444.sim.api.sensors.OrientationHandler;
import org.jetbrains.annotations.NotNull;

public class BNOOrientationHandler implements OrientationHandler {
    private final BNO055 bno055;
    private final BNOOrientation orientation;
    public BNOOrientationHandler(BNO055 bno055){
        this.bno055 = bno055;
        this.orientation = new BNOOrientation(bno055);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isInitialized() {
        return bno055.getCalibrationData().isGyro();
    }

    @NotNull
    @Override
    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public boolean reinitialize() {
        bno055.reinitialize();
        return isInitialized();
    }

    @Override
    public void close() {
        bno055.close();
    }
}
