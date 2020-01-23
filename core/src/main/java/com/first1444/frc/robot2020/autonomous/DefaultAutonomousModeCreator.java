package com.first1444.frc.robot2020.autonomous;

import com.first1444.frc.robot2020.autonomous.creator.AutonomousActionCreator;
import com.first1444.frc.robot2020.autonomous.options.AutonomousSettings;
import com.first1444.frc.robot2020.autonomous.options.AutonomousType;
import com.first1444.frc.robot2020.autonomous.options.BasicMovementType;
import com.first1444.sim.api.Transform2;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;

import static java.util.Objects.requireNonNull;

public class DefaultAutonomousModeCreator implements AutonomousModeCreator {
    /** The amount to move for a basic move in meters */
    private static final double BASIC_MOVE_DISTANCE = 2.0;
    private final AutonomousActionCreator creator;

    public DefaultAutonomousModeCreator(AutonomousActionCreator creator) {
        this.creator = creator;
    }

    @Override
    public Action createAction(AutonomousSettings autonomousSettings, Transform2 startingTransform) {
        AutonomousType autonomousType = autonomousSettings.getAutonomousType();
        if(autonomousType == AutonomousType.DO_NOTHING){
            return creator.getLogCreator().createLogMessageAction("Doing nothing for autonomous!");
        } else if(autonomousType == AutonomousType.MOVE){
            return createBasicMove(autonomousSettings.getBasicMovementType(), startingTransform);
        }
        return creator.getLogCreator().createLogWarningAction("Unable to create action for autonomousType=" + autonomousType);
    }
    private Action createBasicMove(BasicMovementType basicMovementType, Transform2 startingTransform){
        requireNonNull(basicMovementType);
        if(basicMovementType == BasicMovementType.STILL){
            return creator.getLogCreator().createLogMessageAction("Basic move is still");
        } else if(basicMovementType == BasicMovementType.BACKWARD){
            return creator.getDriveCreator().createMoveToAbsolute(startingTransform.getPosition().plus(0.0, -BASIC_MOVE_DISTANCE), .5);
        } else if(basicMovementType == BasicMovementType.FORWARD){
            return creator.getDriveCreator().createMoveToAbsolute(startingTransform.getPosition().plus(0.0, BASIC_MOVE_DISTANCE), .5);
        }
        throw new UnsupportedOperationException("Unknown basicMovementType=" + basicMovementType);
    }
}
