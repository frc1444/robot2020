package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.Intake;
import com.first1444.frc.util.reportmap.ReportMap;

import java.text.DecimalFormat;

import static java.util.Objects.requireNonNull;

public class DummyIntake implements Intake {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
    private final ReportMap reportMap;

    private double intakeSpeed;

    public DummyIntake(ReportMap reportMap) {
        this.reportMap = reportMap;
    }

    @Override
    public void setIntakeSpeed(double speed) {
        this.intakeSpeed = speed;
    }

    @Override
    public int getBallCount() {
        return 0;
    }
    @Override
    public void run() {
        reportMap.report("Intake Speed", FORMAT.format(intakeSpeed));
    }
}
