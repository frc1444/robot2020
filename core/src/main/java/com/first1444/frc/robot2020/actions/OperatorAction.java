package com.first1444.frc.robot2020.actions;

import com.first1444.frc.robot2020.Robot;
import com.first1444.frc.robot2020.input.RobotInput;
import me.retrodaredevil.action.SimpleAction;

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
    }
}
