package com.first1444.frc.robot2020.vision;

public interface VisionState extends Runnable {
    boolean isEnabled();
    void setEnabled(boolean enabled);
}
