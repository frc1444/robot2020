package com.first1444.frc.robot2020.subsystems;

import com.first1444.sim.api.frc.implementations.infiniterecharge.WheelColor;

public interface WheelSpinner extends Runnable {

    /**
     * Sets the speed of the motor that spins the control panel/color wheel/wheel of fortune
     * @param speed The speed of the motor. A positive value spins the wheel clockwise, a negative value spins the wheel counter clockwise
     */
    void setSpeed(double speed);

    /**
     * @return Gets the current color from the sensor or null
     */
    WheelColor getCurrentColor();

    void setDesiredState(State state);
    State getDesiredState();

    State getCurrentState();

    enum State {
        OUT,
        IN
    }
}
