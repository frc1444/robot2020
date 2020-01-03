package com.first1444.frc.robot2020;

import com.first1444.sim.wpi.frc.RoboSimRobot;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;

/**
 * Do NOT add any static variables to this class, or any initialization at all.
 * Unless you know what you are doing, do not modify this file except to
 * change the parameter class to the startRobot call.
 */
public final class Main {
    private Main() {
    }

    /**
     * Main initialization function. Do not perform any initialization here.
     *
     * <p>If you change your main robot class, change the parameter type.
     */
    public static void main(String... args) {
        RobotBase.startRobot(Main::createRobot);
    }
    public static RobotBase createRobot(){
        return new RoboSimRobot(new WpiRunnableCreator(), TimedRobot.kDefaultPeriod);
    }
}
