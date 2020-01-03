package com.first1444.frc.robot2020.autonomous.actions;

import com.first1444.frc.util.autonomous.actions.TurnToOrientation;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.sensors.Orientation;
import me.retrodaredevil.action.Action;
import org.jetbrains.annotations.NotNull;

import java.util.function.DoubleConsumer;

public class TurnToOrientationTest {
	private static final DoubleConsumer TURN_AMOUNT_PRINTER = (turnAmount) -> System.out.println("turnAmount: " + turnAmount);
	public static void main(String[] args){
		final Orientation orientation = createConstantDegrees(90);
		final Action action = new TurnToOrientation(Rotation2.fromDegrees(95.0), TURN_AMOUNT_PRINTER, orientation);
		action.update();
	}
	public static Orientation createConstantDegrees(double valueDegrees){
	    Rotation2 rotation = Rotation2.fromDegrees(valueDegrees);
	    return new Orientation() {
            @NotNull
            @Override
            public Rotation2 getOrientation() {
                return rotation;
            }

            @Override
            public double getOrientationDegrees() {
                return valueDegrees;
            }

            @Override
            public double getOrientationRadians() {
                return rotation.getRadians();
            }
        };
    }
}
