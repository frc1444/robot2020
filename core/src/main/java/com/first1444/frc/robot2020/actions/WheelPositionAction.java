package com.first1444.frc.robot2020.actions;

import com.first1444.frc.robot2020.subsystems.WheelSpinner;
import com.first1444.sim.api.frc.implementations.infiniterecharge.WheelColor;
import me.retrodaredevil.action.SimpleAction;

/**
 * The spins the color wheel to a certain color
 */
public class WheelPositionAction extends SimpleAction {
    private final WheelSpinner spinner;
    private final WheelColor color;
    public WheelPositionAction(WheelSpinner spinner, WheelColor color) {
        super(true);
        this.spinner = spinner;
        this.color = color;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
    }
}
