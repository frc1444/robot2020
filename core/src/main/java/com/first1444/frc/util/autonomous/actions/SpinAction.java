package com.first1444.frc.util.autonomous.actions;

import com.first1444.frc.robot2020.actions.TimedAction;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.drivetrain.swerve.SwerveDrive;

public class SpinAction extends TimedAction {
    private final SwerveDrive drive;
    public SpinAction(Clock clock, SwerveDrive drive) {
        super(true, clock, 1.3);
        this.drive = drive;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
//        drive.setControl(Vector2.ZERO, 0.825, 1.0);
        // 80 doesn't work
        drive.setControl(Vector2.ZERO, 1.0, 1.0);
    }
}
