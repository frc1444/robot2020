package com.first1444.frc.robot2020.subsystems;

public interface Climber {
    /**
     * @param speed The speed of the lift/boom mechanism. Positive makes it go up, negative retracts it.
     */
    void setRawSpeed(double speed);

    /**
     * Sets the positioning to "lock" at its current location
     */
    void lock();
}
