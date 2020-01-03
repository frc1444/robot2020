package com.first1444.frc.robot2020.sound;

import com.first1444.sim.api.sound.Sound;
import com.first1444.sim.api.sound.implementations.DummySound;

public class SilentSoundMap implements SoundMap {
    public static final SilentSoundMap INSTANCE = new SilentSoundMap();

    private SilentSoundMap(){}
    @Override
    public Sound getDisable() {
        return DummySound.INSTANCE;
    }

    @Override
    public Sound getAutonomousEnable() {
        return DummySound.INSTANCE;
    }

    @Override
    public Sound getTeleopEnable() {
        return DummySound.INSTANCE;
    }

    @Override
    public Sound getMatchEnd() {
        return DummySound.INSTANCE;
    }

    @Override
    public Sound getTargetFound() {
        return DummySound.INSTANCE;
    }

    @Override
    public Sound getTargetFailed() {
        return DummySound.INSTANCE;
    }
}
