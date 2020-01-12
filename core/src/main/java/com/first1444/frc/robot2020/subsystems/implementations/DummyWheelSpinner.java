package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.WheelSpinner;
import com.first1444.frc.util.reportmap.ReportMap;
import com.first1444.sim.api.frc.implementations.infiniterecharge.WheelColor;

import java.text.DecimalFormat;

import static java.util.Objects.requireNonNull;

public class DummyWheelSpinner implements WheelSpinner {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

    private final ReportMap reportMap;
    private double speed = 0;
    private State desiredState = State.IN;

    public DummyWheelSpinner(ReportMap reportMap) {
        this.reportMap = reportMap;
    }

    @Override
    public void run() {
        reportMap.report("Wheel Spinner Speed", FORMAT.format(speed));
        reportMap.report("Wheel Spinner Desired State", desiredState.toString());
        speed = 0;
    }

    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    @Override
    public WheelColor getCurrentColor() {
        return null;
    }

    @Override
    public void setDesiredState(State state) {
        desiredState = requireNonNull(state);
    }

    @Override
    public State getDesiredState() {
        return desiredState;
    }

    @Override
    public State getCurrentState() {
        return desiredState;
    }
}
