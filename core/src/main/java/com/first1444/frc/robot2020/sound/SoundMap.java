package com.first1444.frc.robot2020.sound;

import com.first1444.sim.api.sound.Sound;
import com.first1444.sim.api.sound.SoundCreator;
import com.first1444.sim.api.sound.implementations.DummySoundCreator;

public class SoundMap {
    public static final SoundMap SILENT = new SoundMap(DummySoundCreator.INSTANCE);

    private final SoundCreator creator;

    public SoundMap(SoundCreator creator){
        this.creator = new CachedSoundCreator(creator);
    }
    private Sound get(String s){
        return creator.create(s);
    }
    public Sound getAutonomousEnable() { return get("sounds/enable_auto"); }
    public Sound getAutonomousDisable() { return get("sounds/disable_auto"); }
    public Sound getTeleopEnable() { return get("sounds/enable_teleop"); }
    public Sound getTeleopDisable() { return get("sounds/disable_teleop"); }
    public Sound getPostMatchFiveSeconds(){ return get("sounds/post_match_five_seconds"); }

    public Sound getTargetFound() { return get("sounds/found_vision"); }
    public Sound getTargetFailed() { return get("sounds/failed_vision"); }

    public Sound getColorWheelBlue() { return get("sounds/color_wheel/blue"); }
    public Sound getColorWheelGreen() { return get("sounds/color_wheel/green"); }
    public Sound getColorWheelRed() { return get("sounds/color_wheel/red"); }
    public Sound getColorWheelYellow() { return get("sounds/color_wheel/yellow"); }

    public Sound getBallCount0(){ return get("sounds/ball_count/0"); }
    public Sound getBallCount1(){ return get("sounds/ball_count/1"); }
    public Sound getBallCount2(){ return get("sounds/ball_count/2"); }
    public Sound getBallCount3(){ return get("sounds/ball_count/3"); }
    public Sound getBallCount4(){ return get("sounds/ball_count/4"); }
    public Sound getBallCount5(){ return get("sounds/ball_count/5"); }
}
