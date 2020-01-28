package com.first1444.frc.robot2020;

import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;

import java.util.Objects;

import static com.first1444.sim.api.MathUtil.mod;
import static java.util.Objects.requireNonNull;

public final class Perspective {
    public static final Perspective ROBOT_FORWARD_CAM = new Perspective(Rotation2.ZERO, false);
    public static final Perspective ROBOT_RIGHT_CAM = new Perspective(Rotation2.DEG_270, false);
    public static final Perspective ROBOT_LEFT_CAM = new Perspective(Rotation2.DEG_90, false);
    public static final Perspective ROBOT_BACK_CAM = new Perspective(Rotation2.DEG_180, false);

    public static final Perspective DRIVER_STATION = new Perspective(Rotation2.DEG_90, true);
    /** When the jumbotron is on the right side of our driver station*/
    public static final Perspective LEFT_VIEW = new Perspective(Rotation2.ZERO, true);
    /** When the jumbotron is on the left side of our driver station*/
    public static final Perspective RIGHT_VIEW = new Perspective(Rotation2.DEG_180, true);

    private final Rotation2 offset;
    private final boolean useGyro;
    private final Vector2 location;

    public Perspective(Rotation2 offset, boolean useGyro, Vector2 location) {
        this.offset = requireNonNull(offset);
        this.useGyro = useGyro;
        this.location = location;
    }
    public Perspective(Rotation2 offset, boolean useGyro) {
        this(offset, useGyro, null);
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
    public Vector2 getLocation(){
        return location;
    }

    @Override
    public String toString() {
        return "Perspective(" +
                "offset=" + offset +
                ", useGyro=" + useGyro +
                ", location=" + location +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Perspective that = (Perspective) o;
        return useGyro == that.useGyro &&
                offset.equals(that.offset) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, useGyro, location);
    }
}
