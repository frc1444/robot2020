package com.first1444.frc.robot2020;

import edu.wpi.first.wpilibj.DigitalInput;

public class SensorArray {
    private final DigitalInput intakeSensor;
    private final DigitalInput transferSensor;
    private final DigitalInput feederSensor;
    public SensorArray(){
        intakeSensor = new DigitalInput(1);
        transferSensor = new DigitalInput(2);
        feederSensor = new DigitalInput(3);
    }
    public boolean isIntakeSensor(){ return !intakeSensor.get(); }
    public boolean isTransferSensor(){ return !transferSensor.get(); }
    public boolean isFeederSensor(){ return !feederSensor.get(); }
}
