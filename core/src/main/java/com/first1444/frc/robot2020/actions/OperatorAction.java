package com.first1444.frc.robot2020.actions;

import com.first1444.frc.robot2020.Robot;
import com.first1444.frc.robot2020.input.RobotInput;
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
    public OperatorAction(Robot robot, RobotInput input) {
        super(true);
        this.robot = robot;
        this.input = input;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        if(input.getTurretCenterOrient().isDown()){
            robot.getTurret().setDesiredRotation(Rotation2.ZERO);
        } else if(input.getTurretLeftOrient().isDown()){
            robot.getTurret().setDesiredRotation(Rotation2.DEG_90);
        } else if(input.getTurretRightOrient().isDown()){
            robot.getTurret().setDesiredRotation(Rotation2.DEG_270);
        } else {
            InputPart rawSpeedPart = input.getTurretRawControl();
            if(!rawSpeedPart.isDeadzone()){
                robot.getTurret().setRawSpeed(rawSpeedPart.getPosition());
//                System.out.println("Setting speed to " + rawSpeedPart.getPosition());
            } else if(input.getEnableTurretAutoTarget().isDown()) {
                Vector2 position = robot.getAbsoluteDistanceAccumulator().getPosition();
                Rotation2 rotation = robot.getOrientation().getOrientation();
                Rotation2 angle = Field2020.ALLIANCE_POWER_PORT.getTransform().getPosition().minus(position).getAngle();

                Rotation2 desired = angle.minus(rotation);
                if (desired.getRadians() <= Turret.MAX_ROTATION.getRadians() && desired.getRadians() >= Turret.MIN_ROTATION.getRadians()) {
                    robot.getTurret().setDesiredRotation(desired);
                }
            }
        }
        final double shootSpeed;
        if(input.getManualShootSpeed().isDeadzone()){
            shootSpeed = 0;
        } else {
            shootSpeed = input.getManualShootSpeed().getPosition() * .55 + .45;
        }
        robot.getBallShooter().setSpeed(shootSpeed);

        final double intakeSpeed;
        if (input.getIntakeSpeed().isDeadzone()) {
            intakeSpeed = 0;
        } else {
            intakeSpeed = input.getIntakeSpeed().getPosition();
        }
        robot.getIntake().setIntakeSpeed(intakeSpeed);
    }
}
