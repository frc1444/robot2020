package com.first1444.frc.robot2020;

import com.first1444.sim.api.frc.implementations.infiniterecharge.WheelColor;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.ColorShim;

/**
 * Some credit to
 * <a href="https://github.com/FRCTeam225/Ri3D2020/blob/master/src/main/java/frc/robot/subsystems/ColorMatcher.java">FRCTeam255's Robot in 3 Days Code</a>
 */
public class ColorHelper {
    /*
    If we want to use multiple I2C devices: https://www.chiefdelphi.com/t/using-more-then-one-color-sensors-per-the-one-i2c-port-on-the-roborio/369584
     */
    private static final Color BLUE = new ColorShim(0.143, 0.427, 0.429);
    private static final Color GREEN = new ColorShim(0.197, 0.561, 0.240);
    private static final Color RED = new ColorShim(0.561, 0.232, 0.114);
    private static final Color YELLOW = new ColorShim(0.361, 0.524, 0.113);
    private static final ColorMatch colorMatch = new ColorMatch();
    static {
        colorMatch.addColorMatch(BLUE);
        colorMatch.addColorMatch(GREEN);
        colorMatch.addColorMatch(RED);
        colorMatch.addColorMatch(YELLOW);
    }

    private final ColorSensorV3 colorSensor;

    public ColorHelper(I2C.Port port) {
        colorSensor = new ColorSensorV3(port);
    }
    public ColorHelper(){
        this(I2C.Port.kOnboard);
    }

    private void doStuff(){
        Color detectedColor = colorSensor.getColor();
        ColorMatchResult result = colorMatch.matchClosestColor(detectedColor);
        Color color = result.color;
        final WheelColor wheelColor;
        if(color.equals(BLUE)){
            wheelColor = WheelColor.BLUE;
        } else if(color.equals(GREEN)){
            wheelColor = WheelColor.GREEN;
        } else if(color.equals(RED)){
            wheelColor = WheelColor.RED;
        } else if(color.equals(YELLOW)){
            wheelColor = WheelColor.YELLOW;
        } else {
            wheelColor = null;
        }
    }
}
