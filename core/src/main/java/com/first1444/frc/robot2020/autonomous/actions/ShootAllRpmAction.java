package com.first1444.frc.robot2020.autonomous.actions;

import com.first1444.frc.robot2020.subsystems.BallShooter;
import com.first1444.frc.robot2020.subsystems.Intake;
import com.first1444.frc.robot2020.subsystems.balltrack.BallTracker;
import com.first1444.sim.api.Clock;
import me.retrodaredevil.action.SimpleAction;

import static java.lang.Math.abs;

/**
 * Shoots all of the balls at a certain RPM
 */
public class ShootAllRpmAction extends SimpleAction {
    private final Clock clock;
    private final Intake intake;
    private final BallShooter ballShooter;
    private final BallTracker ballTracker;
    private final double rpm;
    private Double setpointStartTime = null;
    public ShootAllRpmAction(Clock clock, Intake intake, BallShooter ballShooter, BallTracker ballTracker, double rpm) {
        super(true);
        this.clock = clock;
        this.intake = intake;
        this.ballShooter = ballShooter;
        this.ballTracker = ballTracker;
        this.rpm = rpm;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        if(ballTracker.getBallCount() == 0){
            setDone(true);
        }
        ballShooter.setDesiredRpm(rpm);
        if(ballShooter.atSetpoint()){
            Double setpointStartTime = this.setpointStartTime;
            if(setpointStartTime == null){
                setpointStartTime = clock.getTimeSeconds();
                this.setpointStartTime = setpointStartTime;
            }
            if(clock.getTimeSeconds() - setpointStartTime > .1) {
                intake.setControl(Intake.Control.FEED_ALL_AND_INTAKE);
            } else {
                intake.setControl(Intake.Control.STORE);
            }
        } else {
            setpointStartTime = null;
            intake.setControl(Intake.Control.STORE);
        }
    }

    @Override
    protected void onEnd(boolean peacefullyEnded) {
        super.onEnd(peacefullyEnded);
        ballShooter.setDesiredRpm(0.0);
    }
}
