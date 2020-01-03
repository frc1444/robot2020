package com.first1444.frc.util.autonomous.creator;

import com.first1444.frc.util.autonomous.actions.movement.ConstantRotationProvider;
import com.first1444.frc.util.autonomous.actions.movement.ConstantSpeedProvider;
import com.first1444.frc.util.autonomous.actions.movement.DesiredRotationProvider;
import com.first1444.frc.util.autonomous.actions.movement.SpeedProvider;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;
import me.retrodaredevil.action.Action;
import org.jetbrains.annotations.Nullable;

public interface SwerveDriveActionCreator {
    Action createTurnToOrientation(Rotation2 desiredOrientation);

    Action createMoveToAbsolute(Vector2 position, SpeedProvider speedProvider, @Nullable DesiredRotationProvider desiredRotationProvider);
    default Action createMoveToAbsolute(Vector2 position, double speed, @Nullable Rotation2 faceDirection) {
        return createMoveToAbsolute(position, new ConstantSpeedProvider(speed), faceDirection == null ? null : new ConstantRotationProvider(faceDirection));
    }
    default Action createMoveToAbsolute(Vector2 position, double speed) {
        return createMoveToAbsolute(position, speed, null);
    }
    default Action createMoveToAbsolute(Vector2 position, SpeedProvider speedProvider) {
        return createMoveToAbsolute(position, speedProvider, null);
    }

}
