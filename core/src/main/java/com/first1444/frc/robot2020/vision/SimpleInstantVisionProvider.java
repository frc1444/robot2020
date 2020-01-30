package com.first1444.frc.robot2020.vision;

import com.first1444.sim.api.Clock;
import com.first1444.sim.api.surroundings.Surrounding;
import com.first1444.sim.api.surroundings.SurroundingProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleInstantVisionProvider implements VisionProvider {
    private final SurroundingProvider surroundingProvider;
    private final Clock clock;

    public SimpleInstantVisionProvider(SurroundingProvider surroundingProvider, Clock clock) {
        this.surroundingProvider = surroundingProvider;
        this.clock = clock;
    }

    @Nullable
    @Override
    public VisionInstant getVisionInstant() {
        List<Surrounding> surroundings = surroundingProvider.getSurroundings();
        return new VisionInstant(surroundings, clock.getTimeSeconds());
    }
}
