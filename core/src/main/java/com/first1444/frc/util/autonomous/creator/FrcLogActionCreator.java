package com.first1444.frc.util.autonomous.creator;

import com.first1444.sim.api.frc.FrcLogger;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;

import java.io.PrintStream;

public class FrcLogActionCreator implements LogActionCreator {
    private final PrintStream messageStream;
    private final FrcLogger logger;

    public FrcLogActionCreator(PrintStream messageStream, FrcLogger logger) {
        this.messageStream = messageStream;
        this.logger = logger;
    }

    @Override
    public Action createLogMessageAction(String message) {
        return Actions.createRunOnce(() -> messageStream.println(message));
    }

    @Override
    public Action createLogWarningAction(String message) {
        return Actions.createRunOnce(() -> logger.reportWarning(message, false));
    }

    @Override
    public Action createLogErrorAction(String message) {
        return Actions.createRunOnce(() -> logger.reportError(message, false));
    }
}
