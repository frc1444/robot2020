package com.first1444.frc.util.autonomous.actions.movement;

import com.first1444.sim.api.MathUtil;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.distance.DistanceAccumulator;
import com.first1444.sim.api.drivetrain.swerve.SwerveDrive;
import com.first1444.sim.api.sensors.Orientation;
import me.retrodaredevil.action.SimpleAction;
import org.jetbrains.annotations.Nullable;

import static com.first1444.sim.api.MeasureUtil.inchesToMeters;
import static java.lang.Math.*;
import static java.util.Objects.requireNonNull;

public class MoveToPosition extends SimpleAction {
    private final SwerveDrive drive;
    private final Orientation orientation;
    private final DistanceAccumulator distanceAccumulator;
    private final Vector2 desiredPosition;
    private final SpeedProvider speedProvider;
    @Nullable
    private final DesiredRotationProvider desiredRotationProvider;

    public MoveToPosition(
            SwerveDrive drive,
            Orientation orientation, DistanceAccumulator distanceAccumulator,
            Vector2 desiredPosition,
            SpeedProvider speedProvider,
            @Nullable DesiredRotationProvider desiredRotationProvider
    ) {
        super(true);
        this.drive = requireNonNull(drive);
        this.orientation = requireNonNull(orientation);
        this.distanceAccumulator = requireNonNull(distanceAccumulator);
        this.desiredPosition = requireNonNull(desiredPosition);
        this.speedProvider = requireNonNull(speedProvider);
        this.desiredRotationProvider = desiredRotationProvider;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        Vector2 position = distanceAccumulator.getPosition();
        double translateSpeed = speedProvider.getSpeed(position);
        Vector2 offsetVector = desiredPosition.minus(position);
        if(offsetVector.compareTo(inchesToMeters(4)) < 0){
            setDone(true);
            return;
        }
        Vector2 translate = offsetVector.getNormalized().times(translateSpeed);
        final double turnAmount;
        if(desiredRotationProvider == null){
            turnAmount = 0;
        } else {
            Rotation2 rotation = desiredRotationProvider.getDesiredRotation(position);
            double minChangeDegrees = MathUtil.minChange(rotation.getDegrees(), orientation.getOrientationDegrees(), 360);
            turnAmount = .75 * max(-1, min(1, minChangeDegrees / -40));
        }
        drive.setControl(translate.rotate(orientation.getOrientation().unaryMinus()), turnAmount, 1.0);
    }

    @Override
    protected void onEnd(boolean peacefullyEnded) {
        super.onEnd(peacefullyEnded);
        drive.setControl(Vector2.ZERO, 0, 0);
    }
}
