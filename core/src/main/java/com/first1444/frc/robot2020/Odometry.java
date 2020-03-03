package com.first1444.frc.robot2020;

import com.first1444.dashboard.ActiveComponent;
import com.first1444.dashboard.ActiveComponentMultiplexer;
import com.first1444.dashboard.advanced.Sendable;
import com.first1444.dashboard.shuffleboard.ComponentMetadataHelper;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.dashboard.shuffleboard.implementations.GyroMetadataHelper;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
import com.first1444.dashboard.value.implementations.PropertyActiveComponent;
import com.first1444.frc.robot2020.input.RobotInput;
import com.first1444.frc.robot2020.subsystems.OrientationSystem;
import com.first1444.sim.api.distance.*;
import com.first1444.sim.api.drivetrain.swerve.SwerveDriveData;
import com.first1444.sim.api.sensors.*;

import java.util.Arrays;

public class Odometry implements Runnable {
    private final DistanceAccumulator relativeDistanceAccumulator;
    private final DistanceAccumulator relativeVisionDistanceAccumulator;

    private final MutableDistanceAccumulator absoluteDistanceAccumulator;
    private final MutableDistanceAccumulator absoluteAndVisionDistanceAccumulator;
    private final MutableOrientation absoluteOrientation;
    private final MutableOrientation absoluteAndVisionOrientation;

    private final OrientationSystem orientationSystem;
    private final RobotInput robotInput;

    public Odometry(OrientationHandler orientationHandler, SwerveDriveData swerveDriveData, RobotInput robotInput, DashboardMap dashboardMap){
        this.robotInput = robotInput;
        orientationSystem = new OrientationSystem(orientationHandler);
        absoluteOrientation = new DefaultMutableOrientation(orientationSystem.getOrientation());
        absoluteAndVisionOrientation = new DefaultMutableOrientation(orientationSystem.getOrientation());

        relativeDistanceAccumulator = new DeltaDistanceAccumulator(new OrientationDeltaDistanceCalculator(new SwerveDeltaDistanceCalculator(swerveDriveData), absoluteOrientation));
        relativeVisionDistanceAccumulator = new DeltaDistanceAccumulator(new OrientationDeltaDistanceCalculator(new SwerveDeltaDistanceCalculator(swerveDriveData), absoluteAndVisionOrientation));

        absoluteDistanceAccumulator = new DefaultMutableDistanceAccumulator(relativeDistanceAccumulator, false);
        absoluteAndVisionDistanceAccumulator = new DefaultMutableDistanceAccumulator(relativeVisionDistanceAccumulator, false);

        updateDashboard(dashboardMap);
    }
    private void updateDashboard(DashboardMap dashboardMap){
        dashboardMap.getUserTab().add("Orientation",
                new SendableComponent<>(new OrientationSendable(absoluteAndVisionOrientation)),
                (metadata) -> {
                    new ComponentMetadataHelper(metadata).setSize(2, 3).setPosition(2, 0);
                    new GyroMetadataHelper(metadata).setMajorTickSpacing(90.0).setStartingAngle(90).setCounterClockwise(true);
                }
        );
        dashboardMap.getUserTab().add(
                "Position",
                new SendableComponent<>((Sendable<ActiveComponent>) (title, dashboard) -> new ActiveComponentMultiplexer(title,
                        Arrays.asList(
                                new PropertyActiveComponent("", dashboard.get("x"), ValueProperty.createGetOnly(() -> BasicValue.makeString(Constants.DECIMAL_FORMAT.format(absoluteDistanceAccumulator.getPosition().getX())))),
                                new PropertyActiveComponent("", dashboard.get("y"), ValueProperty.createGetOnly(() -> BasicValue.makeString(Constants.DECIMAL_FORMAT.format(absoluteDistanceAccumulator.getPosition().getY()))))
                        )
                )),
                metadata -> new ComponentMetadataHelper(metadata).setSize(2, 2).setPosition(4, 0)
        );
    }

    @Override
    public void run() {
        if(robotInput.getGyroReinitializeButton().isJustPressed()){
            orientationSystem.setToReinitialize();
            System.out.println("Gyro reinitialize pressed. Will be reinitialized soon.");
        }
        orientationSystem.run();
        if(robotInput.getMovementJoyResetGyroButton().isJustPressed()){
            final double angle = robotInput.getMovementJoy().getAngle();
            absoluteOrientation.setOrientationDegrees(angle);
            absoluteAndVisionOrientation.setOrientationDegrees(angle);
        }
        relativeDistanceAccumulator.run();
        relativeVisionDistanceAccumulator.run();
    }

    public DistanceAccumulator getRelativeDistanceAccumulator() { return relativeDistanceAccumulator; }
    public MutableDistanceAccumulator getAbsoluteDistanceAccumulator() { return absoluteDistanceAccumulator; }
    public MutableDistanceAccumulator getAbsoluteAndVisionDistanceAccumulator() { return absoluteAndVisionDistanceAccumulator; }
    public MutableOrientation getAbsoluteOrientation() { return absoluteOrientation; }
    public MutableOrientation getAbsoluteAndVisionOrientation() { return absoluteAndVisionOrientation; }
}
