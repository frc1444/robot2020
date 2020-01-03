package com.first1444.frc.robot2020.input;

import me.retrodaredevil.controller.SimpleControllerPart;
import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.input.JoystickPart;
import me.retrodaredevil.controller.output.ControllerRumble;
import me.retrodaredevil.controller.output.DisconnectedRumble;
import me.retrodaredevil.controller.types.RumbleCapableController;
import me.retrodaredevil.controller.types.StandardControllerInput;

import static java.util.Objects.requireNonNull;

public class DefaultRobotInput extends SimpleControllerPart implements RobotInput {
    private final StandardControllerInput controller;
    private final ControllerRumble rumble;

    public DefaultRobotInput(StandardControllerInput controller, ControllerRumble rumble) {
        this.controller = requireNonNull(controller);
        if(rumble != null){
            partUpdater.addPartAssertNotPresent(rumble);
            this.rumble = rumble;
        } else {
            if (controller instanceof RumbleCapableController) {
                this.rumble = ((RumbleCapableController) controller).getRumble();
            } else {
                this.rumble = DisconnectedRumble.getInstance();
            }
        }
        partUpdater.addPartsAssertNonePresent(controller); // add the controllers as children
    }

    @Override
    public JoystickPart getMovementJoy() {
        return controller.getLeftJoy();
    }

    @Override
    public InputPart getTurnAmount() {
        return controller.getRightJoy().getXAxis();
    }

    @Override
    public InputPart getMovementSpeed() {
        return controller.getRightTrigger();
    }

    @Override
    public InputPart getVisionAlign() {
        return controller.getLeftTrigger();
    }

    @Override
    public InputPart getFirstPersonHoldButton() {
        return controller.getLeftBumper();
    }

    @Override
    public JoystickPart getResetGyroJoy() {
        return controller.getDPad();
    }

    @Override
    public InputPart getGyroReinitializeButton() {
        return controller.getFaceUp();
    }

    @Override
    public ControllerRumble getDriverRumble() {
        return rumble;
    }

    @Override
    public InputPart getSwerveQuickReverseCancel() {
        return controller.getSelect();
    }

    @Override
    public InputPart getSwerveRecalibrate() {
        return controller.getStart();
    }

    @Override
    public boolean isConnected() {
        return controller.isConnected();
    }
}
