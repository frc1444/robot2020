package com.first1444.frc.robot2020;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.first1444.dashboard.ActiveComponent;
import com.first1444.dashboard.ActiveComponentMultiplexer;
import com.first1444.dashboard.BasicDashboard;
import com.first1444.dashboard.advanced.Sendable;
import com.first1444.dashboard.shuffleboard.ComponentMetadataHelper;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
import com.first1444.dashboard.value.implementations.PropertyActiveComponent;
import com.first1444.frc.robot2020.actions.ColorWheelMonitorAction;
import com.first1444.frc.robot2020.actions.OperatorAction;
import com.first1444.frc.robot2020.actions.SwerveDriveAction;
import com.first1444.frc.robot2020.actions.TimedAction;
import com.first1444.frc.robot2020.actions.positioning.*;
import com.first1444.frc.robot2020.autonomous.AutonomousChooserState;
import com.first1444.frc.robot2020.autonomous.AutonomousModeCreator;
import com.first1444.frc.robot2020.autonomous.creator.RobotAutonomousActionCreator;
import com.first1444.frc.robot2020.input.RobotInput;
import com.first1444.frc.robot2020.packets.transfer.*;
import com.first1444.frc.robot2020.perspective.FirstPersonPerspectiveOverride;
import com.first1444.frc.robot2020.perspective.PerspectiveHandler;
import com.first1444.frc.robot2020.perspective.PerspectiveProviderMultiplexer;
import com.first1444.frc.robot2020.sound.PacketSenderSoundCreator;
import com.first1444.frc.robot2020.sound.SoundMap;
import com.first1444.frc.robot2020.subsystems.*;
import com.first1444.frc.robot2020.subsystems.swerve.SwerveModuleEvent;
import com.first1444.frc.robot2020.vision.VisionProvider;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.Transform2;
import com.first1444.sim.api.Vector2;
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
import me.retrodaredevil.action.*;
import me.retrodaredevil.controller.ControlConfig;
import me.retrodaredevil.controller.MutableControlConfig;
import me.retrodaredevil.controller.PartUpdater;
import me.retrodaredevil.controller.output.ControllerRumble;
import me.retrodaredevil.controller.types.LogitechAttack3JoystickControllerInput;
import me.retrodaredevil.controller.types.StandardControllerInput;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

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
    private final PacketSender packetSender;
    private final PacketQueueCreator packetQueueCreator;
    private final OrientationSystem orientationSystem;
    private final SwerveDrive drive;
    private final Intake intake;
    private final Turret turret;
    private final BallShooter ballShooter;
    private final WheelSpinner wheelSpinner;
    private final Climber climber;

    private final PartUpdater partUpdater = new PartUpdater();
    private final RobotInput robotInput;
    /** The distance accumulator where the position will never jump. This should be updated using {@link DistanceAccumulator#run()} */
    private final DistanceAccumulator relativeDistanceAccumulator;
    /** The distance accumulator representing the absolute position of the robot. This may jump around as we correct the position over time. This does not have to be updated. */
    private final MutableDistanceAccumulator absoluteDistanceAccumulator;

    private final MatchSchedulerRunnable matchScheduler;

    private final SoundMap soundMap;

    /** Should be updated last in robotPeriodic. Usually updates actions that are used to monitor different things */
    private final Action periodicAction;
    /** The {@link ActionChooser} that handles an action that updates subsystems. (One action is active)*/
    private final ActionChooser actionChooser;
    /** The action that when updated, allows the driver and operator to control the robot */
    private final Action teleopAction;
    private final ActionMultiplexer dynamicAction;
    private final AutonomousChooserState autonomousChooserState;


    // region Initialize
    public Robot(
            FrcDriverStation driverStation,
            FrcLogger logger,
            Clock clock,
            DashboardMap dashboardMap,
            StandardControllerInput controller, LogitechAttack3JoystickControllerInput joystick, ControllerRumble rumble,
            OrientationHandler rawOrientationHandler,
            FourWheelSwerveDriveData fourWheelSwerveData,
            Intake intake, Turret turret, BallShooter ballShooter, WheelSpinner wheelSpinner, Climber climber,
            VisionProvider visionProvider
    ){
        this.driverStation = driverStation;
        this.logger = logger;
        this.clock = clock;
        this.dashboardMap = dashboardMap;
        this.intake = intake;
        this.turret = turret;
        this.ballShooter = ballShooter;
        this.wheelSpinner = wheelSpinner;
        this.climber = climber;
        robotInput = new RobotInput(
                controller,
                joystick, rumble
        );
        partUpdater.addPartAssertNotPresent(robotInput);
        partUpdater.updateParts(controlConfig); // update this so when calling get methods don't throw exceptions
        drive = new FourWheelSwerveDrive(fourWheelSwerveData);

        {
            PacketQueue packetQueue = ZMQPacketQueue.create(new ObjectMapper(), 5808);
            packetSender = ZMQPacketSender.create(new ObjectMapper(), 5809);
            packetQueueCreator = new PacketQueueMaster(packetQueue, true);
        }
        soundMap = new SoundMap(new PacketSenderSoundCreator(packetSender, false));

        this.orientationSystem = new OrientationSystem(dashboardMap, rawOrientationHandler, robotInput);
        this.matchScheduler = new DefaultMatchScheduler(driverStation, clock);

        relativeDistanceAccumulator = new DeltaDistanceAccumulator(new OrientationDeltaDistanceCalculator(new SwerveDeltaDistanceCalculator(fourWheelSwerveData), getOrientation()));
        absoluteDistanceAccumulator = new DefaultMutableDistanceAccumulator(relativeDistanceAccumulator, false);
        dashboardMap.getUserTab().add(
                "Position",
                new SendableComponent<>((Sendable<ActiveComponent>) (title, dashboard) -> new ActiveComponentMultiplexer(title,
                        Arrays.asList(
                                new PropertyActiveComponent("", dashboard.get("x"), ValueProperty.createGetOnly(() -> BasicValue.makeString(Constants.DECIMAL_FORMAT.format(absoluteDistanceAccumulator.getPosition().getX())))),
                                new PropertyActiveComponent("", dashboard.get("y"), ValueProperty.createGetOnly(() -> BasicValue.makeString(Constants.DECIMAL_FORMAT.format(absoluteDistanceAccumulator.getPosition().getY()))))
                        )
                )),
                metadata -> new ComponentMetadataHelper(metadata).setSize(2, 2).setPosition(4, 0)
        );

        PerspectiveHandler perspectiveHandler = new PerspectiveHandler(dashboardMap);
        SwerveDriveAction swerveDriveAction = new SwerveDriveAction(
                clock, drive, getOrientation(), absoluteDistanceAccumulator, robotInput,
                new PerspectiveProviderMultiplexer(Arrays.asList(
                        new FirstPersonPerspectiveOverride(robotInput),
                        perspectiveHandler
                ))
        );
        perspectiveHandler.setPerspectiveLocation(Constants.DRIVER_STATION_2_DRIVER_LOCATION);

        periodicAction = new Actions.ActionMultiplexerBuilder(
                new ColorWheelMonitorAction(driverStation, soundMap),
                new SurroundingPositionCorrectAction(clock, visionProvider, orientationSystem.getMutableOrientation(), absoluteDistanceAccumulator),
                new AbsolutePositionPacketAction(packetQueueCreator.create(), absoluteDistanceAccumulator),
                new PerspectiveLocationPacketAction(packetQueueCreator.create(), perspectiveHandler),
                new OutOfBoundsPositionCorrectAction(absoluteDistanceAccumulator),
                new SurroundingDashboardLoggerAction(clock, visionProvider, dashboardMap) // TODO only update this every .1 seconds
        ).build();
        actionChooser = Actions.createActionChooser(WhenDone.CLEAR_ACTIVE);

        teleopAction = new Actions.ActionMultiplexerBuilder(
                swerveDriveAction,
                new OperatorAction(this, robotInput)
        ).canBeDone(false).canRecycle(true).build();
        dynamicAction = new Actions.ActionMultiplexerBuilder().canBeDone(true).canRecycle(true).build();

        autonomousChooserState = new AutonomousChooserState(clock, new AutonomousModeCreator(new RobotAutonomousActionCreator(this)), dashboardMap);

        System.out.println("Finished constructor");
    }

    @Override
    public void close() {
        try {
            packetSender.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            packetQueueCreator.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("close() method called! Robot program must be ending!");
    }
    // endregion

    // region Overridden Methods
    @Override
    public void robotPeriodic() {
        partUpdater.updateParts(controlConfig); // handles updating controller logic
        orientationSystem.run(); // we want to make sure the orientation is correct when we use it
        relativeDistanceAccumulator.run(); // we want to make sure the absolute and relative positions are correct when we use them
        periodicAction.update(); // does stuff to orientation and absolute position
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

        // update subsystems
        drive.run();
        intake.run();
        turret.run();
        ballShooter.run();
        wheelSpinner.run();
        climber.run();

        matchScheduler.run();

        // Publish absolute position data to network tables
        BasicDashboard dashboard = dashboardMap.getRawBundle().getRootDashboard().getSubDashboard("Absolute Position");
        Vector2 position = absoluteDistanceAccumulator.getPosition();
        dashboard.get("x").getStrictSetter().setDouble(position.getX());
        dashboard.get("y").getStrictSetter().setDouble(position.getY());
        dashboard.get("orientationRadians").getStrictSetter().setDouble(getOrientation().getOrientationRadians());

        dynamicAction.update(); // unimportant stuff
    }

    @Override
    public void disabledInit(@Nullable FrcMode previousMode) {
        dashboardMap.getLiveWindow().setEnabled(false);
        actionChooser.setToClearAction();
        if(previousMode == FrcMode.TELEOP){
            soundMap.getTeleopDisable().play();
            dynamicAction.add(new Actions.ActionQueueBuilder(
                    new TimedAction(false, clock, 5.0),
                    Actions.createRunOnce(() -> {
                        soundMap.getPostMatchFiveSeconds().play();
                    })
            ).build());
        } else if(previousMode == FrcMode.AUTONOMOUS){
            soundMap.getAutonomousDisable().play();
        }
        for(SwerveModule module : drive.getDrivetrainData().getModules()){
            module.getEventHandler().handleEvent(SwerveModuleEvent.DISABLE, false);
        }
    }

    @Override
    public void teleopInit() {
        actionChooser.setNextAction(new Actions.ActionMultiplexerBuilder(
                teleopAction
        ).canRecycle(false).canBeDone(true).build());
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
        actionChooser.setNextAction(autonomousChooserState.createAutonomousAction(new Transform2(absoluteDistanceAccumulator.getPosition(), getOrientation().getOrientation())));
        soundMap.getAutonomousEnable().play();
        climber.storedPosition();
    }
    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void testInit() {
        dashboardMap.getLiveWindow().setEnabled(true);
        for(SwerveModule module : drive.getDrivetrainData().getModules()){
            module.getEventHandler().handleEvent(SwerveModuleEvent.DISABLE, true);
        }
    }
    // endregion

    public Clock getClock() { return clock; }
    public FrcLogger getLogger(){ return logger; }

    public SwerveDrive getDrive(){ return drive; }
    public Intake getIntake() {
        return intake;
    }
    public BallShooter getBallShooter(){ return ballShooter; }
    public Turret getTurret(){ return turret; }
    public Orientation getOrientation(){
        return orientationSystem.getOrientation();
    }
    public DistanceAccumulator getRelativeDistanceAccumulator(){
        return relativeDistanceAccumulator;
    }
    public DistanceAccumulator getAbsoluteDistanceAccumulator(){
        return absoluteDistanceAccumulator;
    }

    public SoundMap getSoundMap(){ return soundMap; }
}
