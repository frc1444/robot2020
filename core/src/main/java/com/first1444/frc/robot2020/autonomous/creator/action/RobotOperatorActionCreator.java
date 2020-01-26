package com.first1444.frc.robot2020.autonomous.creator.action;

import com.first1444.frc.robot2020.Robot;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;

public class RobotOperatorActionCreator implements OperatorActionCreator {
    private final Robot robot;

    public RobotOperatorActionCreator(Robot robot) {
        this.robot = robot;
    }

    @Override
    public Action createIntakeRunForever() {
        return Actions.createRunForever(() -> System.out.println("Intake forever yay")); // TODO
    }

    @Override
    public Action createTurretAlignAndShootAll() {
        return Actions.createRunOnce(() -> System.out.println("turret align then shoot all yay!"));
    }
}
