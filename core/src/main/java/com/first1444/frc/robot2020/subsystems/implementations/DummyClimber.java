package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.util.reportmap.ReportMap;

import java.text.DecimalFormat;

public class DummyClimber extends BaseClimber {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
    private final ReportMap reportMap;

    private double speed = 0.0;
    private boolean startingPosition = false;
    private boolean storedPosition = false;

    public DummyClimber(ReportMap reportMap) {
        this.reportMap = reportMap;
    }

    @Override
    protected void useSpeed(double speed) {
        report("Speed " + FORMAT.format(speed));
    }

    @Override
    protected void goToStartingPosition() {
        report("Starting position");
    }

    @Override
    protected void goToStoredPosition() {
        report("Stored position");
    }
    private void report(String movementString){
        reportMap.report("Climber Movement", movementString);
    }
}
