package com.first1444.frc.robot2020.subsystems;

import com.first1444.sim.api.frc.implementations.infiniterecharge.WheelColor;

public class DummyWheelSpinner implements WheelSpinner {

    // TODO log values to dashboard
    public DummyWheelSpinner() {
    }
    @Override
    public void setSpeed(double speed) {
    }
    @Override
    public WheelColor getCurrentColor() {
        return null;
    }
}
