package com.first1444.frc.robot2020.actions;

import com.first1444.frc.robot2020.Perspective;
import com.first1444.frc.robot2020.input.RobotInput;
import com.first1444.frc.robot2020.setpoint.PIDController;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Transform2;
import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.distance.DistanceAccumulator;
import com.first1444.sim.api.drivetrain.swerve.SwerveDrive;
import com.first1444.sim.api.frc.implementations.infiniterecharge.Field2020;
import com.first1444.sim.api.sensors.Orientation;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.input.JoystickPart;
import me.retrodaredevil.controller.output.ControllerRumble;

import static com.first1444.sim.api.MathUtil.conservePow;
import static java.lang.Math.*;
import static java.util.Objects.requireNonNull;

/**
 * This swerve controls for teleop and should be ended when teleop is over. This can be recycled
 */
public class SwerveDriveAction extends SimpleAction {
    private final SwerveDrive drive;
    private final Orientation orientation;
    private final DistanceAccumulator absoluteDistanceAccumulator;
    private final RobotInput input;

    private final PIDController visionYawController;


    /** The perspective or null to automatically choose the perspective based on the task */
    private Perspective perspective = Perspective.DRIVER_STATION;

    public SwerveDriveAction(
            Clock clock,
            SwerveDrive drive,
            Orientation orientation, DistanceAccumulator absoluteDistanceAccumulator,
            RobotInput input
    ) {
        super(true);
        this.drive = requireNonNull(drive);
        this.orientation = requireNonNull(orientation);
        this.absoluteDistanceAccumulator = requireNonNull(absoluteDistanceAccumulator);
        this.input = requireNonNull(input);

        visionYawController = new PIDController(clock, 0.1, .01, 0);
        visionYawController.enableContinuousInput(0.0, 360.0);
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
        driverControl();
    }
    private double getVisionAlignPercent(){
        if(input.getVisionAlign().isDeadzone()){
            return 0;
        }
        return input.getVisionAlign().getPosition();
    }
    private double getDriverTurnAmount(){
        if(input.getTurnAmount().isDeadzone()){
            return 0;
        }
        return input.getTurnAmount().getPosition();
    }
    private double getTargetTurnAmount(){
        Vector2 position = absoluteDistanceAccumulator.getPosition();
        Rotation2 rotation = orientation.getOrientation();
        Rotation2 angle = Field2020.ALLIANCE_POWER_PORT.getTransform().getPosition().minus(position).getAngle();
//        System.out.println("Desired angle: " + angle + " current rotation: " + rotation);
        visionYawController.setSetpoint(angle.getDegrees());
        return -max(-1, min( 1, visionYawController.calculate(rotation.getDegrees())));
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


        double visionAlign = getVisionAlignPercent();
        if(visionAlign <= .3){
            visionYawController.reset();
        }
        double turnAmount = visionAlign * getTargetTurnAmount() + (1 - visionAlign) * getDriverTurnAmount();

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
