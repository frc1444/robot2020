package com.first1444.frc.robot2020.input;

import me.retrodaredevil.controller.SimpleControllerPart;
import me.retrodaredevil.controller.input.AxisType;
import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.input.JoystickPart;
import me.retrodaredevil.controller.input.implementations.DummyInputPart;
import me.retrodaredevil.controller.output.ControllerRumble;
import me.retrodaredevil.controller.output.DisconnectedRumble;
import me.retrodaredevil.controller.types.RumbleCapableController;
import me.retrodaredevil.controller.types.StandardControllerInput;

import static java.util.Objects.requireNonNull;

public class RobotInput extends SimpleControllerPart {
    private final InputPart dummyInput = new DummyInputPart(AxisType.DIGITAL, 0.0);

    private final StandardControllerInput controller;
    private final ControllerRumble rumble;

    public RobotInput(StandardControllerInput controller, ControllerRumble rumble) {
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
        partUpdater.addPartsAssertNonePresent(
                controller,
                dummyInput
        ); // add the controllers as children
    }

    // region Swerve Controls
    /** @return A JoystickPart representing the direction to move*/
    public JoystickPart getMovementJoy() {
        return controller.getLeftJoy();
    }

    public InputPart getTurnAmount() {
        return controller.getRightJoy().getXAxis();
    }
    /** @return An InputPart that can have a range of [0..1] or [-1..1] representing the speed multiplier */
    public InputPart getMovementSpeed() {
        return controller.getRightTrigger();
    }
    public InputPart getVisionAlign() {
        return controller.getLeftTrigger();
    }
    public InputPart getFirstPersonHoldButton() {
        return controller.getLeftBumper();
    }
    // endregion
    // region Gyro Controls
    public JoystickPart getResetGyroJoy() {
        return controller.getDPad();
    }
    public InputPart getGyroReinitializeButton() {
        return controller.getFaceUp();
    }
    /** When this button is pressed, {@link #getMovementJoy()}'s angle should be used to reset the gyro */
    public InputPart getMovementJoyResetGyroButton() {
        return controller.getFaceLeft();
    }
    // endregion
    public ControllerRumble getDriverRumble() { return rumble; }
    // region Calibration
    public InputPart getSwerveQuickReverseCancel() {
        return controller.getSelect();
    }
    public InputPart getSwerveRecalibrate() {
        return controller.getStart();
    }
    // endregion

    // region Turret Controls
    public InputPart getTurretCenterOrient(){
        return controller.getFaceDown(); // temporary
    }
    public InputPart getTurretLeftOrient(){
        return controller.getFaceRight(); // temporary
    }
    public InputPart getTurretRightOrient(){
        return dummyInput;
    }
    // endregion

    // region Climb Controls
    /** If pressed, should make the climber go to the stored position*/
    public InputPart getClimbStored(){
        return dummyInput;
    }
    /** If pressed, should make the climber go to the starting position*/
    public InputPart getClimbStarting(){
        return dummyInput;
    }
    /** An input part that makes the climber move up and down manually.*/
    public InputPart getClimbRawControl(){
        return dummyInput;
    }
    // endregion


    @Override
    public boolean isConnected() {
        return controller.isConnected();
    }
}
