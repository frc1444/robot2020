package com.first1444.frc.robot2020.actions;

import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Transform2;
import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.distance.MutableDistanceAccumulator;
import com.first1444.sim.api.frc.implementations.infiniterecharge.Extra2020;
import com.first1444.sim.api.frc.implementations.infiniterecharge.Field2020;
import com.first1444.sim.api.frc.implementations.infiniterecharge.VisionTarget2020;
import com.first1444.sim.api.sensors.Orientation;
import com.first1444.sim.api.surroundings.Surrounding;
import com.first1444.sim.api.surroundings.SurroundingProvider;
import me.retrodaredevil.action.SimpleAction;

import static java.util.Objects.requireNonNull;

/**
 * An action that resets the absolute position when a vision target is visible
 */
public class SurroundingPositionCorrectAction extends SimpleAction {
    private final SurroundingProvider surroundingProvider;
    private final Orientation orientation;
    private final MutableDistanceAccumulator absoluteDistanceAccumulator;
    public SurroundingPositionCorrectAction(SurroundingProvider surroundingProvider, Orientation orientation, MutableDistanceAccumulator absoluteDistanceAccumulator) {
        super(true);
        this.surroundingProvider = surroundingProvider;
        this.orientation = orientation;
        this.absoluteDistanceAccumulator = absoluteDistanceAccumulator;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        Rotation2 orientation = this.orientation.getOrientation();
        Vector2 position = absoluteDistanceAccumulator.getPosition();

        for(Surrounding surrounding : surroundingProvider.getSurroundings()){
            Object extraData = surrounding.getExtraData();
            final Extra2020 extra = extraData instanceof Extra2020 ? (Extra2020) extraData : null;
            Transform2 transform = surrounding.getTransform();
            Transform2 visionTransform = transform.rotate(orientation).plus(position);
            VisionTarget2020 best = null;
            double closest2 = Double.MAX_VALUE;
            for(VisionTarget2020 target : Field2020.ALL_VISION_TARGETS){
                Transform2 targetTransform = target.getTransform();
                double distance2 = targetTransform.getPosition().distance2(visionTransform.getPosition());
                if(distance2 < closest2 && (extra == null || extra.getVisionType() == target.getIdentifier().getVisionType())){
                    best = target;
                    closest2 = distance2;
                }
            }
            requireNonNull(best);
//            System.out.println("We see: " + best.getIdentifier() + " distance error: " + Math.sqrt(closest2) + " yaw error: " + Math.abs(visionTransform.getRotationDegrees() - best.getTransform().getRotationDegrees()));
            absoluteDistanceAccumulator.setPosition(best.getTransform().getPosition().minus(transform.getPosition().rotate(orientation)));
        }
    }
}
