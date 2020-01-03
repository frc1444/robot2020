package com.first1444.frc.robot2020.input;

import me.retrodaredevil.controller.ControllerPart;
import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.input.JoystickPart;
import me.retrodaredevil.controller.output.ControllerRumble;

public interface RobotInput extends ControllerPart {

    /** @return A JoystickPart representing the direction to move*/
    JoystickPart getMovementJoy();

    InputPart getTurnAmount();

    /** @return An InputPart that can have a range of [0..1] or [-1..1] representing the speed multiplier */
    InputPart getMovementSpeed();

    InputPart getVisionAlign();
    InputPart getFirstPersonHoldButton();

    JoystickPart getResetGyroJoy();
    InputPart getGyroReinitializeButton();

    ControllerRumble getDriverRumble();

    InputPart getSwerveQuickReverseCancel();
    InputPart getSwerveRecalibrate();
}
