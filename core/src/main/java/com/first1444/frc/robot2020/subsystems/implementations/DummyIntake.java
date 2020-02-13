package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.Intake;
import com.first1444.frc.util.reportmap.ReportMap;

import java.text.DecimalFormat;

import static java.util.Objects.requireNonNull;

public class DummyIntake implements Intake {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
    private final ReportMap reportMap;
    // TODO make a BaseIntake class and move a lot of code in here to that class
    private double intakeSpeed;
    private double indexerSpeed;
    private double feederSpeed;

    private double previousIntakeSpeed;

    public DummyIntake(ReportMap reportMap) {
        this.reportMap = requireNonNull(reportMap);
    }

    @Override
    public void setIntakeSpeed(double speed) {
        this.intakeSpeed = speed;
    }

    @Override
    public void setIndexerSpeed(double speed) {
        this.indexerSpeed = speed;
    }

    @Override
    public void setFeederSpeed(double speed) {
        this.feederSpeed = speed;
    }

    @Override
    public void run() {
        reportMap.report("Intake Speed", FORMAT.format(intakeSpeed));
        reportMap.report("Indexer Speed", FORMAT.format(indexerSpeed));
        reportMap.report("Feeder Speed", FORMAT.format(feederSpeed));
        previousIntakeSpeed = intakeSpeed;
        intakeSpeed = 0;
        indexerSpeed = 0;
        feederSpeed = 0;
    }
    public double getPreviousIntakeSpeed(){ return previousIntakeSpeed; }
    protected double getIndexerSpeed(){ return indexerSpeed; }
    protected double getFeederSpeed(){ return feederSpeed; }
}
