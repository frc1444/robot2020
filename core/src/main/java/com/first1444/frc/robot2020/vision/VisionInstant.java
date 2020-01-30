package com.first1444.frc.robot2020.vision;

import com.first1444.sim.api.surroundings.Surrounding;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.Objects.requireNonNull;

public final class VisionInstant {
    private final List<Surrounding> surroundings;
    private final double timestamp;

    public VisionInstant(List<Surrounding> surroundings, double timestamp) {
        this.surroundings = requireNonNull(surroundings);
        this.timestamp = timestamp;
    }

    @NotNull
    public List<Surrounding> getSurroundings() {
        return surroundings;
    }

    public double getTimestamp() {
        return timestamp;
    }
}
