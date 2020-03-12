package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.util.reportmap.ReportMap;
import com.first1444.sim.api.Clock;

import java.text.DecimalFormat;

public class DummyClimber extends BaseClimber {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
    private final ReportMap reportMap;
    private boolean stored = false;

    public DummyClimber(Clock clock, ReportMap reportMap) {
        super(clock);
        this.reportMap = reportMap;
    }

    @Override
    protected void useSpeed(double speed) {
        report("Speed " + FORMAT.format(speed));
        if(speed > 0){
            stored = false;
        }
    }

    @Override
    protected void goToClimbingPosition() {
        report("Climbing Position");
        stored = false;
    }

    @Override
    protected void goToStoredPosition() {
        report("Stored position");
        stored = true;
    }
    private void report(String movementString){
        reportMap.report("Climber Movement", movementString);
    }

    @Override
    public boolean isStored() {
        return stored;
    }

    @Override
    public boolean isIntakeDown() {
        return true;
    }
}
