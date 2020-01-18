package com.first1444.frc.robot2020;

import com.first1444.frc.robot2020.subsystems.swerve.ModuleConfig;
import com.first1444.frc.util.valuemap.MutableValueMap;

public interface SwerveSetup {
    int getFLDriveCAN();
    int getFRDriveCAN();
    int getRLDriveCAN();
    int getRRDriveCAN();

    int getFLSteerCAN();
    int getFRSteerCAN();
    int getRLSteerCAN();
    int getRRSteerCAN();

    /** @return wheel base in meters*/
    double getWheelBase();
    /** @return track width in meters*/
    double getTrackWidth();

    int getQuadCountsPerRevolution();

    MutableValueMap<ModuleConfig> setupFL(MutableValueMap<ModuleConfig> config);
    MutableValueMap<ModuleConfig> setupFR(MutableValueMap<ModuleConfig> config);
    MutableValueMap<ModuleConfig> setupRL(MutableValueMap<ModuleConfig> config);
    MutableValueMap<ModuleConfig> setupRR(MutableValueMap<ModuleConfig> config);
}
