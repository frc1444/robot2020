/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package com.first1444.frc.robot2020.setpoint;

import com.first1444.dashboard.ActiveComponent;
import com.first1444.dashboard.ActiveComponentMultiplexer;
import com.first1444.dashboard.BasicDashboard;
import com.first1444.dashboard.advanced.Sendable;
import com.first1444.dashboard.advanced.SendableHelper;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
import com.first1444.dashboard.value.implementations.PropertyActiveComponent;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.ValueMap;
import com.first1444.sim.api.Clock;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * Implements a PID control loop.
 */
public class PIDController implements Sendable<ActiveComponent> {
    private final Clock clock;
    private final double defaultPeriod;
    private final double maxPeriod;
    private Double lastTimeSeconds = null;

    // Factor for "proportional" control
    private double p;
    // Factor for "integral" control
    private double i;
    // Factor for "derivative" control
    private double d;

    private double maximumIntegral = 1.0;

    private double minimumIntegral = -1.0;

    // Maximum input - limit setpoint to this
    private double maximumInput;

    // Minimum input - limit setpoint to this
    private double minimumInput;

    // Input range - difference between maximum and minimum
    private double inputRange;

    // Do the endpoints wrap around? eg. Absolute encoder
    private boolean continuous;

    // The error at the time of the most recent call to calculate()
    private double positionError;
    private double velocityError;

    // The error at the time of the second-most-recent call to calculate() (used to compute velocity)
    private double previousError;

    // The sum of the errors for use in the integral calc
    private double totalError;

    // The percentage or absolute error that is considered at setpoint.
    private double positionTolerance = 0.05;
    private double velocityTolerance = Double.POSITIVE_INFINITY;

    private double setpoint;

    /**
     * Allocates a PIDController with the given constants for p, i, and d.
     * @param clock
     * @param defaultPeriod
     * @param maxPeriod
     * @param p     The proportional coefficient.
     * @param i     The integral coefficient.
     * @param d     The derivative coefficient.
     */
    public PIDController(Clock clock, double defaultPeriod, double maxPeriod, double p, double i, double d) {
        this.defaultPeriod = defaultPeriod;
        this.maxPeriod = maxPeriod;
        this.p = p;
        this.i = i;
        this.d = d;
        this.clock = clock;
    }

    /**
     * Sets the PID Controller gain parameters.
     *
     * <p>Set the proportional, integral, and differential coefficients.
     *
     * @param p The proportional coefficient.
     * @param i The integral coefficient.
     * @param d The derivative coefficient.
     */
    public void setPID(double p, double i, double d) {
        this.p = p;
        this.i = i;
        this.d = d;
    }
    public void applyFrom(ValueMap<PidKey> pidConfig){
        setPID(pidConfig.getDouble(PidKey.P), pidConfig.getDouble(PidKey.I), pidConfig.getDouble(PidKey.D));
    }

    /**
     * Sets the Proportional coefficient of the PID controller gain.
     *
     * @param p proportional coefficient
     */
    public void setP(double p) {
        this.p = p;
    }

    /**
     * Sets the Integral coefficient of the PID controller gain.
     *
     * @param i integral coefficient
     */
    public void setI(double i) {
        this.i = i;
    }

    /**
     * Sets the Differential coefficient of the PID controller gain.
     *
     * @param d differential coefficient
     */
    public void setD(double d) {
        this.d = d;
    }

    /**
     * Get the Proportional coefficient.
     *
     * @return proportional coefficient
     */
    public double getP() {
        return p;
    }

    /**
     * Get the Integral coefficient.
     *
     * @return integral coefficient
     */
    public double getI() {
        return i;
    }

    /**
     * Get the Differential coefficient.
     *
     * @return differential coefficient
     */
    public double getD() {
        return d;
    }

    /**
     * Sets the setpoint for the PIDController.
     *
     * @param setpoint The desired setpoint.
     */
    public void setSetpoint(double setpoint) {
        if (maximumInput > minimumInput) {
            this.setpoint = max(minimumInput, min(maximumInput, setpoint));
        } else {
            this.setpoint = setpoint;
        }
    }

    /**
     * Returns the current setpoint of the PIDController.
     *
     * @return The current setpoint.
     */
    public double getSetpoint() {
        return setpoint;
    }

    /**
     * Returns true if the error is within the percentage of the total input range, determined by
     * SetTolerance. This asssumes that the maximum and minimum input were set using SetInput.
     *
     * <p>This will return false until at least one input value has been computed.
     *
     * @return Whether the error is within the acceptable bounds.
     */
    public boolean isAtSetpoint() {
        return Math.abs(positionError) < positionTolerance
                && Math.abs(velocityError) < velocityTolerance;
    }

    /**
     * Enables continuous input.
     *
     * <p>Rather then using the max and min input range as constraints, it considers
     * them to be the same point and automatically calculates the shortest route
     * to the setpoint.
     *
     * @param minimumInput The minimum value expected from the input.
     * @param maximumInput The maximum value expected from the input.
     */
    public void enableContinuousInput(double minimumInput, double maximumInput) {
        continuous = true;
        setInputRange(minimumInput, maximumInput);
    }

    /**
     * Disables continuous input.
     */
    public void disableContinuousInput() {
        continuous = false;
    }

    /**
     * Sets the minimum and maximum values for the integrator.
     *
     * <p>When the cap is reached, the integrator value is added to the controller
     * output rather than the integrator value times the integral gain.
     *
     * @param minimumIntegral The minimum value of the integrator.
     * @param maximumIntegral The maximum value of the integrator.
     */
    public void setIntegratorRange(double minimumIntegral, double maximumIntegral) {
        this.minimumIntegral = minimumIntegral;
        this.maximumIntegral = maximumIntegral;
    }

    /**
     * Sets the error which is considered tolerable for use with atSetpoint().
     *
     * @param positionTolerance Position error which is tolerable.
     */
    public void setTolerance(double positionTolerance) {
        setTolerance(positionTolerance, Double.POSITIVE_INFINITY);
    }

    /**
     * Sets the error which is considered tolerable for use with atSetpoint().
     *
     * @param positionTolerance Position error which is tolerable.
     * @param velocityTolerance Velocity error which is tolerable.
     */
    public void setTolerance(double positionTolerance, double velocityTolerance) {
        this.positionTolerance = positionTolerance;
        this.velocityTolerance = velocityTolerance;
    }

    /**
     * Returns the difference between the setpoint and the measurement.
     *
     * @return The error.
     */
    public double getPositionError() {
        return getContinuousError(positionError);
    }

    /**
     * Returns the velocity error.
     */
    public double getVelocityError() {
        return velocityError;
    }

    /**
     * Returns the next output of the PID controller.
     *
     * @param measurement The current measurement of the process variable.
     * @param setpoint    The new setpoint of the controller.
     */
    public double calculate(double measurement, double setpoint) {
        // Set setpoint to provided value
        setSetpoint(setpoint);
        return calculate(measurement);
    }

    /**
     * Returns the next output of the PID controller.
     *
     * @param measurement The current measurement of the process variable.
     */
    public double calculate(double measurement) {
//        final double delta;
//        final double timeSeconds = clock.getTimeSeconds();
//        final Double lastTimeSeconds = this.lastTimeSeconds;
//        this.lastTimeSeconds = timeSeconds;
//        if(lastTimeSeconds == null){
//            delta = defaultPeriod;
//        } else {
//            double newDelta = timeSeconds - lastTimeSeconds;
//            delta = Math.min(newDelta, maxPeriod);
//        }
        final double timeSeconds = clock.getTimeSeconds();
        final Double lastTimeSeconds = this.lastTimeSeconds;
        this.lastTimeSeconds = timeSeconds;
        if(lastTimeSeconds == null){
            return 0;
        }
        double period = timeSeconds - lastTimeSeconds;
        previousError = positionError;
        positionError = getContinuousError(setpoint - measurement);
        velocityError = (positionError - previousError) / period;

        if (i != 0) {
            totalError = max(minimumIntegral / i, min(maximumIntegral / i, totalError + positionError * period));
        }

        return p * positionError + i * totalError + d * velocityError;
    }

    /**
     * Resets the previous error and the integral term.
     */
    public void reset() {
        previousError = 0;
        totalError = 0;
        lastTimeSeconds = null;
    }

    @NotNull
    @Override
    public ActiveComponent init(@NotNull String title, @NotNull BasicDashboard dashboard) {
        new SendableHelper(dashboard)
                .setActuator(true) // I don't think this does anything with how this is set up, but that's fine, we can't fix it here
                .setType("PIDController");
        return new ActiveComponentMultiplexer(title, Arrays.asList(
                new PropertyActiveComponent("", dashboard.get("p"), ValueProperty.createGetOnly(() -> BasicValue.makeDouble(getP()))),
                new PropertyActiveComponent("", dashboard.get("i"), ValueProperty.createGetOnly(() -> BasicValue.makeDouble(getI()))),
                new PropertyActiveComponent("", dashboard.get("d"), ValueProperty.createGetOnly(() -> BasicValue.makeDouble(getD())))
        ));
    }

    /**
     * Wraps error around for continuous inputs. The original error is returned if continuous mode is
     * disabled.
     *
     * @param error The current error of the PID controller.
     * @return Error for continuous inputs.
     */
    protected double getContinuousError(double error) {
        if (continuous && inputRange > 0) {
            error %= inputRange;
            if (Math.abs(error) > inputRange / 2) {
                if (error > 0) {
                    return error - inputRange;
                } else {
                    return error + inputRange;
                }
            }
        }
        return error;
    }

    /**
     * Sets the minimum and maximum values expected from the input.
     *
     * @param minimumInput The minimum value expected from the input.
     * @param maximumInput The maximum value expected from the input.
     */
    private void setInputRange(double minimumInput, double maximumInput) {
        this.minimumInput = minimumInput;
        this.maximumInput = maximumInput;
        inputRange = maximumInput - minimumInput;

        // Clamp setpoint to new input
        if (maximumInput > minimumInput) {
            setpoint = max(minimumInput, min(maximumInput, setpoint));
        }
    }
}
