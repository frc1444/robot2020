package com.first1444.frc.util.autonomous.actions.movement;

import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;
import org.jetbrains.annotations.NotNull;

public interface DesiredRotationProvider {
    @NotNull
    Rotation2 getDesiredRotation(Vector2 currentAbsolutePosition);
}
