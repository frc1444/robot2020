package com.first1444.frc.robot2020;

import com.first1444.dashboard.shuffleboard.PropertyComponent;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
import com.first1444.frc.robot2020.vision.VisionState;
import com.first1444.sim.api.Clock;
import edu.wpi.first.wpilibj.DigitalOutput;

public class WpiVisionState implements VisionState {
    private static final double DIM_TIME = 1.0;
    private final Clock clock;
    private final DigitalOutput pwm;

    private double maxBrightness = .75;
    private double dimStartTime = 0;
    private boolean enabled;

    public WpiVisionState(Clock clock, DashboardMap dashboardMap){
        this.clock = clock;
        pwm = new DigitalOutput(RobotConstants.DIO.VISION_LED);
        pwm.enablePWM(1.0);
        pwm.setPWMRate(500);

        dashboardMap.getDevTab().add("LED Brightness", new PropertyComponent(ValueProperty.create(() -> BasicValue.makeDouble(maxBrightness), (value) -> {
            maxBrightness = ((Number) value.getValue()).doubleValue();
        })));
    }

    @Override
    public void run() {
        if(enabled){
            pwm.updateDutyCycle(maxBrightness);
        } else {
            double elapsed = clock.getTimeSeconds() - dimStartTime;
            double percent = Math.max(0, 1 - elapsed / DIM_TIME);
            percent = Math.pow(percent, 2);
            pwm.updateDutyCycle(percent * maxBrightness);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if(this.enabled && !enabled){
            dimStartTime = clock.getTimeSeconds();
        }
        this.enabled = enabled;
    }
}
