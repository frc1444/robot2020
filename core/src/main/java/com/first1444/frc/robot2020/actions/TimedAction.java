package com.first1444.frc.robot2020.actions;

import com.first1444.sim.api.Clock;
import me.retrodaredevil.action.SimpleAction;

import static java.util.Objects.requireNonNull;

public class TimedAction extends SimpleAction {

    private final Clock clock;
    private final double lastSeconds;

    private Double startSeconds = null;

    /**
     *
     * @param clock The clock
     * @param canRecycle Can this action be recycled
     * @param lastSeconds The amount of time in millis for this to last
     */
    public TimedAction(boolean canRecycle, Clock clock, double lastSeconds) {
        super(canRecycle);
        this.clock = requireNonNull(clock);
        this.lastSeconds = lastSeconds;
    }
    protected final double getTimeLeft(){
        Double startSeconds = this.startSeconds;
        if(startSeconds == null){
            throw new IllegalStateException("Cannot get the time left if this hasn't started yet!");
        }
        return (lastSeconds + startSeconds) - clock.getTimeSeconds();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startSeconds = clock.getTimeSeconds();
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        if(getTimeLeft() <= 0) {
            setDone(true);
        }
    }

    @Override
    protected void onEnd(boolean peacefullyEnded) {
        super.onEnd(peacefullyEnded);
        this.startSeconds = null;
    }
}
