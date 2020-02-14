package com.first1444.frc.robot2020.autonomous.creator;

import com.first1444.frc.robot2020.Robot;
import com.first1444.frc.robot2020.actions.TimedAction;
import me.retrodaredevil.action.Action;

public class BasicActionCreator {
    private final Robot robot;

    public BasicActionCreator(Robot robot) {
        this.robot = robot;
    }

    public Action createTimedAction(double timeSeconds){
        return new TimedAction(true, robot.getClock(), timeSeconds);
    }
}
