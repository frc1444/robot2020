package com.first1444.frc.robot2020.actions.positioning;

import com.first1444.frc.robot2020.vision.VisionInstant;
import com.first1444.frc.robot2020.vision.VisionProvider;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Transform2;
import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.distance.MutableDistanceAccumulator;
import com.first1444.sim.api.frc.implementations.infiniterecharge.Extra2020;
import com.first1444.sim.api.frc.implementations.infiniterecharge.Field2020;
import com.first1444.sim.api.frc.implementations.infiniterecharge.VisionTarget2020;
import com.first1444.sim.api.sensors.MutableOrientation;
import com.first1444.sim.api.surroundings.Surrounding;
import me.retrodaredevil.action.SimpleAction;

import static java.util.Objects.requireNonNull;

/**
 * An action that resets the absolute position when a vision target is visible
 */
public class SurroundingPositionCorrectAction extends SimpleAction {
    private static final double VISION_DELAY_TIME_ALLOWED = .3;
    private final Clock clock;
    private final VisionProvider visionProvider;
    private final MutableOrientation orientation;
    private final MutableDistanceAccumulator absoluteDistanceAccumulator;

    private double lastTimestamp = 0;
    public SurroundingPositionCorrectAction(Clock clock, VisionProvider visionProvider, MutableOrientation orientation, MutableDistanceAccumulator absoluteDistanceAccumulator) {
        super(true);
        this.clock = clock;
        this.visionProvider = visionProvider;
        this.orientation = orientation;
        this.absoluteDistanceAccumulator = absoluteDistanceAccumulator;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        double currentTimestamp = clock.getTimeSeconds();
        Rotation2 rotation = this.orientation.getOrientation();
        Vector2 position = absoluteDistanceAccumulator.getPosition();

        final double lastTimestamp = this.lastTimestamp;
        VisionInstant instant = visionProvider.getVisionInstant();
        if(instant != null) {
            for (Surrounding surrounding : instant.getSurroundings()) {
                double timestamp = surrounding.getTimestamp();
                this.lastTimestamp = timestamp; // for this to work, we're just going to assume that all the surroundings we see have the same timestamp.
                if (timestamp + VISION_DELAY_TIME_ALLOWED < currentTimestamp) {
                    continue; // this is too old!
                }
                if (timestamp <= lastTimestamp) {
                    continue; // this isn't new!
                }

                Object extraData = surrounding.getExtraData();
                final Extra2020 extra = extraData instanceof Extra2020 ? (Extra2020) extraData : null;
                Transform2 transform = surrounding.getTransform();
                Transform2 visionTransform = transform.rotate(rotation).plus(position);
                VisionTarget2020 best = null;
                double closest2 = Double.MAX_VALUE;
                for (VisionTarget2020 target : Field2020.ALL_VISION_TARGETS) {
                    Transform2 targetTransform = target.getTransform();
                    double distance2 = targetTransform.getPosition().distance2(visionTransform.getPosition());
                    if (distance2 < closest2 && (extra == null || extra.getVisionType() == target.getIdentifier().getVisionType())) {
                        best = target;
                        closest2 = distance2;
                    }
                }
                requireNonNull(best);
//            System.out.println("We see: " + best.getIdentifier() + " distance error: " + Math.sqrt(closest2) + " yaw error: " + Math.abs(visionTransform.getRotationDegrees() - best.getTransform().getRotationDegrees()));
                Rotation2 visionOffset = transform.getRotation();

                Rotation2 calculatedOrientation = best.getTransform().getRotation().minus(visionOffset);
                absoluteDistanceAccumulator.setPosition(best.getTransform().getPosition().minus(transform.getPosition().rotate(calculatedOrientation)));
                orientation.setOrientation(calculatedOrientation);
            }
        }
    }
}
