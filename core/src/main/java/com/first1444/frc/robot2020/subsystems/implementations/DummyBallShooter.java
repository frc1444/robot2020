package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.BallShooter;
import com.first1444.frc.util.reportmap.ReportMap;

import java.text.DecimalFormat;

public class DummyBallShooter implements BallShooter {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
    private final ReportMap reportMap;
    private double rpm;

    public DummyBallShooter(ReportMap reportMap) {
        this.reportMap = reportMap;
    }

    @Override
    public void run() {
        double rpm = this.rpm;
        reportMap.report("Ball Shooter RPM", FORMAT.format(rpm));
    }

    @Override
    public void setDesiredRpm(double rpm) {
        this.rpm = rpm;
    }

    @Override
    public double getCurrentRpm() {
        return rpm;
    }

    @Override
    public boolean atSetpoint() {
        return true; // TODO simulate better
    }
}
