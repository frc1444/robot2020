package com.first1444.frc.robot2020.autonomous.actions;

import com.first1444.frc.robot2020.subsystems.Turret;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.distance.DistanceAccumulator;
import com.first1444.sim.api.frc.implementations.infiniterecharge.Field2020;
import com.first1444.sim.api.sensors.Orientation;
import me.retrodaredevil.action.SimpleAction;

import static java.lang.Math.abs;

public class TurretAlign extends SimpleAction {
    private final Turret turret;
    private final Orientation orientation;
    private final DistanceAccumulator distanceAccumulator;
    public TurretAlign(Turret turret, Orientation orientation, DistanceAccumulator distanceAccumulator) {
        super(true);
        this.turret = turret;
        this.orientation = orientation;
        this.distanceAccumulator = distanceAccumulator;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        Vector2 position = distanceAccumulator.getPosition().plus(Turret.TURRET_OFFSET);
        Rotation2 rotation = orientation.getOrientation();
        Rotation2 angle = Field2020.ALLIANCE_POWER_PORT.getTransform().getPosition().minus(position).getAngle();

        Rotation2 desired = angle.minus(rotation);

        if (desired.getRadians() <= Turret.MAX_ROTATION.getRadians() && desired.getRadians() >= Turret.MIN_ROTATION.getRadians()) {
            turret.setDesiredState(Turret.DesiredState.createDesiredRotation(desired));
            Rotation2 current = turret.getCurrentRotation();
            if(abs(current.minus(desired).getDegrees()) < 5.0){
                setDone(true);
            }
        }
    }
}
