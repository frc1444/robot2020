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
import com.first1444.sim.api.frc.implementations.infiniterecharge.Field2020;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.WhenDone;

import static java.lang.Math.*;
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
            case TEST_MODE: // this is used for whatever we currently happen to be testing
                return creator.getDriveCreator().createTurnToOrientation(startingTransform.getRotation().plusDegrees(180));
            case DO_NOTHING:
                return creator.getLogCreator().createLogMessageAction("Doing nothing for autonomous!");
            case MOVE:
                return createBasicMove(autonomousSettings.getBasicMovementType());
            case MOVE_AND_SPIN:
                return new Actions.ActionQueueBuilder(
                        createBasicMove(autonomousSettings.getBasicMovementType()),
                        creator.getDriveCreator().createSpinAction()
                ).build();
            case MOVE_TURN_SHOOT:
                return new Actions.ActionQueueBuilder(
                        createBasicMove(autonomousSettings.getBasicMovementType()),
                        createSimpleTurnShoot(startingTransform)
                ).build();
            case TRENCH_AUTO:
                if(!onInitLine){
                    return notOnInitLineLogAction;
                }
                return createTrenchAuto(startingTransform);
            case CENTER_RV:
                if(!onInitLine){
                    return notOnInitLineLogAction;
                }
                return createCenterRVAuto(startingTransform);
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
        Rotation2 angle = Field2020.ALLIANCE_POWER_PORT.getTransform().getPosition().minus(startingTransform.getPosition()).getAngle();
        Action alignAndShootAll = creator.getOperatorCreator().createTurretAlignAndShootAll();
        if(abs(angle.minus(startingTransform.getRotation()).getDegrees()) > 45.0){
            return new Actions.ActionQueueBuilder(
                    creator.getDriveCreator().createTurnToOrientation(angle),
                    alignAndShootAll
            ).build();
        }
        return alignAndShootAll;
    }
    // endregion
    private Action createTrenchAuto(Transform2 startingTransform){
        Action intakeForever = creator.getOperatorCreator().createIntakeRunForever();
        return new Actions.ActionQueueBuilder(
                creator.getDriveCreator().createMoveToAbsolute(new Vector2(3.3, 3.3), .7, startingTransform.getRotation()),
                creator.getDriveCreator().createSpinAction(), // get intake down
                creator.getDriveCreator().createTurnToOrientation(Rotation2.DEG_270),
                Actions.createSupplementaryAction(
                        creator.getDriveCreator().createMoveToAbsolute(new Vector2(3.3, 0.9), .7, Rotation2.DEG_270),
                        intakeForever
                ),
                creator.getOperatorCreator().createTurnOnVision(),
                creator.getDriveCreator().createTurnToOrientation(Rotation2.DEG_90),
                creator.getOperatorCreator().createRequireVision(
                        1.0,
                        creator.getOperatorCreator().createTurretAlignAndShootAll(),
                        creator.getLogCreator().createLogWarningAction("No vision for autonomous!")
                ),
                creator.getOperatorCreator().createTurnOffVision()
        ).build();
    }
    private Action createCenterRVAuto(Transform2 startingTransform){
        Action intakeForever = creator.getOperatorCreator().createIntakeRunForever();
        Rotation2 pickupRotation = Rotation2.fromDegrees(-75);
        Vector2 shootPosition = new Vector2(.875, 3.750);
        Rotation2 shootRotation = Rotation2.DEG_90;
        return new Actions.ActionQueueBuilder(
                creator.getDriveCreator().createMoveToAbsolute(new Vector2(0.0, 3.3), .7, startingTransform.getRotation()),
                creator.getDriveCreator().createSpinAction(), // get intake down
                creator.getDriveCreator().createTurnToOrientation(pickupRotation),
                Actions.createSupplementaryAction(
                        new Actions.ActionQueueBuilder(
                                creator.getDriveCreator().createMoveToAbsolute(new Vector2(0.0, 2.4), .3, pickupRotation),
                                creator.getDriveCreator().createMoveToAbsolute(new Vector2(-0.3, 2.6), .2, pickupRotation),
                                creator.getDriveCreator().createMoveToAbsolute(new Vector2(-0.4, 2.23), .2, pickupRotation)
                        ).build(),
                        intakeForever
                ),
                Actions.createSupplementaryAction(creator.getBasicActionCreator().createTimedAction(0.4), intakeForever),
                creator.getOperatorCreator().createTurnOnVision(),
                creator.getDriveCreator().createMoveToAbsolute(
                        shootPosition, new ConstantSpeedProvider(.5),
                        new LinearDistanceRotationProvider(pickupRotation, shootRotation, shootPosition, 1.8, .6)
                ),
                creator.getOperatorCreator().createRequireVision(
                        1.0,
                        creator.getOperatorCreator().createTurretAlignAndShootAll(),
                        creator.getLogCreator().createLogWarningAction("No vision for autonomous!")
                )
        ).build();
    }

}
