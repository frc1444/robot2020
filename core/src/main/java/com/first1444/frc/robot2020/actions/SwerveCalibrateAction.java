package com.first1444.frc.robot2020.actions;

import com.first1444.frc.robot2020.input.RobotInput;
import com.first1444.sim.api.drivetrain.swerve.SwerveDrive;

@Deprecated
public class SwerveCalibrateAction implements Runnable{
    private final SwerveDrive drive;
    private final RobotInput input;
    public SwerveCalibrateAction(SwerveDrive drive, RobotInput input) {
        this.drive = drive;
        this.input = input;
    }

    @Override
    public void run() {
    }
}
