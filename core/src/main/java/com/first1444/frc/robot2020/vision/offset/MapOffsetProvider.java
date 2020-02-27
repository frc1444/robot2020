package com.first1444.frc.robot2020.vision.offset;

import com.first1444.frc.robot2020.vision.InvalidCameraIdException;
import com.first1444.sim.api.Rotation2;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MapOffsetProvider implements OffsetProvider {
    private final Map<Integer, Rotation2> cameraOffsetMap;

    public MapOffsetProvider(Map<Integer, Rotation2> cameraOffsetMap) {
        this.cameraOffsetMap = cameraOffsetMap;
    }

    @NotNull
    @Override
    public Rotation2 getOffset(int cameraId) throws InvalidCameraIdException {
        Rotation2 r = cameraOffsetMap.get(cameraId);
        if(r == null){
            throw new InvalidCameraIdException(cameraId + " is not a valid camera id! cameraOffsetMap: " + cameraOffsetMap);
        }
        return r;
    }
}
