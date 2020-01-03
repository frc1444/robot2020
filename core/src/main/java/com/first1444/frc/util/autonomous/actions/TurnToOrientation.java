package com.first1444.frc.util.autonomous.actions;

import com.first1444.sim.api.MathUtil;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.drivetrain.swerve.SwerveDrive;
import com.first1444.sim.api.sensors.Orientation;
import me.retrodaredevil.action.SimpleAction;

import java.util.function.DoubleConsumer;

import static java.lang.Math.*;
import static java.util.Objects.requireNonNull;

public class TurnToOrientation extends SimpleAction {
    private static final double MAX_SPEED = .5;
    private static final double MIN_SPEED = .15;

    private final Rotation2 desiredOrientation;
    private final DoubleConsumer turnAmountConsumer;
    private final Orientation orientation;

    public TurnToOrientation(Rotation2 desiredOrientation, DoubleConsumer turnAmountConsumer, Orientation orientation) {
        super(true);
        this.desiredOrientation = desiredOrientation;
        this.turnAmountConsumer = requireNonNull(turnAmountConsumer);
        this.orientation = requireNonNull(orientation);
    }
    public TurnToOrientation(Rotation2 desiredOrientation, SwerveDrive drive, Orientation orientation) {
        this(desiredOrientation, (turnAmount) -> drive.setControl(Vector2.ZERO, 1, turnAmount), orientation);
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        final double currentOrientationDegrees = orientation.getOrientationDegrees();

        final double minChange = MathUtil.minChange(desiredOrientation.getDegrees(), currentOrientationDegrees, 360);
        double turnAmount = max(-1, min(1, MathUtil.conservePow(minChange / -110, 2.4))); // when positive turn right, when negative turn left
        if(abs(turnAmount) < MIN_SPEED){
            turnAmount = MIN_SPEED * signum(turnAmount);
        } else if(abs(turnAmount) > MAX_SPEED){
            turnAmount = MAX_SPEED * signum(turnAmount);
        }
        if(Double.isNaN(turnAmount)){
            throw new AssertionError("turnAmount is NaN!");
        }
        if(abs(minChange) < 3){
            setDone(true);
            System.out.println("turn done!");
        } else {
            turnAmountConsumer.accept(turnAmount);
        }
    }
}
