package com.first1444.frc.util.autonomous.creator;

import com.first1444.frc.util.autonomous.actions.TurnToOrientation;
import com.first1444.frc.util.autonomous.actions.movement.DesiredRotationProvider;
import com.first1444.frc.util.autonomous.actions.movement.MoveToAbsoluteAction;
import com.first1444.frc.util.autonomous.actions.movement.SpeedProvider;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.distance.DistanceAccumulator;
import com.first1444.sim.api.drivetrain.swerve.SwerveDrive;
import com.first1444.sim.api.sensors.Orientation;
import me.retrodaredevil.action.Action;
import org.jetbrains.annotations.Nullable;

public class DefaultSwerveDriveActionCreator implements SwerveDriveActionCreator {

    private final SwerveDrive swerveDrive;
    private final Orientation orientation;
    private final DistanceAccumulator distanceAccumulator;

    public DefaultSwerveDriveActionCreator(SwerveDrive swerveDrive, Orientation orientation, DistanceAccumulator distanceAccumulator) {
        this.swerveDrive = swerveDrive;
        this.orientation = orientation;
        this.distanceAccumulator = distanceAccumulator;
    }

    @Override
    public Action createTurnToOrientation(Rotation2 desiredOrientation) {
        return new TurnToOrientation(desiredOrientation, swerveDrive, orientation);
    }

    @Override
    public Action createMoveToAbsolute(Vector2 position, SpeedProvider speedProvider, @Nullable DesiredRotationProvider desiredRotationProvider) {
        return new MoveToAbsoluteAction(swerveDrive, orientation, distanceAccumulator, position, speedProvider, desiredRotationProvider);
    }
}
