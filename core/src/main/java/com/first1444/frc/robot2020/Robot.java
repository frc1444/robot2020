package com.first1444.frc.robot2020;

import com.first1444.dashboard.ActiveComponent;
import com.first1444.dashboard.ActiveComponentMultiplexer;
import com.first1444.dashboard.advanced.Sendable;
import com.first1444.dashboard.advanced.implementations.chooser.MutableMappedChooserProvider;
import com.first1444.dashboard.advanced.implementations.chooser.SimpleMappedChooserProvider;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
import com.first1444.dashboard.value.implementations.PropertyActiveComponent;
import com.first1444.frc.robot2020.actions.ColorWheelMonitorAction;
import com.first1444.frc.robot2020.actions.SwerveDriveAction;
import com.first1444.frc.robot2020.input.DefaultRobotInput;
import com.first1444.frc.robot2020.input.RobotInput;
import com.first1444.frc.robot2020.sound.DefaultSoundMap;
import com.first1444.frc.robot2020.sound.SoundMap;
import com.first1444.frc.robot2020.subsystems.OrientationSystem;
import com.first1444.frc.robot2020.subsystems.swerve.SwerveModuleEvent;
import com.first1444.frc.robot2020.vision.VisionPacketListener;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.distance.*;
import com.first1444.sim.api.drivetrain.swerve.FourWheelSwerveDrive;
import com.first1444.sim.api.drivetrain.swerve.FourWheelSwerveDriveData;
import com.first1444.sim.api.drivetrain.swerve.SwerveDrive;
import com.first1444.sim.api.drivetrain.swerve.SwerveModule;
import com.first1444.sim.api.frc.AdvancedIterativeRobotAdapter;
import com.first1444.sim.api.frc.FrcDriverStation;
import com.first1444.sim.api.frc.FrcLogger;
import com.first1444.sim.api.frc.FrcMode;
import com.first1444.sim.api.scheduler.match.DefaultMatchScheduler;
import com.first1444.sim.api.scheduler.match.MatchSchedulerRunnable;
import com.first1444.sim.api.scheduler.match.MatchTime;
import com.first1444.sim.api.sensors.Orientation;
import com.first1444.sim.api.sensors.OrientationHandler;
import com.first1444.sim.api.sound.SoundCreator;
import com.first1444.sim.api.surroundings.SurroundingProvider;
import me.retrodaredevil.action.*;
import me.retrodaredevil.controller.ControlConfig;
import me.retrodaredevil.controller.MutableControlConfig;
import me.retrodaredevil.controller.PartUpdater;
import me.retrodaredevil.controller.output.ControllerRumble;
import me.retrodaredevil.controller.types.StandardControllerInput;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;

public class Robot extends AdvancedIterativeRobotAdapter {
    private static final ControlConfig controlConfig;
    static {
        MutableControlConfig config = new MutableControlConfig();
        // *edit values of controlConfig if desired*
        config.switchToSquareInputThreshold = 1.2;
        config.fullAnalogDeadzone = .075;
        config.analogDeadzone = .02;
        config.cacheAngleAndMagnitudeInUpdate = false;
        config.useAbstractedIsDownIfPossible = false; // On PS4 controllers, this option is too sensitive
        controlConfig = config;
    }
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final FrcDriverStation driverStation; // We want to keep this as a field even though it isn't used yet
    private final FrcLogger logger;
    private final Clock clock;
    private final DashboardMap dashboardMap;
    private final OrientationSystem orientationSystem;
    private final SwerveDrive drive;

    private final PartUpdater partUpdater = new PartUpdater();
    private final RobotInput robotInput;
    /** The distance accumulator where the position will never jump. This should be updated using {@link DistanceAccumulator#run()} */
    private final DistanceAccumulator relativeDistanceAccumulator;
    /** The distance accumulator representing the absolute position of the robot. This may jump around as we correct the position over time. This does not have to be updated. */
    private final MutableDistanceAccumulator absoluteDistanceAccumulator;

    private final MatchSchedulerRunnable matchScheduler;

    private final VisionPacketListener visionPacketListener;
    private final SoundMap soundMap;

    private final Action periodicAction;

    /** The {@link ActionChooser} that handles an action that updates subsystems. (One action is active)*/
    private final ActionChooser actionChooser;

    /** The action that when updated, allows the driver and operator to control the robot */
    private final Action teleopAction;
    /** This is updated by teleopAction and should not be updated directly. This is stored as a field because we may need to change its perspective */
    private final SwerveDriveAction swerveDriveAction;

    // region Initialize
    /** Used to initialize final fields.*/
    public Robot(
            FrcDriverStation driverStation,
            FrcLogger logger,
            Clock clock,
            DashboardMap dashboardMap,
            StandardControllerInput controller, ControllerRumble rumble,
            SoundCreator soundCreator,
            OrientationHandler rawOrientationHandler,
            FourWheelSwerveDriveData fourWheelSwerveData,
            SurroundingProvider surroundingProvider
    ){
        this.driverStation = driverStation;
        this.logger = logger;
        this.clock = clock;
        this.dashboardMap = dashboardMap;

        robotInput = new DefaultRobotInput(
                controller,
                rumble
        );
        partUpdater.addPartAssertNotPresent(robotInput);
        partUpdater.updateParts(controlConfig); // update this so when calling get methods don't throw exceptions

        soundMap = new DefaultSoundMap(soundCreator);

        this.drive = new FourWheelSwerveDrive(fourWheelSwerveData);
        this.orientationSystem = new OrientationSystem(dashboardMap, rawOrientationHandler, robotInput);
        this.matchScheduler = new DefaultMatchScheduler(driverStation, clock);

        relativeDistanceAccumulator = new DeltaDistanceAccumulator(new OrientationDeltaDistanceCalculator(new SwerveDeltaDistanceCalculator(fourWheelSwerveData), getOrientation()));
        absoluteDistanceAccumulator = new DefaultMutableDistanceAccumulator(relativeDistanceAccumulator, false);
        dashboardMap.getUserTab().add("Position", new SendableComponent<>((Sendable<ActiveComponent>) (title, dashboard) -> new ActiveComponentMultiplexer(title,
                Arrays.asList(
                        new PropertyActiveComponent("", dashboard.get("x"), ValueProperty.createGetOnly(() -> BasicValue.makeDouble(absoluteDistanceAccumulator.getPosition().getX()))),
                        new PropertyActiveComponent("", dashboard.get("y"), ValueProperty.createGetOnly(() -> BasicValue.makeDouble(absoluteDistanceAccumulator.getPosition().getY())))
                )
        )));

        visionPacketListener = new VisionPacketListener(
                clock,
                Map.of(
                        0, Rotation2.ZERO,
                        1, Rotation2.DEG_180
                ),
                "10.14.44.5", 5801
        );

        periodicAction = new Actions.ActionMultiplexerBuilder(
            new ColorWheelMonitorAction(driverStation, soundMap)
        ).build();
        actionChooser = Actions.createActionChooser(WhenDone.CLEAR_ACTIVE);

        swerveDriveAction = new SwerveDriveAction(clock, drive, getOrientation(), robotInput, surroundingProvider);
        teleopAction = new Actions.ActionMultiplexerBuilder(
                swerveDriveAction
        ).canBeDone(false).canRecycle(true).build();

        visionPacketListener.start();
        System.out.println("Finished constructor");
    }

    @Override
    public void close() {
        visionPacketListener.interrupt();
        System.out.println("close() method called! Robot program must be ending!");
    }


    // endregion

    // region Overridden Methods

    @Override
    public void robotPeriodic() {
        partUpdater.updateParts(controlConfig); // handles updating controller logic
        periodicAction.update();
        actionChooser.update(); // update Actions that control the subsystems

        if(robotInput.getSwerveQuickReverseCancel().isJustPressed()){
            for(SwerveModule module : drive.getDrivetrainData().getModules()){
                module.getEventHandler().handleEvent(SwerveModuleEvent.QUICK_REVERSE_ENABLED, false);
            }
        } else if(robotInput.getSwerveQuickReverseCancel().isJustReleased()){
            for(SwerveModule module : drive.getDrivetrainData().getModules()){
                module.getEventHandler().handleEvent(SwerveModuleEvent.QUICK_REVERSE_ENABLED, true);
            }
        }
        if(robotInput.getSwerveRecalibrate().isJustPressed()){
            for(SwerveModule module : drive.getDrivetrainData().getModules()){
                module.getEventHandler().handleEvent(SwerveModuleEvent.RECALIBRATE, null);
            }
        }

        drive.run();
        relativeDistanceAccumulator.run();
        matchScheduler.run();
    }

    @Override
    public void disabledInit(@Nullable FrcMode previousMode) {
        dashboardMap.getLiveWindow().setEnabled(false);
        actionChooser.setToClearAction();
        if(previousMode == FrcMode.TELEOP){
            soundMap.getMatchEnd().play();
        } else {
            soundMap.getDisable().play();
        }
    }

    @Override
    public void teleopInit() {
        actionChooser.setNextAction(new Actions.ActionMultiplexerBuilder(
                teleopAction
        ).canRecycle(false).canBeDone(true).build());
        swerveDriveAction.setPerspective(Perspective.DRIVER_STATION);
        soundMap.getTeleopEnable().play();
        matchScheduler.schedule(new MatchTime(7, MatchTime.Mode.TELEOP, MatchTime.Type.FROM_END), () -> {
            System.out.println("rumbling");
            final var rumble = robotInput.getDriverRumble();
            if(rumble.isConnected()){
                rumble.rumbleTime(150, .6);
            }
        });
        System.out.println("Scheduled some stuff for end of teleop!");
    }

    @Override
    public void autonomousInit() {
        actionChooser.setNextAction(null);
        swerveDriveAction.setPerspective(Perspective.ROBOT_FORWARD_CAM);
        soundMap.getAutonomousEnable().play();
    }
    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void testInit() {
        dashboardMap.getLiveWindow().setEnabled(true);
        for(SwerveModule module : drive.getDrivetrainData().getModules()){
            module.getEventHandler().handleEvent(SwerveModuleEvent.DISABLE, null);
        }
    }
    // endregion

    public Clock getClock() { return clock; }
    public FrcLogger getLogger(){ return logger; }

    public SwerveDrive getDrive(){ return drive; }
    public Orientation getOrientation(){
        return orientationSystem.getOrientation();
    }
    public DistanceAccumulator getAbsoluteDistanceAccumulator(){
        return absoluteDistanceAccumulator;
    }

    public SoundMap getSoundMap(){ return soundMap; }

    public SurroundingProvider getSurroundingProvider() {
        throw new UnsupportedOperationException();
    }
}
