package com.first1444.frc.robot2020.subsystems;

import com.first1444.dashboard.shuffleboard.ComponentMetadataHelper;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.dashboard.shuffleboard.implementations.GyroMetadataHelper;
import com.first1444.frc.robot2020.DashboardMap;
import com.first1444.frc.robot2020.input.RobotInput;
import com.first1444.sim.api.MathUtil;
import com.first1444.sim.api.sensors.*;
import me.retrodaredevil.controller.input.InputPart;

import static java.util.Objects.requireNonNull;

public class OrientationSystem implements Runnable {

    private final RobotInput robotInput;
    private final OrientationHandler orientationHandler;
    private final MutableOrientation orientation;

    private Double lastRawOrientationDegrees = null;
    private Double lastOrientationDegrees = null;
    public OrientationSystem(DashboardMap dashboardMap, OrientationHandler orientationHandler, RobotInput robotInput) {
        requireNonNull(dashboardMap);
        this.orientationHandler = requireNonNull(orientationHandler);
        this.orientation = new DefaultMutableOrientation(orientationHandler.getOrientation());
        this.robotInput = requireNonNull(robotInput);

        dashboardMap.getUserTab().add("Orientation",
                new SendableComponent<>(new OrientationSendable(orientation)),
                (metadata) -> {
                    new ComponentMetadataHelper(metadata).setSize(2, 3).setPosition(9, 1);
                    new GyroMetadataHelper(metadata).setMajorTickSpacing(90.0).setStartingAngle(90).setCounterClockwise(true);
                }
        );
    }
    public Orientation getOrientation(){
        return orientation;
    }
    public MutableOrientation getMutableOrientation(){
        return orientation;
    }

    @Override
    public void run() {
        // resetting the gyro code
        final InputPart x = robotInput.getResetGyroJoy().getXAxis();
        final InputPart y = robotInput.getResetGyroJoy().getYAxis();
        if (x.isDown() || y.isDown()){
            final double angle = robotInput.getResetGyroJoy().getAngle();
            orientation.setOrientationDegrees(angle);
        }

        if(robotInput.getGyroReinitializeButton().isJustPressed()){
            orientationHandler.reinitialize();
        }

        final double rawOrientationDegrees = orientationHandler.getOrientation().getOrientationDegrees();
        final Double lastRawOrientationDegrees = this.lastRawOrientationDegrees;
        if(lastRawOrientationDegrees != null){
            if(MathUtil.minDistance(rawOrientationDegrees, lastRawOrientationDegrees, 360.0) > 5.0){
                orientationHandler.reinitialize();
                orientation.setOrientationDegrees(lastOrientationDegrees);
                System.out.println("Reinitialized gyro with most recent position!");
            }
        }

        this.lastRawOrientationDegrees = rawOrientationDegrees;
        this.lastOrientationDegrees = orientation.getOrientationDegrees();
    }
}
