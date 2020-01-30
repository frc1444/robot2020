package com.first1444.frc.robot2020.vision;

import org.jetbrains.annotations.Nullable;

public interface VisionProvider {
    @Nullable
    VisionInstant getVisionInstant();
}
