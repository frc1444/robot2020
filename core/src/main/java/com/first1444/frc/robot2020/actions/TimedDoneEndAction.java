package com.first1444.frc.robot2020.actions;

import com.first1444.sim.api.Clock;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;

/**
 * Updates an action until it has been "done" for a set amount of time
 */
public class TimedDoneEndAction extends SimpleAction {
    private final Clock clock;
    private final double time;
    private final Action action;

    private Double doneStartTime = null;
    public TimedDoneEndAction(boolean canRecycle, Clock clock, double time, Action action) {
        super(canRecycle);
        this.clock = clock;
        this.time = time;
        this.action = action;
    }

    @Override
    protected void onStart() {
        super.onStart();
        doneStartTime = null;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        action.update();
        if(action.isDone()){
            double now = clock.getTimeSeconds();
            Double doneStartTime = this.doneStartTime;
            if(doneStartTime == null){
                doneStartTime = now;
                this.doneStartTime = doneStartTime;
            }
            if(doneStartTime + time < now) {
                setDone(true);
            }
        } else {
            doneStartTime = null;
        }
    }

    @Override
    protected void onEnd(boolean peacefullyEnded) {
        super.onEnd(peacefullyEnded);
        action.end();
    }
}
