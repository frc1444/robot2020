package com.first1444.frc.robot2020.autonomous;

import com.first1444.sim.api.Transform2;
import me.retrodaredevil.action.Action;

public interface AutonomousModeCreator {
    Action createAction(AutonomousSettings autonomousSettings, Transform2 startingTransform);
}
