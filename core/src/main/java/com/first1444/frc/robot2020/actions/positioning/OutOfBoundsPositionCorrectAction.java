package com.first1444.frc.robot2020.actions.positioning;

import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.distance.MutableDistanceAccumulator;
import com.first1444.sim.api.frc.implementations.infiniterecharge.Field2020;
import me.retrodaredevil.action.SimpleAction;

public class OutOfBoundsPositionCorrectAction extends SimpleAction {
    private static final double MAX_X = Field2020.WIDTH / 2.0;
    private static final double MIN_X = -MAX_X;
    private static final double MAX_Y = Field2020.LENGTH / 2.0;
    private static final double MIN_Y = -MAX_Y;

    private final MutableDistanceAccumulator absoluteDistanceAccumulator;

    public OutOfBoundsPositionCorrectAction(MutableDistanceAccumulator absoluteDistanceAccumulator) {
        super(true);
        this.absoluteDistanceAccumulator = absoluteDistanceAccumulator;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        Vector2 position = absoluteDistanceAccumulator.getPosition();
        double x = position.getX();
        double y = position.getY();
        boolean changed = false;
        if(x < MIN_X){
            x = MIN_X;
            changed = true;
        } else if(x > MAX_X){
            x = MAX_X;
            changed = true;
        }
        if(y < MIN_Y){
            y = MIN_Y;
            changed = true;
        } else if(y > MAX_Y){
            y = MAX_Y;
            changed = true;
        }
        if(changed){
            absoluteDistanceAccumulator.setPosition(new Vector2(x, y));
        }
    }
}
