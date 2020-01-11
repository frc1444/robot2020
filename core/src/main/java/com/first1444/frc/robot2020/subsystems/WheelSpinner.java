package com.first1444.frc.robot2020.subsystems;

import com.first1444.sim.api.frc.implementations.infiniterecharge.WheelColor;

public interface WheelSpinner {
    void setSpeed(double speed);

    /**
     * @return Gets the current color from the sensor or null
     */
    WheelColor getCurrentColor();
}
