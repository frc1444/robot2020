package com.first1444.frc.util.autonomous.actions.movement;

import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;
import org.jetbrains.annotations.NotNull;

public class ConstantRotationProvider implements DesiredRotationProvider {
    private final Rotation2 desiredRotation;

    public ConstantRotationProvider(Rotation2 desiredRotation) {
        this.desiredRotation = desiredRotation;
    }

    @NotNull
    @Override
    public Rotation2 getDesiredRotation(Vector2 currentAbsolutePosition) {
        return desiredRotation;
    }

    @Override
    public String toString() {
        return "ConstantRotationProvider(" +
            "desiredRotation=" + desiredRotation +
            ')';
    }
}
