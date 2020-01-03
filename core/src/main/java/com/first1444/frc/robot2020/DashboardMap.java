package com.first1444.frc.robot2020;


import com.first1444.dashboard.bundle.DashboardBundle;
import com.first1444.dashboard.livewindow.LiveWindow;
import com.first1444.dashboard.shuffleboard.ShuffleboardContainer;

public interface DashboardMap {
    DashboardBundle getRawBundle();
    ShuffleboardContainer getUserTab();
    ShuffleboardContainer getDevTab();
    ShuffleboardContainer getDebugTab();
    LiveWindow getLiveWindow();
}
