package com.first1444.frc.robot2020.subsystems.implementations;

import com.first1444.frc.util.reportmap.ReportMap;
import com.first1444.sim.api.Rotation2;

import static java.util.Objects.requireNonNull;

public class DummyTurret extends BaseTurret {
    private final ReportMap reportMap;
    private Rotation2 currentRotation;

    public DummyTurret(ReportMap reportMap) {
        this.reportMap = requireNonNull(reportMap);
    }

    @Override
    public void run(DesiredState desiredState) {
        reportMap.report("Turret Desired", desiredState.toString());
        Rotation2 rotation = desiredState.getDesiredRotation();
        if(rotation != null){
            currentRotation = rotation;
        }
    }

    @Override
    public Rotation2 getCurrentRotation() {
        return currentRotation;
    }
}
