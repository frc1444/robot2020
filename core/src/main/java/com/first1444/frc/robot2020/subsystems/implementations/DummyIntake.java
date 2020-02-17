package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.util.reportmap.ReportMap;

import java.text.DecimalFormat;

import static java.util.Objects.requireNonNull;

public class DummyIntake extends BaseIntake {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
    private final ReportMap reportMap;

    private double previousIntakeSpeed;

    public DummyIntake(ReportMap reportMap) {
        this.reportMap = requireNonNull(reportMap);
    }
    @Override
    protected void run(double intakeSpeed, double indexerSpeed, double feederSpeed){
        reportMap.report("Intake Speed", FORMAT.format(intakeSpeed));
        reportMap.report("Indexer Speed", FORMAT.format(indexerSpeed));
        reportMap.report("Feeder Speed", FORMAT.format(feederSpeed));
        previousIntakeSpeed = intakeSpeed;
    }
    public double getPreviousIntakeSpeed(){ return previousIntakeSpeed; }
}
