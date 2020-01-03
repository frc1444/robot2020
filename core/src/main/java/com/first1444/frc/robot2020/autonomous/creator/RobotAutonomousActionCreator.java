package com.first1444.frc.robot2020.autonomous.creator;

import com.first1444.frc.robot2020.Robot;
import com.first1444.frc.util.autonomous.creator.DefaultSwerveDriveActionCreator;
import com.first1444.frc.util.autonomous.creator.FrcLogActionCreator;
import com.first1444.frc.util.autonomous.creator.LogActionCreator;
import com.first1444.frc.util.autonomous.creator.SwerveDriveActionCreator;

public class RobotAutonomousActionCreator implements AutonomousActionCreator {

    private final LogActionCreator logActionCreator;
    private final SwerveDriveActionCreator driveCreator;

    public RobotAutonomousActionCreator(Robot robot) {
        logActionCreator = new FrcLogActionCreator(System.out, robot.getLogger());
        driveCreator = new DefaultSwerveDriveActionCreator(robot.getDrive(), robot.getOrientation(), robot.getAbsoluteDistanceAccumulator());
    }

    @Override
    public LogActionCreator getLogCreator() {
        return logActionCreator;
    }

    @Override
    public SwerveDriveActionCreator getDriveCreator() {
        return driveCreator;
    }

}
