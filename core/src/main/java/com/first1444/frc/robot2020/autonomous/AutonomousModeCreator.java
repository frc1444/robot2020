package com.first1444.frc.robot2020.autonomous;

import com.first1444.frc.robot2020.autonomous.creator.AutonomousActionCreator;
import com.first1444.frc.robot2020.autonomous.options.AutonomousSettings;
import com.first1444.frc.robot2020.autonomous.options.AutonomousType;
import com.first1444.frc.robot2020.autonomous.options.BasicMovementType;
import com.first1444.frc.util.autonomous.actions.movement.ConstantSpeedProvider;
import com.first1444.frc.util.autonomous.actions.movement.DesiredRotationProvider;
import com.first1444.frc.util.autonomous.actions.movement.LinearDistanceRotationProvider;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Transform2;
import com.first1444.sim.api.Vector2;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;

import static java.util.Objects.requireNonNull;

public class AutonomousModeCreator {

    /** The amount to move for a basic move in meters */
    private static final double BASIC_MOVE_DISTANCE = 1.3;
    private final AutonomousActionCreator creator;

    public AutonomousModeCreator(AutonomousActionCreator creator) {
        this.creator = creator;
    }

    public Action createAction(AutonomousSettings autonomousSettings, Transform2 startingTransform) {
        boolean onInitLine = startingTransform.getY() >= 3.6 && startingTransform.getY() <= 5.9;
        Action notOnInitLineLogAction = creator.getLogCreator().createLogWarningAction("Are you starting on the init line? We don't want to run auto if you aren't... startingTransform: " + startingTransform);
        if(!onInitLine){
            System.out.println("We aren't starting on the init line!!! startingTransform: " + startingTransform);
        }

        AutonomousType autonomousType = autonomousSettings.getAutonomousType();
        switch (autonomousType) {
            case DO_NOTHING:
                return creator.getLogCreator().createLogMessageAction("Doing nothing for autonomous!");
            case MOVE:
                return createBasicMove(autonomousSettings.getBasicMovementType());
            case TURN_SHOOT_MOVE:
                return new Actions.ActionQueueBuilder(
                        createSimpleTurnShoot(startingTransform),
                        createBasicMove(autonomousSettings.getBasicMovementType())
                ).build();
            case MOVE_TURN_SHOOT:
                return new Actions.ActionQueueBuilder(
                        createBasicMove(autonomousSettings.getBasicMovementType()),
                        createSimpleTurnShoot(startingTransform)
                ).build();
            case SHOOT_IMMEDIATE:
                break;
            case CENTER_RV:
                if(!onInitLine){
                    return notOnInitLineLogAction;
                }
                return createCenterRVAuto(startingTransform);
            case GUARD_TRENCH:
                if(!onInitLine){
                    return notOnInitLineLogAction;
                }
                return createGuardTrench(startingTransform);
        }
        return creator.getLogCreator().createLogWarningAction("Unable to create action for autonomousType=" + autonomousType);
    }
    // region Reused Stuff
    private Action createBasicMove(BasicMovementType basicMovementType){
        requireNonNull(basicMovementType);
        if(basicMovementType == BasicMovementType.STILL){
            return creator.getLogCreator().createLogMessageAction("Basic move is still");
        } else if(basicMovementType == BasicMovementType.BACKWARD){
            return creator.getDriveCreator().createMoveRelative(new Vector2(0.0, -BASIC_MOVE_DISTANCE), .5);
        } else if(basicMovementType == BasicMovementType.FORWARD){
            return creator.getDriveCreator().createMoveRelative(new Vector2(0.0, BASIC_MOVE_DISTANCE), .5);
        }
        throw new UnsupportedOperationException("Unknown basicMovementType=" + basicMovementType);
    }
    private Action createSimpleTurnShoot(Transform2 startingTransform){
        // TODO also physically turn robot if we need to
        return creator.getOperatorCreator().createTurretAlignAndShootAll();
    }
    // endregion

    private Action createGuardTrench(Transform2 startingTransform){
        if(startingTransform.getX() < 2.4){
            return creator.getLogCreator().createLogWarningAction("If you run trench guard, you'll run into something! startingTransform: " + startingTransform);
        }
        return new Actions.ActionQueueBuilder(
                creator.getDriveCreator().createMoveToAbsolute(new Vector2(3.5, -2.2), 1.0, startingTransform.getRotation())
        ).build();
    }
    private Action createCenterRVAuto(Transform2 startingTransform){
        Action intakeForever = creator.getOperatorCreator().createIntakeRunForever();
        Rotation2 pickupRotation = Rotation2.fromDegrees(-75);
        Vector2 shootPosition = new Vector2(.875, 3.750);
        Rotation2 shootRotation = Rotation2.DEG_90;
        return new Actions.ActionQueueBuilder(
                creator.getDriveCreator().createMoveToAbsolute(new Vector2(0.0, 3.3), .7, startingTransform.getRotation()),
                creator.getDriveCreator().createTurnToOrientation(pickupRotation),
                Actions.createSupplementaryAction(
                        new Actions.ActionQueueBuilder(
                                creator.getDriveCreator().createMoveToAbsolute(new Vector2(0.0, 2.4), .3, pickupRotation),
                                creator.getDriveCreator().createMoveToAbsolute(new Vector2(-0.4, 2.23), .2, pickupRotation)
                        ).build(),
                        intakeForever
                ),
                Actions.createSupplementaryAction(creator.getBasicActionCreator().createTimedAction(0.4), intakeForever),
                creator.getDriveCreator().createMoveToAbsolute(
                        shootPosition, new ConstantSpeedProvider(.5),
                        new LinearDistanceRotationProvider(pickupRotation, shootRotation, shootPosition, 1.8, .6)
                ),
                creator.getOperatorCreator().createTurretAlignAndShootAll()
        ).build();
    }

}
