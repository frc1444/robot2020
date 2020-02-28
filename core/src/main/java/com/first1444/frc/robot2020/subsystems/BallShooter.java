package com.first1444.frc.robot2020.subsystems;

public interface BallShooter extends Runnable {
    /**
     * This must be called continuously
     * @param rpm How fast to spin the shooter motor. Positive values shoot out. Negative values are allowed, but why?
     */
    void setDesiredRpm(double rpm);

    double getCurrentRpm();
    boolean atSetpoint();

//    double MAX_RPM = 6380;
    double MAX_RPM = 6100;
}
