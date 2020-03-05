package com.first1444.frc.util.autonomous.creator;

import com.first1444.frc.util.autonomous.actions.SpinAction;
import com.first1444.frc.util.autonomous.actions.TurnToOrientation;
import com.first1444.frc.util.autonomous.actions.movement.DesiredRotationProvider;
import com.first1444.frc.util.autonomous.actions.movement.MoveToPosition;
import com.first1444.frc.util.autonomous.actions.movement.SpeedProvider;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.distance.DistanceAccumulator;
import com.first1444.sim.api.drivetrain.swerve.SwerveDrive;
import com.first1444.sim.api.sensors.Orientation;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import org.jetbrains.annotations.Nullable;

public class DefaultSwerveDriveActionCreator implements SwerveDriveActionCreator {

    private final Clock clock;
    private final SwerveDrive swerveDrive;
    private final Orientation orientation;
    private final DistanceAccumulator relativeDistanceAccumulator;
    private final DistanceAccumulator absoluteDistanceAccumulator;

    public DefaultSwerveDriveActionCreator(Clock clock, SwerveDrive swerveDrive, Orientation orientation, DistanceAccumulator relativeDistanceAccumulator, DistanceAccumulator absoluteDistanceAccumulator) {
        this.clock = clock;
        this.swerveDrive = swerveDrive;
        this.orientation = orientation;
        this.relativeDistanceAccumulator = relativeDistanceAccumulator;
        this.absoluteDistanceAccumulator = absoluteDistanceAccumulator;
    }

    @Override
    public Action createTurnToOrientation(Rotation2 desiredOrientation) {
        return new TurnToOrientation(desiredOrientation, swerveDrive, orientation);
    }

    @Override
    public Action createSpinAction() {
        return new SpinAction(clock, swerveDrive);
    }

    @Override
    public Action createMoveToAbsolute(Vector2 position, SpeedProvider speedProvider, @Nullable DesiredRotationProvider desiredRotationProvider) {
        return new MoveToPosition(swerveDrive, orientation, absoluteDistanceAccumulator, position, speedProvider, desiredRotationProvider);
    }

    @Override
    public Action createMoveRelative(Vector2 vector, SpeedProvider speedProvider, @Nullable DesiredRotationProvider desiredRotationProvider) {
        return Actions.createDynamicActionRunner(() -> {
            Vector2 startingPosition = relativeDistanceAccumulator.getPosition();
            Vector2 desiredPosition = startingPosition.plus(vector);
            return new MoveToPosition(swerveDrive, orientation, relativeDistanceAccumulator, desiredPosition, speedProvider, desiredRotationProvider);
        });
    }
}
