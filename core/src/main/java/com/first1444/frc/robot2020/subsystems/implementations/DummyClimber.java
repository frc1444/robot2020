package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.Climber;
import com.first1444.frc.util.reportmap.ReportMap;

import java.text.DecimalFormat;

import static java.lang.Math.abs;

public class DummyClimber implements Climber {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
    private final ReportMap reportMap;

    private double speed = 0.0;
    private boolean startingPosition = false;
    private boolean storedPosition = false;

    public DummyClimber(ReportMap reportMap) {
        this.reportMap = reportMap;
    }

    @Override
    public void setRawSpeed(double speed) {
        if(abs(speed) > 1){
            throw new IllegalArgumentException("speed is out of range! speed=" + speed);
        }
        this.speed = speed;
        startingPosition = false;
        storedPosition = false;
    }

    @Override
    public void startingPosition() {
        startingPosition = true;
        storedPosition = false;
    }

    @Override
    public void storedPosition() {
        startingPosition = false;
        storedPosition = true;
    }

    @Override
    public void run() {
        final Double speed = this.speed;
        final String movementString;
        if(startingPosition){
            movementString = "Starting position";
        } else if(storedPosition){
            movementString = "Stored position";
        } else {
            movementString = "Speed " + FORMAT.format(speed);
        }
        reportMap.report("Climber Movement", movementString);
    }
}
