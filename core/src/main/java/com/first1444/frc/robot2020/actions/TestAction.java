package com.first1444.frc.robot2020.actions;

import com.first1444.frc.robot2020.input.RobotInput;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.output.ControllerRumble;

public class TestAction extends SimpleAction {
    private final RobotInput input;

    public TestAction(RobotInput robotInput) {
        super(true);
        this.input = robotInput;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        final ControllerRumble rumble = input.getDriverRumble();
        if(rumble.isConnected()) {
            final InputPart inputPart = input.getMovementSpeed();
            if (!inputPart.isDeadzone()) {
                rumble.rumbleTimeout(100, inputPart.getPosition());
            } else {
                rumble.rumbleForever(0);
            }
        }
    }
}
