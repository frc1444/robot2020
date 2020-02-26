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

    val shooterOn: InputPart = extremeJoystick.thumbLeftUpper
    val shooterOff: InputPart = extremeJoystick.thumbLeftLower


    val intakeSpeed: InputPart = extremeJoystick.dPad.yAxis
    val shootButton: InputPart = extremeJoystick.trigger
    val manualShootSpeed: InputPart

    /** Should make the ball move towards the shooter*/
    val feederManualInButton: InputPart = extremeJoystick.gridUpperLeft
    val feederManualOutButton: InputPart = extremeJoystick.gridUpperRight
    val indexerManualInButton: InputPart = extremeJoystick.gridMiddleLeft
    val indexerManualOutButton: InputPart = extremeJoystick.gridMiddleRight
    val intakeManualInButton: InputPart = extremeJoystick.gridLowerLeft
    val intakeManualOutButton: InputPart = extremeJoystick.gridLowerRight

    // region Turret Controls
    val turretTrim: InputPart = extremeJoystick.mainJoystick.xAxis
    val turretCenterOrient: InputPart = attackJoystick.rightUpper
    val turretRawControl: InputPart
    /** When pressed, this enables the turret to auto target using vision or absolute position. */
    val enableAuto: InputPart
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

    // region Vision Controls
    val visionOn: InputPart
        get() = dummyInput
    val visionOff: InputPart
        get() = dummyInput
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


        manualShootSpeed = ScaledInputPart(AxisType.ANALOG, extremeJoystick.slider, false)
        partUpdater.addPartsAssertNonePresent(manualShootSpeed)

        enableAuto = ScaledInputPart(AxisType.ANALOG, attackJoystick.slider, false)
        partUpdater.addPartAssertNotPresent(enableAuto)

        climbRawControl = MultiplierInputPart(false, listOf(
                attackJoystick.trigger,
                attackJoystick.mainJoystick.yAxis
        ), false)
        partUpdater.addPartAssertNotPresent(climbRawControl)
    }

    override fun isConnected() = controller.isConnected

    val isControllerConnected: Boolean
        get() = controller.isConnected
    val isExtremeConnected: Boolean
        get() = extremeJoystick.isConnected
    val isAttackConnected: Boolean
        get() = attackJoystick.isConnected
}
