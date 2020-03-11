package com.first1444.frc.robot2020.autonomous.actions;

import com.first1444.frc.robot2020.subsystems.BallShooter;
import me.retrodaredevil.action.SimpleAction;

@Deprecated
public class RpmSpinAction extends SimpleAction {
    private final BallShooter ballShooter;
    private final double rpm;
    public RpmSpinAction(BallShooter ballShooter, double rpm) {
        super(true);
        this.ballShooter = ballShooter;
        this.rpm = rpm;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        ballShooter.setDesiredRpm(rpm);
    }

    @Override
    protected void onEnd(boolean peacefullyEnded) {
        super.onEnd(peacefullyEnded);
        ballShooter.setDesiredRpm(0.0);
    }
}
