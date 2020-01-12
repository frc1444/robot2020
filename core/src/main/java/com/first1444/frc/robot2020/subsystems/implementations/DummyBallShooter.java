package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.BallShooter;
import com.first1444.frc.util.reportmap.ReportMap;

import java.text.DecimalFormat;

public class DummyBallShooter implements BallShooter {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
    private final ReportMap reportMap;
    private double speed;

    public DummyBallShooter(ReportMap reportMap) {
        this.reportMap = reportMap;
    }

    @Override
    public void run() {
        reportMap.report("Ball Shooter Speed", FORMAT.format(speed));
        speed = 0;
    }

    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
