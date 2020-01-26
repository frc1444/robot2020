package com.first1444.frc.robot2020.actions;

import com.first1444.frc.robot2020.Robot;
import com.first1444.frc.robot2020.input.RobotInput;
import com.first1444.sim.api.Rotation2;
import me.retrodaredevil.action.SimpleAction;

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
        }
    }
}
