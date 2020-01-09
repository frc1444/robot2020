package com.first1444.frc.robot2020;

import com.first1444.sim.wpi.frc.RoboSimRobot;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;

public final class Main {
    private Main() {
    }

    public static void main(String... args) {
        RobotBase.startRobot(Main::createRobot);
    }

    public static RobotBase createRobot(){
        return new RoboSimRobot(new WpiRunnableCreator(), TimedRobot.kDefaultPeriod) {
            @Override
            protected void printLoopOverrunMessage() {
                super.printLoopOverrunMessage(); // TODO we may actually override this in the future
            }
        };
    }
}
