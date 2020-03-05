package com.first1444.frc.robot2020.autonomous.creator;

import com.first1444.frc.robot2020.Robot;
import com.first1444.frc.util.autonomous.creator.DefaultSwerveDriveActionCreator;
import com.first1444.frc.util.autonomous.creator.FrcLogActionCreator;
import com.first1444.frc.util.autonomous.creator.LogActionCreator;
import com.first1444.frc.util.autonomous.creator.SwerveDriveActionCreator;

public class AutonomousActionCreator {
    private final LogActionCreator logActionCreator;
    private final SwerveDriveActionCreator driveCreator;
    private final OperatorActionCreator operatorCreator;
    private final BasicActionCreator basicActionCreator;

    public AutonomousActionCreator(Robot robot) {
        logActionCreator = new FrcLogActionCreator(System.out, robot.getLogger());
        driveCreator = new DefaultSwerveDriveActionCreator(robot.getClock(), robot.getDrive(), robot.getOdometry().getAbsoluteOrientation(), robot.getOdometry().getRelativeDistanceAccumulator(), robot.getOdometry().getAbsoluteDistanceAccumulator());
        operatorCreator = new OperatorActionCreator(robot);
        basicActionCreator = new BasicActionCreator(robot);
    }
    public LogActionCreator getLogCreator() {
        return logActionCreator;
    }
    public SwerveDriveActionCreator getDriveCreator() {
        return driveCreator;
    }
    public OperatorActionCreator getOperatorCreator() {
        return operatorCreator;
    }
    public BasicActionCreator getBasicActionCreator() {
        return basicActionCreator;
    }
}
