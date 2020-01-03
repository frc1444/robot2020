package com.first1444.frc.robot2020.autonomous.actions.vision;

import com.first1444.frc.robot2020.autonomous.actions.DistanceAwayLinkedAction;
import com.first1444.frc.robot2020.sound.SoundMap;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.drivetrain.swerve.SwerveDrive;
import com.first1444.sim.api.selections.HighestValueSelector;
import com.first1444.sim.api.sensors.Orientation;
import com.first1444.sim.api.surroundings.SurroundingProvider;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.WhenDone;

public final class LineUpCreator {
    private LineUpCreator() { throw new UnsupportedOperationException(); }

    public static Action createLineUpAction(
            Clock clock,
            SurroundingProvider surroundingProvider,
            SwerveDrive drive, Orientation orientation,
            Rotation2 desiredSurroundingRotation,
            Action failAction, Action successAction, SoundMap soundMap){
        return Actions.createLinkedActionRunner(
                createLinkedLineUpAction(clock, surroundingProvider, drive, orientation, desiredSurroundingRotation, failAction, successAction, soundMap),
                WhenDone.CLEAR_ACTIVE_AND_BE_DONE,
                true
        );
    }

    /**
     *
     * @param clock The clock
     * @param surroundingProvider The surrounding provider
     * @param drive
     * @param orientation
     * @param desiredSurroundingRotation The orientation of the vision relative to the robot while lining up. AKA the offset corresponding to the side that will face the vision when lining up
     * @param failAction The fail action
     * @param successAction The success action
     * @return
     */
    public static DistanceAwayLinkedAction createLinkedLineUpAction(
            Clock clock,
            SurroundingProvider surroundingProvider,
            SwerveDrive drive, Orientation orientation,
            Rotation2 desiredSurroundingRotation,
            Action failAction, Action successAction, SoundMap soundMap){
        return new StrafeLineUpAction(clock, surroundingProvider, new HighestValueSelector<>(surrounding -> -surrounding.getTransform().getPosition().distance2(Vector2.ZERO)), drive, orientation, desiredSurroundingRotation, .5, failAction, successAction, soundMap);
    }
}
