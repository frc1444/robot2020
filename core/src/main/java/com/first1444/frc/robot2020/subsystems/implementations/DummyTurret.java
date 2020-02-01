package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.util.reportmap.ReportMap;

import static java.util.Objects.requireNonNull;

public class DummyTurret extends BaseTurret {
    private final ReportMap reportMap;

    public DummyTurret(ReportMap reportMap) {
        this.reportMap = requireNonNull(reportMap);
    }

    @Override
    public void run(DesiredState desiredState) {
        reportMap.report("Turret Desired", desiredState.toString());
    }
}
