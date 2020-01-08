package com.first1444.frc.robot2020.sound;

import com.first1444.sim.api.sound.Sound;

public interface SoundMap {
    Sound getDisable();
    Sound getAutonomousEnable();
    Sound getTeleopEnable();

    Sound getMatchEnd();

    Sound getTargetFound();
    Sound getTargetFailed();

    // region Color Wheel Sounds
    Sound getColorWheelBlue();
    Sound getColorWheelGreen();
    Sound getColorWheelRed();
    Sound getColorWheelYellow();
    // endregion
}
