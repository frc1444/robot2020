package com.first1444.frc.util.autonomous.creator;

import me.retrodaredevil.action.Action;

public interface LogActionCreator {
    Action createLogMessageAction(String message);
    Action createLogWarningAction(String message);
    Action createLogErrorAction(String message);
}
