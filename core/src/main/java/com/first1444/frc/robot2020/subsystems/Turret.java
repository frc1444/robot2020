package com.first1444.frc.robot2020.subsystems;

import com.first1444.sim.api.Rotation2;

public interface Turret extends Runnable {
    void setDesiredRotation(Rotation2 rotation);
}
