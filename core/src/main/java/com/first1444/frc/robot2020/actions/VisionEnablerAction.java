package com.first1444.frc.robot2020.actions;

import com.first1444.frc.robot2020.input.RobotInput;
import com.first1444.frc.robot2020.vision.VisionState;
import com.first1444.sim.api.Clock;
import me.retrodaredevil.action.SimpleAction;

public class VisionEnablerAction extends SimpleAction {
    private final Clock clock;
    private final RobotInput input;
    private final VisionState visionState;

    private double lastChange = 0;

    public VisionEnablerAction(Clock clock, RobotInput input, VisionState visionState) {
        super(false);
        this.clock = clock;
        this.input = input;
        this.visionState = visionState;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        double now = clock.getTimeSeconds();
        if(now - lastChange < 1.0){
            return;
        }
        if(input.getVisionToggle().isJustPressed()){
            visionState.setEnabled(!visionState.isEnabled());
            lastChange = now;
        }
        if(input.getVisionOn().isDown()){
            visionState.setEnabled(true);
            lastChange = now;
        }
        if(input.getVisionOff().isDown()){
            visionState.setEnabled(false);
            lastChange = now;
        }
    }
}
