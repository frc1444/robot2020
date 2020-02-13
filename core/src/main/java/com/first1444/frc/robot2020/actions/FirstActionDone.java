package com.first1444.frc.robot2020.actions;

import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.ActionMultiplexer;
import me.retrodaredevil.action.SetActionMultiplexer;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class FirstActionDone extends SetActionMultiplexer {
    /*
    NOTE: This is tightly coupled to the behaviour of SetActionMultiplexer. It is not ideal
     */
    private FirstActionDone(Set<Action> actionSet) {
        super(false, actionSet, false, false);
    }
    public static ActionMultiplexer create(Action... actions){
        return new FirstActionDone(new LinkedHashSet<>(Arrays.asList(actions)));
    }

    @Override
    protected void onUpdate() {
        int oldSize = getActiveActions().size();
        super.onUpdate();
        int newSize = getActiveActions().size();
        if(oldSize != newSize){
            setDone(true);
        }
    }
}
