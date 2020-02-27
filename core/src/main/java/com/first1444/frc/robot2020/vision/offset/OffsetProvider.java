package com.first1444.frc.robot2020.vision.offset;

import com.first1444.frc.robot2020.vision.InvalidCameraIdException;
import com.first1444.sim.api.Rotation2;
import org.jetbrains.annotations.NotNull;

public interface OffsetProvider {
    @NotNull
    Rotation2 getOffset(int cameraId) throws InvalidCameraIdException;
}
