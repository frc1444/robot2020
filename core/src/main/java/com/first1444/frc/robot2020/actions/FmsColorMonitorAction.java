package com.first1444.frc.robot2020.actions;

import com.first1444.frc.robot2020.sound.SoundMap;
import com.first1444.sim.api.frc.FrcDriverStation;
import com.first1444.sim.api.frc.implementations.infiniterecharge.WheelColor;
import me.retrodaredevil.action.SimpleAction;

/**
 * This action notifies the driver station when data from the FMS is updated
 * <p>
 * This should be updated continuously
 */
public class FmsColorMonitorAction extends SimpleAction {
    private final FrcDriverStation driverStation;
    private final SoundMap soundMap;

    private WheelColor lastColor = null;
    public FmsColorMonitorAction(FrcDriverStation driverStation, SoundMap soundMap) {
        super(true);
        this.driverStation = driverStation;
        this.soundMap = soundMap;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        final WheelColor lastColor = this.lastColor;
        WheelColor color = WheelColor.parseColorOrNull(driverStation.getGameSpecificMessage());
        this.lastColor = color;
        if(color != lastColor){
            if(color == null){
                System.out.println("Color changed from " + lastColor + " to null!");
            } else {
                switch(color){
                    case BLUE:
                        soundMap.getColorWheelBlue().play();
                        break;
                    case GREEN:
                        soundMap.getColorWheelGreen().play();
                        break;
                    case RED:
                        soundMap.getColorWheelRed().play();
                        break;
                    case YELLOW:
                        soundMap.getColorWheelYellow().play();
                        break;
                }
            }
        }
    }
}
