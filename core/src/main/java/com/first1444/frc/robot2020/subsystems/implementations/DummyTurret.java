package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.robot2020.subsystems.Turret;
import com.first1444.frc.util.reportmap.ReportMap;
import com.first1444.sim.api.Rotation2;

public class DummyTurret implements Turret {
    private final ReportMap reportMap;

    private Rotation2 desiredRotation;

    public DummyTurret(ReportMap reportMap) {
        this.reportMap = reportMap;
    }

    @Override
    public void setDesiredRotation(Rotation2 rotation) {
        this.desiredRotation = rotation;
    }

    @Override
    public void run() {
        reportMap.report("Turret Desired Rotation", desiredRotation.toString());
    }
}
