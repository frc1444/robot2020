package com.first1444.frc.robot2020.perspective;

import com.first1444.frc.robot2020.input.RobotInput;

public class FirstPersonPerspectiveOverride implements PerspectiveProvider {
    private final RobotInput input;

    public FirstPersonPerspectiveOverride(RobotInput input) {
        this.input = input;
    }

    @Override
    public Perspective getPerspective() {
        if(input.getFirstPersonHoldButton().isDown()){
            return Perspective.ROBOT_FORWARD_CAM;
        }
        return null;
    }
}
