package com.first1444.frc.robot2020.autonomous.actions;

import me.retrodaredevil.action.Action;

public interface DistanceAwayAction extends Action {
    /**
     * @return The distance away in meters
     */
    double getDistanceAway();
}
