package com.first1444.frc.robot2020.autonomous.creator.action;

import com.first1444.frc.robot2020.Robot;
import com.first1444.frc.robot2020.actions.TimedDoneEndAction;
import com.first1444.frc.robot2020.autonomous.actions.TurretAlign;
import com.first1444.frc.robot2020.subsystems.Intake;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.SimpleAction;

public class RobotOperatorActionCreator implements OperatorActionCreator {
    private final Robot robot;

    public RobotOperatorActionCreator(Robot robot) {
        this.robot = robot;
    }

    @Override
    public Action createIntakeRunForever() {
        Intake intake = robot.getIntake();
        return Actions.createRunForever(() -> {
            intake.setIntakeSpeed(1.0);
            intake.setIndexerSpeed(1.0);
        });
    }

    @Override
    public Action createTurretAlignAndShootAll() {
        return new Actions.ActionMultiplexerBuilder(
                new TimedDoneEndAction(false, robot.getClock(), .5, new TurretAlign(robot.getTurret(), robot.getOrientation(), robot.getAbsoluteDistanceAccumulator())),
                Actions.createRunOnce(() -> {})
        ).build();
    }
}
