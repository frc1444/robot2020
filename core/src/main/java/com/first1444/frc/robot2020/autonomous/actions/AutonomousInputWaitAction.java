package com.first1444.frc.robot2020.autonomous.actions;

import com.first1444.frc.robot2020.actions.TimedAction;
import com.first1444.sim.api.Clock;

import java.util.function.BooleanSupplier;

import static java.util.Objects.requireNonNull;

/**
 * A {@link TimedAction} designed for autonomous that allows you to specify a time to wait while you are able to
 * keep waiting by having a {@link BooleanSupplier} that gives true. You can also end this action early if the other {@link BooleanSupplier}
 * returns true
 */
public class AutonomousInputWaitAction extends TimedAction {
    private final BooleanSupplier shouldWait;
    private final BooleanSupplier shouldStart;

    public AutonomousInputWaitAction(Clock clock, double lastSeconds, BooleanSupplier shouldWait, BooleanSupplier shouldStart) {
        super(true, clock, lastSeconds);
        this.shouldWait = requireNonNull(shouldWait);
        this.shouldStart = requireNonNull(shouldStart);
    }

    @Override
    protected void onIsDoneRequest() {
        super.onIsDoneRequest();
        if(shouldStart.getAsBoolean()){
            setDone(true);
        }
        if(shouldWait.getAsBoolean()){
            setDone(false);
        }
    }
}
