package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.Climber;
import com.first1444.frc.util.reportmap.ReportMap;

import java.text.DecimalFormat;

public class DummyClimber implements Climber {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
    private final ReportMap reportMap;

    private Double speed = 0.0;
    private NeutralState neutralState = NeutralState.BRAKE;

    public DummyClimber(ReportMap reportMap) {
        this.reportMap = reportMap;
    }

    @Override
    public void setRawSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public void lockCurrentPosition() {
        speed = null;
    }

    @Override
    public void setNeutralState(NeutralState neutralState) {
        this.neutralState = neutralState;
    }

    @Override
    public NeutralState getNeutralState() {
        return neutralState;
    }

    @Override
    public void run() {
        reportMap.report("Raw Speed", FORMAT.format(speed));
        reportMap.report("Neutral State", neutralState.toString());
    }
}
