package com.first1444.frc.robot2020.subsystems.swerve;

import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.drivetrain.swerve.SwerveModule;
import com.first1444.sim.api.event.EventHandler;
import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.NotNull;

public class DummySwerveModule implements SwerveModule {
    public DummySwerveModule() {
    }

    @NotNull
    @Override
    public Rotation2 getCurrentAngle() {
        return Rotation2.ZERO;
    }

    @Override
    public double getCurrentAngleDegrees() {
        return 0;
    }

    @Override
    public double getCurrentAngleRadians() {
        return 0;
    }

    @Override
    public double getDistanceTraveledMeters() {
        return 0;
    }

    @NotNull
    @Override
    public EventHandler getEventHandler() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public String getName() {
        return "dummy";
    }

    @Override
    public void run() {

    }

    @Override
    public void setTargetAngle(@NotNull Rotation2 rotation2) {
    }

    @Override
    public void setTargetAngleDegrees(double v) {

    }

    @Override
    public void setTargetAngleRadians(double v) {

    }

    @Override
    public void setTargetSpeed(double v) {

    }
}
