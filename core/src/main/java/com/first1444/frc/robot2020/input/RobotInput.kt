package com.first1444.frc.robot2020.input

import me.retrodaredevil.controller.SimpleControllerPart
import me.retrodaredevil.controller.input.AxisType
import me.retrodaredevil.controller.input.InputPart
import me.retrodaredevil.controller.input.JoystickPart
import me.retrodaredevil.controller.input.implementations.DummyInputPart
import me.retrodaredevil.controller.input.implementations.MultiplierInputPart
import me.retrodaredevil.controller.input.implementations.ScaledInputPart
import me.retrodaredevil.controller.output.ControllerRumble
import me.retrodaredevil.controller.output.DisconnectedRumble
import me.retrodaredevil.controller.types.ExtremeFlightJoystickControllerInput
import me.retrodaredevil.controller.types.LogitechAttack3JoystickControllerInput
import me.retrodaredevil.controller.types.RumbleCapableController
import me.retrodaredevil.controller.types.StandardControllerInput

/**
 * Contains properties that represent controls on our driver station.
 *
 * This is one of our few robot code classes written in Kotlin. This specific class was written in Kotlin
 * because of the significant boilerplate decrease by using Kotlin over Java.
 */
class RobotInput(
        private val controller: StandardControllerInput,
        private val extremeJoystick: ExtremeFlightJoystickControllerInput,
        private val attackJoystick: LogitechAttack3JoystickControllerInput,
        rumble: ControllerRumble?
) : SimpleControllerPart() {
    private val dummyInput: InputPart = DummyInputPart(AxisType.DIGITAL, 0.0)
    private val downDummyInput: InputPart = DummyInputPart(AxisType.DIGITAL, 1.0)

    val driverRumble: ControllerRumble = if (rumble != null) {
        partUpdater.addPartAssertNotPresent(rumble)
        rumble
    } else {
        if (controller is RumbleCapableController) {
            controller.rumble
        } else {
            DisconnectedRumble.getInstance()
        }
    }

    // region Swerve Controls
    /** @return A JoystickPart representing the direction to move */
    val movementJoy: JoystickPart = controller.leftJoy
    val turnAmount: InputPart = controller.rightJoy.xAxis
    /** @return An InputPart that can have a range of [0..1] or [-1..1] representing the speed multiplier */
    val movementSpeed: InputPart = controller.rightTrigger
    val visionAlign: InputPart = controller.leftTrigger
    val firstPersonHoldButton: InputPart = controller.leftBumper
    // endregion
    // region Gyro Controls
    val resetGyroJoy: JoystickPart = controller.dPad
    val gyroReinitializeButton: InputPart = controller.faceUp
    /** When this button is pressed, [movementJoy]'s angle should be used to reset the gyro  */
    val movementJoyResetGyroButton: InputPart = controller.faceLeft
    // endregion
    // region Swerve Recalibration
    val swerveQuickReverseCancel: InputPart = controller.select
    val swerveRecalibrate: InputPart = controller.start
    // endregion

    val intakeSpeed: InputPart = controller.rightStick // temp
    val indexerSpeed: InputPart = controller.rightStick // temp
    val feederSpeed: InputPart = controller.rightStick // temp
    val manualShootSpeed: InputPart

    // region Turret Controls
    val turretCenterOrient: InputPart = controller.faceDown
    @Deprecated("Going to remove")
    val turretLeftOrient: InputPart = controller.faceRight
    @Deprecated("Going to remove")
    val turretRightOrient: InputPart = dummyInput
    val turretRawControl: InputPart
    /** When pressed, this enables the turret to auto target using vision or absolute position. This also enables/disables vision*/
    val enableAutoAim: InputPart
    // endregion

    // region Climb Controls
    val climbStored: InputPart
        get() = attackJoystick.leftLower

    /** If pressed, should make the climber go to the starting position */
    val climbStarting: InputPart
        get() = attackJoystick.rightUpper

    /** An input part that makes the climber move up and down manually. */
    val climbRawControl: InputPart
    // endregion

    init {
        partUpdater.addPartsAssertNonePresent(
                controller,
                extremeJoystick,
                attackJoystick
        ) // add the controllers as children
        partUpdater.addPartsAssertNonePresent(dummyInput, downDummyInput) // these are placeholders

        turretRawControl = MultiplierInputPart(false, listOf(attackJoystick.thumbLower, attackJoystick.mainJoystick.xAxis), false)
        partUpdater.addPartAssertNotPresent(turretRawControl)

        val scaledShootSlider: InputPart = ScaledInputPart(AxisType.ANALOG, extremeJoystick.slider, false)
        manualShootSpeed = MultiplierInputPart(false, listOf(
                extremeJoystick.gridLowerLeft, // TODO temp
                scaledShootSlider
        ), false)
        partUpdater.addPartsAssertNonePresent(
                manualShootSpeed,
                scaledShootSlider
        )

        val scaledAutoAimSlider: InputPart = ScaledInputPart(AxisType.ANALOG, attackJoystick.slider, false)
        enableAutoAim = scaledShootSlider
        partUpdater.addPartAssertNotPresent(scaledAutoAimSlider)

        climbRawControl = MultiplierInputPart(false, listOf(
                attackJoystick.trigger,
                attackJoystick.mainJoystick.yAxis
        ), false)
        partUpdater.addPartAssertNotPresent(climbRawControl)
    }

    override fun isConnected() = controller.isConnected
}
