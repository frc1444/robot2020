package com.first1444.frc.robot2020.subsystems;

public interface BallShooter extends Runnable {
    /**
     * @param rpm How fast to spin the shooter motor. Positive values shoot out.
     */
    void setDesiredRpm(double rpm);

    double getCurrentRpm();
    boolean atSetpoint();

//    double MAX_RPM = 6380;
    double MAX_RPM = 6100;

    double IDLE_RPM = 5000;
}
