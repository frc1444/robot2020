package com.first1444.frc.robot2020;

import com.first1444.sim.wpi.frc.RoboSimRobot;
import com.first1444.sim.wpi.frc.WatchdogRunnableCreator;
import com.first1444.sim.wpi.frc.WpiTimedRobot;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;

public final class Main {
    private Main() {
    }

    public static void main(String... args) {
        RobotBase.startRobot(Main::createRobot);
    }

    public static RobotBase createRobot(){
        final double period = TimedRobot.kDefaultPeriod;
        return new RoboSimRobot(new WatchdogRunnableCreator(new WpiRunnableCreator(), period){
            {
                getWatchdog().suppressTimeoutMessage(true);
            }
            @Override
            protected void onWatchdogDisable() {
            }
            @Override
            protected void printLoopOverrunMessage() {
            }
        }, period){
            @Override
            public void endCompetition() {
                System.out.println("Ending competition");
                super.endCompetition();
            }
        };
//        return new WpiTimedRobot(new WpiRunnableCreator());
    }
}
