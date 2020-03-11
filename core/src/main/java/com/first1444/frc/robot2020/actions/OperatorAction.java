package com.first1444.frc.robot2020.actions;

import com.first1444.frc.robot2020.Robot;
import com.first1444.frc.robot2020.input.RobotInput;
import com.first1444.frc.robot2020.subsystems.BallShooter;
import com.first1444.frc.robot2020.subsystems.Intake;
import com.first1444.frc.robot2020.subsystems.Turret;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.frc.implementations.infiniterecharge.Field2020;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.controller.input.InputPart;

import static java.lang.Math.abs;

/**
 * The operator action that takes input from a {@link RobotInput} and sets the desired states of the different subsystems
 */
public class OperatorAction extends SimpleAction {
    private final Robot robot;
    private final RobotInput input;

    private boolean isShooterOn = false;

    public OperatorAction(Robot robot, RobotInput input) {
        super(true);
        this.robot = robot;
        this.input = input;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isShooterOn = false;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();

        final boolean autoDown = input.getEnableAuto().isDown();
        final boolean shootDown = input.getShootButton().isDown();
        if(shootDown && robot.getBallTracker().getBallCount() == 0){
            input.getDriverRumble().rumbleTimeout(200, .5);
        }
        {
            final Rotation2 trim;
            if (input.getTurretTrim().isDeadzone()) {
                trim = Rotation2.ZERO;
            } else {
                double value = input.getTurretTrim().getPosition();
                trim = Rotation2.fromDegrees(value * -10.0);
            }
            robot.getTurret().setDesiredTrim(trim);
        }
        if(input.getTurretCenterOrient().isDown()){
            robot.getTurret().setDesiredState(Turret.DesiredState.createDesiredRotation(Rotation2.ZERO));
        } else {
            InputPart rawSpeedPart = input.getTurretRawControl();
            if(!rawSpeedPart.isDeadzone()){
                robot.getTurret().setDesiredState(Turret.DesiredState.createRawSpeedClockwise(rawSpeedPart.getPosition()));
            } else if(autoDown) {
                Vector2 position = robot.getOdometry().getAbsoluteAndVisionDistanceAccumulator().getPosition().plus(Turret.TURRET_OFFSET);
                Rotation2 rotation = robot.getOdometry().getAbsoluteAndVisionOrientation().getOrientation();
                Rotation2 angle = Field2020.ALLIANCE_POWER_PORT.getTransform().getPosition().minus(position).getAngle();

                Rotation2 desired = angle.minus(rotation);
                if (desired.getDegrees() <= 135 && desired.getDegrees() >= -135) {
                    robot.getTurret().setDesiredState(Turret.DesiredState.createDesiredRotation(desired));
                } else {
                    robot.getTurret().setDesiredState(Turret.DesiredState.createDesiredRotation(Rotation2.ZERO));
                }
            }
        }
        if(input.getShooterOn().isDown() || shootDown){
            isShooterOn = true;
        } else if(input.getShooterOff().isDown()){
            isShooterOn = false;
        }
        final Double desiredRpm;
        if(autoDown){
            if(isShooterOn){
                final double shootSpeed = input.getManualShootSpeed().getPosition() * .15 + .85;
                desiredRpm = robot.getBestEstimatedTargetRpm() * shootSpeed;
            } else {
                desiredRpm = null;
            }
        } else {
            if(isShooterOn){
                final double shootSpeed = input.getManualShootSpeed().getPosition() * .15 + .85;
                desiredRpm = shootSpeed * BallShooter.MAX_RPM;
            } else {
                desiredRpm = null;
            }
        }
        if(desiredRpm != null) {
            robot.getBallShooter().setDesiredRpm(desiredRpm);
        } else {
            robot.getBallShooter().setDesiredRpm(0);
        }

        { // intake stuff
            Intake intake = robot.getIntake();
            boolean feedBalls = shootDown && robot.getBallShooter().atSetpoint();
            int intakePosition = input.getIntakeSpeed().getDigitalPosition();
            if(feedBalls){
                intake.setControl(Intake.Control.FEED_ALL_AND_INTAKE);
            } else if(intakePosition < 0){ // suck in
                intake.setControl(Intake.Control.INTAKE);
                if(robot.getBallTracker().getBallCount() >= 5){
                    input.getDriverRumble().rumbleTimeout(100, .5);
                }
            } else if(intakePosition > 0){ // spit out
                intake.setIntakeSpeed(-1);
                intake.setIndexerSpeed(-1);
            } else {
                if(input.getManualOnly().isDown()){
                    intake.setControl(Intake.Control.MANUAL);
                } else {
                    intake.setControl(Intake.Control.STORE);
                }
            }
            if(input.getFeederManualInButton().isDown()){
                intake.setFeederSpeed(1.0);
            } else if(input.getFeederManualOutButton().isDown()){
                intake.setFeederSpeed(-1.0);
            }
            if(input.getIndexerManualInButton().isDown()){
                intake.setIndexerSpeed(1.0);
            } else if(input.getIndexerManualOutButton().isDown()){
                intake.setIndexerSpeed(-1.0);
            }
            if(input.getIntakeManualInButton().isDown()){
                intake.setIntakeSpeed(1.0);
            } else if (input.getIntakeManualOutButton().isDown()){
                intake.setIntakeSpeed(-1.0);
            }
        }
        {
            boolean wantsToClimb = false;
            final boolean clearToClimb = robot.getTurret().isClearToRaiseClimb();
            if (!input.getClimbRawControl().isDeadzone()) {
                robot.getClimber().setRawSpeed(input.getClimbRawControl().getPosition());
                wantsToClimb = true;
            }
            if(input.getClimbStored().isDown()){
                if(clearToClimb) {
                    robot.getClimber().storedPosition(5.0);
                }
                wantsToClimb = true;
            }
//            if(input.getClimbStarting().isDown()){
//                robot.getClimber().startingPosition(3.0);
//            }
            if (wantsToClimb && (!clearToClimb || autoDown)) { // if it's not clear to climb or auto targeting is enabled
                input.getDriverRumble().rumbleTimeout(500, 1.0);
            }
        }
    }

    @Override
    protected void onEnd(boolean peacefullyEnded) {
        super.onEnd(peacefullyEnded);
        robot.getBallShooter().setDesiredRpm(0.0);
    }
}
