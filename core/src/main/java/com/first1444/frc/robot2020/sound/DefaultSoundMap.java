package com.first1444.frc.robot2020.sound;

import com.first1444.sim.api.sound.Sound;
import com.first1444.sim.api.sound.SoundCreator;

public class DefaultSoundMap implements SoundMap {

    private final Sound disable, autonomousEnable, teleopEnable, matchEnd, targetFound, targetFailed;

    public DefaultSoundMap(SoundCreator creator){
        disable = creator.create("sounds/disable");
        autonomousEnable = creator.create("sounds/enable_auto");
        teleopEnable = creator.create("sounds/enable_teleop");
        matchEnd = creator.create("sounds/match_end");
        targetFound = creator.create("sounds/found_vision");
        targetFailed = creator.create("sounds/failed_vision");
    }
    @Override
    public Sound getDisable() {
        return disable;
    }

    @Override
    public Sound getAutonomousEnable() {
        return autonomousEnable;
    }

    @Override
    public Sound getTeleopEnable() {
        return teleopEnable;
    }

    @Override
    public Sound getMatchEnd() {
        return matchEnd;
    }

    @Override
    public Sound getTargetFound() {
        return targetFound;
    }

    @Override
    public Sound getTargetFailed() {
        return targetFailed;
    }
}
