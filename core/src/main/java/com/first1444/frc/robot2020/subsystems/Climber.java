package com.first1444.frc.robot2020.subsystems;

public interface Climber extends Runnable {
    /**
     * @param speed The speed of the lift/boom mechanism. Positive makes it go up, negative retracts it.
     */
    void setRawSpeed(double speed);

    /**
     * Locks the position. This should be called once
     */
    void lockCurrentPosition();

    void setNeutralState(NeutralState neutralState);
    NeutralState getNeutralState();

    enum NeutralState {
        /**
         * Without using power, this applies a "brake" to the motor by shorting the wires together
         */
        BRAKE,
        /**
         * This lets the motor coast
         */
        COAST
    }
}
