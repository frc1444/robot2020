package com.first1444.frc.robot2020.autonomous.options;

public enum BasicMovementType {
    STILL("Still"),
    FORWARD("Forward"),
    BACKWARD("Backward");
    private final String displayName;

    BasicMovementType(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName(){
        return displayName;
    }
}
