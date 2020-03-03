package com.first1444.frc.robot2020.vision;

import com.first1444.sim.api.Clock;
import com.first1444.sim.api.surroundings.Surrounding;
import com.first1444.sim.api.surroundings.SurroundingProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * A vision provider that *always* has up to date data.
 */
public class SimpleInstantVisionProvider implements VisionProvider {
    private final SurroundingProvider surroundingProvider;
    private final VisionState visionState;
    private final Clock clock;

    public SimpleInstantVisionProvider(SurroundingProvider surroundingProvider, VisionState visionState, Clock clock) {
        this.surroundingProvider = surroundingProvider;
        this.visionState = visionState;
        this.clock = clock;
    }

    @Nullable
    @Override
    public VisionInstant getVisionInstant() {
        if(!visionState.isEnabled()){
            return new VisionInstant(Collections.emptyList(), clock.getTimeSeconds());
        }
        List<Surrounding> surroundings = surroundingProvider.getSurroundings();
        return new VisionInstant(surroundings, clock.getTimeSeconds());
    }
}
