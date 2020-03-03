package com.first1444.frc.robot2020.subsystems;

import com.first1444.dashboard.shuffleboard.ComponentMetadataHelper;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.dashboard.shuffleboard.implementations.GyroMetadataHelper;
import com.first1444.frc.robot2020.DashboardMap;
import com.first1444.frc.robot2020.input.RobotInput;
import com.first1444.sim.api.MathUtil;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.sensors.MutableOrientation;
import com.first1444.sim.api.sensors.Orientation;
import com.first1444.sim.api.sensors.OrientationHandler;
import com.first1444.sim.api.sensors.OrientationSendable;
import me.retrodaredevil.controller.input.InputPart;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class OrientationSystem implements Runnable {

    private final OrientationHandler orientationHandler;
    private final MyOrientation orientation;

    private Double lastRawOrientationDegrees = null;
    private Double lastOrientationDegrees = null;
    private boolean needsReinitialize = false;
    public OrientationSystem(OrientationHandler orientationHandler) {
        this.orientationHandler = requireNonNull(orientationHandler);

        orientation = new MyOrientation();

    }
    public void setToReinitialize(){
        needsReinitialize = true;
    }
    public Orientation getOrientation(){
        return orientation;
    }

    @Override
    public void run() {
        final double rawOrientationDegrees = orientationHandler.getOrientation().getOrientationDegrees();
        orientation.rawOrientationDegrees = rawOrientationDegrees;


        final Double lastRawOrientationDegrees = this.lastRawOrientationDegrees;
        if(lastRawOrientationDegrees != null){
            if(MathUtil.minDistance(rawOrientationDegrees, lastRawOrientationDegrees, 360.0) > 5.0 && !orientationHandler.isInitialized()){
                needsReinitialize = true;
                orientation.setOrientationDegrees(lastOrientationDegrees);
                System.out.println("Gyro must have become disconnected. Will be reinitialized soon.");
            }
        }
        this.lastRawOrientationDegrees = rawOrientationDegrees;
        this.lastOrientationDegrees = orientation.getOrientationDegrees();
        if(needsReinitialize && orientationHandler.isConnected()){
            System.out.println("Going to try to reinitialize gyro.");
            boolean success = orientationHandler.reinitialize();
            if(success){
                needsReinitialize = false;
                System.out.println("Should have reinitialized properly");
            }
        }
    }
    private static class MyOrientation implements MutableOrientation {
        private double rawOrientationDegrees = 0;
        private double offsetDegrees;

        public double getOrientationDegrees() {
            return rawOrientationDegrees - this.offsetDegrees;
        }

        public void setOrientationDegrees(double value) {
            this.offsetDegrees = rawOrientationDegrees - value;
        }

        public double getOrientationRadians() {
            return Math.toRadians(this.getOrientationDegrees());
        }

        public void setOrientationRadians(double value) {
            this.setOrientationDegrees(Math.toDegrees(value));
        }

        @NotNull
        public Rotation2 getOrientation() {
            return Rotation2.fromDegrees(this.getOrientationDegrees());
        }

        public void setOrientation(@NotNull Rotation2 value) {
            this.setOrientationDegrees(value.getDegrees());
        }

    }
}
