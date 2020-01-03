package com.first1444.frc.robot2020.autonomous.creator;

import com.first1444.frc.util.autonomous.creator.LogActionCreator;
import com.first1444.frc.util.autonomous.creator.SwerveDriveActionCreator;

public interface AutonomousActionCreator {
    LogActionCreator getLogCreator();
    SwerveDriveActionCreator getDriveCreator();
}
