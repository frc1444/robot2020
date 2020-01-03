package com.first1444.frc.robot2020;

import com.first1444.dashboard.bundle.DashboardBundle;
import com.first1444.dashboard.livewindow.LiveWindow;
import com.first1444.dashboard.shuffleboard.ShuffleboardContainer;

public class DefaultDashboardMap implements DashboardMap {
    private final DashboardBundle bundle;
    private final ShuffleboardContainer userTab;
    private final ShuffleboardContainer devTab;
    private final ShuffleboardContainer debugTab;
    public DefaultDashboardMap(DashboardBundle bundle){
        this.bundle = bundle;
        userTab = bundle.getShuffleboard().get("user");
        devTab = bundle.getShuffleboard().get("dev");
        debugTab = bundle.getShuffleboard().get("debug");
    }

    @Override
    public DashboardBundle getRawBundle() {
        return bundle;
    }

    @Override
    public ShuffleboardContainer getUserTab() {
        return userTab;
    }

    @Override
    public ShuffleboardContainer getDevTab() {
        return devTab;
    }

    @Override
    public ShuffleboardContainer getDebugTab() {
        return debugTab;
    }

    @Override
    public LiveWindow getLiveWindow() {
        return bundle.getLiveWindow();
    }
}
