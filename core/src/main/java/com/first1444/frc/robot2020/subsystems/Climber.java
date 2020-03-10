package com.first1444.frc.robot2020.subsystems;

public interface Climber extends Runnable {
    /**
     * @param speed The speed of the lift/boom mechanism. Positive makes it go up, negative retracts it.
     */
    void setRawSpeed(double speed);

    void storedPosition(double timeoutSeconds);
    void startingPosition(double timeoutSeconds);

    boolean isStored();

    boolean isIntakeDown();
}
