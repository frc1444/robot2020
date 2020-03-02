package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.music.Orchestra;
import com.first1444.dashboard.advanced.implementations.chooser.ChooserSendable;
import com.first1444.dashboard.advanced.implementations.chooser.MutableMappedChooserProvider;
import com.first1444.dashboard.advanced.implementations.chooser.SimpleMappedChooserProvider;
import com.first1444.dashboard.shuffleboard.ComponentMetadataHelper;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.sim.api.Clock;

import java.util.Arrays;

public class SoundHandler {
    private final Clock clock;
    private final MutableMappedChooserProvider<String> soundChooser;
    private final Orchestra sound;

    public SoundHandler(Clock clock, DashboardMap dashboardMap, TalonFX... talons) {
        this.clock = clock;

        soundChooser = new SimpleMappedChooserProvider<>();
        soundChooser.addOption("Mute", null, true);
        soundChooser.addOption("test", "sound/test.chrp");
//        sound = new Orchestra(Arrays.asList(talons));
        sound = null;

        dashboardMap.getUserTab().add("Sound Chooser", new SendableComponent<>(new ChooserSendable(soundChooser)), (metadata) -> new ComponentMetadataHelper(metadata)
                .setSize(2, 1)
                .setPosition(6,1)
        );
    }

    public void playSoundFile(String filePath) {
        if (!sound.isPlaying()) {
            sound.loadMusic(filePath);
            sound.play();
        }
    }

    public void update() {
        if(sound == null){
            return;
        }
        String selectedSound = soundChooser.getSelected();
        if (selectedSound == null) {
            sound.stop();
        }
    }
}
