package com.first1444.frc.robot2020.sound;

import com.first1444.sim.api.sound.Sound;
import com.first1444.sim.api.sound.SoundCreator;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CachedSoundCreator implements SoundCreator {
    private final SoundCreator soundCreator;
    private final Map<String, Sound> map = new HashMap<>();

    public CachedSoundCreator(SoundCreator soundCreator) {
        this.soundCreator = soundCreator;
    }

    @Override
    public void close() throws Exception {
        soundCreator.close();
    }

    @NotNull
    @Override
    public Sound create(@NotNull String s) {
        return map.computeIfAbsent(s, unused -> soundCreator.create(s));
    }
}
