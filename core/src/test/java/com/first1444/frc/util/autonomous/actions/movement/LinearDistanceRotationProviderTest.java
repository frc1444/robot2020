package com.first1444.frc.util.autonomous.actions.movement;

import com.first1444.sim.api.Rotation2;
import org.junit.jupiter.api.Test;

import static com.first1444.frc.util.autonomous.actions.movement.LinearDistanceRotationProvider.lerp;
import static org.junit.jupiter.api.Assertions.*;

class LinearDistanceRotationProviderTest {

    @Test
    void testLerp(){
        assertEquals(Rotation2.DEG_45, lerp(Rotation2.ZERO, Rotation2.DEG_90, .5));
        assertEquals(Rotation2.DEG_45, lerp(Rotation2.DEG_90, Rotation2.ZERO, .5));

        assertEquals(Rotation2.DEG_180, lerp(Rotation2.fromDegrees(270 - 45), Rotation2.fromDegrees(90 + 45), .5));

        assertEquals(Rotation2.DEG_180, lerp(Rotation2.DEG_180, Rotation2.DEG_270, 0.0));
    }

}
