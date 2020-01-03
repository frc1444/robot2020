package com.first1444.frc.robot2020;

import com.first1444.sim.api.Rotation2;

import static com.first1444.sim.api.MathUtil.mod;

public enum Perspective {
    ROBOT_FORWARD_CAM(Rotation2.ZERO, false),
    ROBOT_RIGHT_CAM(Rotation2.DEG_270, false),
    ROBOT_LEFT_CAM(Rotation2.DEG_90, false),
    ROBOT_BACK_CAM(Rotation2.DEG_180, false),

    DRIVER_STATION(Rotation2.ZERO, true),
    /** When the jumbotron is on the right side of our driver station*/
    JUMBOTRON_ON_RIGHT(Rotation2.DEG_270, true),
    /** When the jumbotron is on the left side of our driver station*/
    JUMBOTRON_ON_LEFT(Rotation2.DEG_90, true);

    private final Rotation2 offset;
    private final boolean useGyro;

    Perspective(Rotation2 offset, boolean useGyro) {
        this.offset = offset;
        this.useGyro = useGyro;
    }
    public double getOffsetDegrees(){
        return offset.getDegrees();
    }
    public double getOffsetRadians(){
        return offset.getRadians();
    }

    /** The rotation relative to the robot that this side is on*/
    public Rotation2 getOffset(){
        return offset;
    }
    public boolean isUseGyro(){
        return useGyro;
    }
}
