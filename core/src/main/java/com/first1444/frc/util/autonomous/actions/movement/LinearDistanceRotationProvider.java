package com.first1444.frc.util.autonomous.actions.movement;

import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;
import org.jetbrains.annotations.NotNull;

public class LinearDistanceRotationProvider implements DesiredRotationProvider {
    private final Rotation2 startingRotation;
    private final Rotation2 endingRotation;
    private final Vector2 endingLocation;
    private final double startDistance;
    private final double endDistance;

    public LinearDistanceRotationProvider(Rotation2 startingRotation, Rotation2 endingRotation, Vector2 endingLocation, double startDistance, double endDistance) {
        this.startingRotation = startingRotation;
        this.endingRotation = endingRotation;
        this.endingLocation = endingLocation;
        this.startDistance = startDistance;
        this.endDistance = endDistance;
    }

    @NotNull
    @Override
    public Rotation2 getDesiredRotation(Vector2 currentAbsolutePosition) {
        double distance = currentAbsolutePosition.distance(endingLocation);
        if(distance > startDistance){
            return startingRotation;
        }
        if(distance < endDistance){
            return endingRotation;
        }
        double percent = 1 - (distance - endDistance) / (startDistance - endDistance);
        return lerp(startingRotation, endingRotation, percent);
    }
    public static Rotation2 lerp(Rotation2 a, Rotation2 b, double percent){
        Rotation2 difference = b.minus(a);
        return a.plusRadians(difference.getRadians() * percent);
    }
}
