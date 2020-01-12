package com.first1444.frc.robot2020;

import com.first1444.frc.robot2020.subsystems.WheelSpinner;
import com.first1444.sim.api.frc.implementations.infiniterecharge.WheelColor;
import edu.wpi.first.wpilibj.I2C;

public class MotorWheelSpinner implements WheelSpinner {
    private final SimpleColorSensor colorSensor;

    public MotorWheelSpinner() {
        colorSensor = new SimpleColorSensor(I2C.Port.kOnboard);
    }

    @Override
    public void setSpeed(double speed) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WheelColor getCurrentColor() {
        return colorSensor.getWheelColor();
    }

    @Override
    public void setDesiredState(State state) {
        throw new UnsupportedOperationException();
    }

    @Override
    public State getDesiredState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public State getCurrentState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException();
    }
}
