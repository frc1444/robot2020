package com.first1444.frc.robot2020.actions;
import com.first1444.frc.robot2020.subsystems.WheelSpinner;
import me.retrodaredevil.action.SimpleAction;

public class WheelSpinAction extends SimpleAction
{
    private final WheelSpinner spinner;

    public WheelSpinAction(WheelSpinner spinner) {
        super(true);
        this.spinner = spinner;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
    }
}