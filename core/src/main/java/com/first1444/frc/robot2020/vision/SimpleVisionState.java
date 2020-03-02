package com.first1444.frc.robot2020.vision;

public class SimpleVisionState implements VisionState {
    private boolean enabled = false;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void run() {

    }
}
