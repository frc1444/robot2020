package com.first1444.frc.robot2020.actions;

import com.first1444.frc.robot2020.Perspective;
import com.first1444.frc.robot2020.input.RobotInput;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.drivetrain.swerve.SwerveDrive;
import com.first1444.sim.api.sensors.Orientation;
import com.first1444.sim.api.surroundings.SurroundingProvider;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.input.JoystickPart;
import me.retrodaredevil.controller.output.ControllerRumble;

import static com.first1444.sim.api.MathUtil.conservePow;
import static java.util.Objects.requireNonNull;

/**
 * This swerve controls for teleop and should be ended when teleop is over. This can be recycled
 */
public class SwerveDriveAction extends SimpleAction {
    private final Clock clock; // we may use this in the future for vision
    private final SwerveDrive drive;
    private final Orientation orientation;
    private final RobotInput input;
    private final SurroundingProvider surroundingProvider; // we may use this in the future for vision


    /** The perspective or null to automatically choose the perspective based on the task */
    private Perspective perspective = Perspective.DRIVER_STATION;

    public SwerveDriveAction(
            Clock clock,
            SwerveDrive drive, Orientation orientation,
            RobotInput input,
            SurroundingProvider surroundingProvider
    ) {
        super(true);
        this.clock = requireNonNull(clock);
        this.drive = requireNonNull(drive);
        this.orientation = requireNonNull(orientation);
        this.input = requireNonNull(input);
        this.surroundingProvider = requireNonNull(surroundingProvider);
    }

    /**
     *
     * @param perspective The perspective or null to automatically choose the perspective based on the task
     */
    public void setPerspective(Perspective perspective){
        requireNonNull(perspective);
        this.perspective = perspective;
    }

    @Override
    protected void onStart() {
        super.onStart();
        final ControllerRumble rumble = input.getDriverRumble();
        if(rumble.isConnected()){
            rumble.rumbleTime(250, .4);
        }
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        // TODO use input.getVisionAlign() and execute something besides driverControl() if it is held down
        driverControl();
    }
    private void driverControl(){
        final Perspective perspective;
        if(input.getFirstPersonHoldButton().isDown()){
            perspective = Perspective.ROBOT_FORWARD_CAM;
        } else {
            perspective = this.perspective;
        }

        final JoystickPart joystick = input.getMovementJoy();
        final double x, y;
        if(joystick.isDeadzone()){
            x = 0;
            y = 0;
        } else {
            x = joystick.getCorrectX();
            y = joystick.getCorrectY();
        }

        final double turnAmount;
        if(input.getTurnAmount().isDeadzone()){
            turnAmount = 0;
        } else {
            turnAmount = input.getTurnAmount().getPosition();
        }

        final InputPart speedInputPart = input.getMovementSpeed();
        final double speed;
        if (speedInputPart.isDeadzone()) {
            speed = 0;
        } else {
            speed = conservePow(speedInputPart.getPosition(), 2);
        }
        final Vector2 translation;
        double offsetRadians = perspective.getOffsetRadians();
        double orientationRadians = orientation.getOrientationRadians();
        if(perspective.isUseGyro()){
            translation = new Vector2(x, y).rotateRadians(offsetRadians - orientationRadians);
        } else {
            //noinspection SuspiciousNameCombination
            translation = new Vector2(y, -x).rotateRadians(offsetRadians);
        }

//        System.out.println("x=" + translation.getX() + " y=" + translation.getY());
        drive.setControl(translation, turnAmount, speed);
    }

}
