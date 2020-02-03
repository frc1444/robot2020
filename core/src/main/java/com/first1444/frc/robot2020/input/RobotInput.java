package com.first1444.frc.robot2020.input;

import me.retrodaredevil.controller.SimpleControllerPart;
import me.retrodaredevil.controller.input.AxisType;
import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.input.JoystickPart;
import me.retrodaredevil.controller.input.implementations.DummyInputPart;
import me.retrodaredevil.controller.input.implementations.LowestPositionInputPart;
import me.retrodaredevil.controller.input.implementations.ScaledInputPart;
import me.retrodaredevil.controller.input.implementations.SensitiveInputPart;
import me.retrodaredevil.controller.options.OptionValues;
import me.retrodaredevil.controller.output.ControllerRumble;
import me.retrodaredevil.controller.output.DisconnectedRumble;
import me.retrodaredevil.controller.types.LogitechAttack3JoystickControllerInput;
import me.retrodaredevil.controller.types.RumbleCapableController;
import me.retrodaredevil.controller.types.StandardControllerInput;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

public class RobotInput extends SimpleControllerPart {
    private final InputPart dummyInput = new DummyInputPart(AxisType.DIGITAL, 0.0);
    private final InputPart downDummyInput = new DummyInputPart(AxisType.DIGITAL, 1.0);

    private final StandardControllerInput controller;
    private final LogitechAttack3JoystickControllerInput joystick;
    private final ControllerRumble rumble;

    private final InputPart turretRawControl;
    private final InputPart manualShootSpeed;

    public RobotInput(StandardControllerInput controller, LogitechAttack3JoystickControllerInput joystick, ControllerRumble rumble) {
        this.controller = requireNonNull(controller);
        this.joystick = joystick;
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
        turretRawControl = new LowestPositionInputPart(false, Arrays.asList(joystick.getThumbLower(), joystick.getMainJoystick().getXAxis()), false);
        InputPart scaledSlider = new ScaledInputPart(AxisType.ANALOG, new SensitiveInputPart(
                joystick.getSlider(),
                OptionValues.createImmutableAnalogRangedOptionValue(1.0),
                OptionValues.createImmutableBooleanOptionValue(true), // TODO when we update abstract-controller-lib, we can remove this
                true
        ), true);
        manualShootSpeed = new LowestPositionInputPart(false, Arrays.asList(
                joystick.getCenterLeft(),
                scaledSlider
        ), false);
        partUpdater.addPartsAssertNonePresent(
                controller,
                joystick,
                turretRawControl,
                scaledSlider,
                manualShootSpeed,
                dummyInput, downDummyInput
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
        return dummyInput;
    }
    public InputPart getTurretLeftOrient(){
        return dummyInput;
    }
    public InputPart getTurretRightOrient(){
        return dummyInput;
    }
    public InputPart getTurretRawControl(){
        return turretRawControl;
    }
    // endregion

    /** When pressed, this enables the turret to auto target using vision or absolute position */
    public InputPart getEnableTurretAutoTarget(){
//        return downDummyInput;
        return dummyInput;
    }

    public InputPart getIntakeSpeed() {
        return controller.getRightStick();
    }
    public InputPart getManualShootSpeed() {
        return manualShootSpeed;
    }
    public InputPart getShootBall() {
        return dummyInput;
    }

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
