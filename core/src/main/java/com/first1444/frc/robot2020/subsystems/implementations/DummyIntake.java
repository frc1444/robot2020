package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.Intake;
import com.first1444.frc.util.reportmap.ReportMap;

import java.text.DecimalFormat;

import static java.util.Objects.requireNonNull;

public class DummyIntake implements Intake {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
    private final ReportMap reportMap;

    private double speed;
    private State desiredState = State.IN;

    public DummyIntake(ReportMap reportMap) {
        this.reportMap = reportMap;
    }

    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public void setDesiredState(State state) {
        this.desiredState = requireNonNull(state);
    }

    @Override
    public State getDesiredState() {
        return desiredState;
    }

    @Override
    public State getCurrentState() {
        return desiredState;
    }

    @Override
    public void run() {
        reportMap.report("Intake Speed", FORMAT.format(speed));
        reportMap.report("Intake Desired State", desiredState.toString());
    }
}
