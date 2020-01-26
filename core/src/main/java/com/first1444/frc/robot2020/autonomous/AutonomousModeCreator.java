package com.first1444.frc.robot2020.autonomous;

import com.first1444.frc.robot2020.autonomous.creator.AutonomousActionCreator;
import com.first1444.frc.robot2020.autonomous.options.AutonomousSettings;
import com.first1444.frc.robot2020.autonomous.options.AutonomousType;
import com.first1444.frc.robot2020.autonomous.options.BasicMovementType;
import com.first1444.sim.api.Transform2;
import com.first1444.sim.api.Vector2;
import me.retrodaredevil.action.Action;

import static java.util.Objects.requireNonNull;

public class AutonomousModeCreator {

    /** The amount to move for a basic move in meters */
    private static final double BASIC_MOVE_DISTANCE = 1.3;
    private final AutonomousActionCreator creator;

    public AutonomousModeCreator(AutonomousActionCreator creator) {
        this.creator = creator;
    }

    public Action createAction(AutonomousSettings autonomousSettings, Transform2 startingTransform) {
        AutonomousType autonomousType = autonomousSettings.getAutonomousType();
        switch (autonomousType) {
            case DO_NOTHING:
                return creator.getLogCreator().createLogMessageAction("Doing nothing for autonomous!");
            case MOVE:
                return createBasicMove(autonomousSettings.getBasicMovementType());
            case TURN_SHOOT:
                break;
            case MOVE_TURN_SHOOT:
                break;
            case SHOOT_IMMEDIATE:
                break;
        }
        return creator.getLogCreator().createLogWarningAction("Unable to create action for autonomousType=" + autonomousType);
    }
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

}
