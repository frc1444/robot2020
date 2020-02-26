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
        if(input.getTurretCenterOrient().isDown()){
            robot.getTurret().setDesiredState(Turret.DesiredState.createDesiredRotation(Rotation2.ZERO));
        } else {
            InputPart rawSpeedPart = input.getTurretRawControl();
            if(!rawSpeedPart.isDeadzone()){
                robot.getTurret().setDesiredState(Turret.DesiredState.createRawSpeedClockwise(rawSpeedPart.getPosition()));
            } else if(autoDown) {
                final Rotation2 trim;
                if(input.getTurretTrim().isDeadzone()){
                    trim = Rotation2.ZERO;
                } else {
                    double value = input.getTurretTrim().getPosition();
                    trim = Rotation2.fromDegrees(value * -5.0);
                }
                Vector2 position = robot.getAbsoluteDistanceAccumulator().getPosition();
                Rotation2 rotation = robot.getOrientation().getOrientation();
                Rotation2 angle = Field2020.ALLIANCE_POWER_PORT.getTransform().getPosition().minus(position).getAngle();

                Rotation2 desired = angle.minus(rotation).plus(trim);
                if (desired.getRadians() <= Turret.MAX_ROTATION.getRadians() && desired.getRadians() >= Turret.MIN_ROTATION.getRadians()) {
                    robot.getTurret().setDesiredState(Turret.DesiredState.createDesiredRotation(desired));
                }
            }
        }
        if(input.getShooterOn().isDown() || shootDown){
            isShooterOn = true;
        } else if(input.getShooterOff().isDown()){
            isShooterOn = false;
        }
        if(autoDown){
            if(isShooterOn){
                robot.getBallShooter().setDesiredRpm(robot.getBestEstimatedTargetRpm());
            }
        } else {
            if(isShooterOn){
                final double shootSpeed = input.getManualShootSpeed().getPosition() * .55 + .45;
                robot.getBallShooter().setDesiredRpm(shootSpeed * BallShooter.MAX_RPM);
            }
        }

        { // intake stuff
            Intake intake = robot.getIntake();
            if(shootDown){
                intake.setFeederSpeed(1.0); // TODO only do this if RPM of shooter is up to speed
                intake.setIndexerSpeed(0.4);
            }
            int intakePosition = input.getIntakeSpeed().getDigitalPosition();
            if(intakePosition < 0){ // suck in
                intake.setIntakeSpeed(1);
                intake.setIndexerSpeed(1);
            } else if(intakePosition > 0){ // spit out
                intake.setIntakeSpeed(-1);
                intake.setIndexerSpeed(-1);
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
    }
}
