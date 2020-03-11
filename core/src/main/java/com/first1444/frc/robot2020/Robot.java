package com.first1444.frc.robot2020;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.first1444.dashboard.BasicDashboard;
import com.first1444.dashboard.shuffleboard.ComponentMetadataHelper;
import com.first1444.dashboard.shuffleboard.PropertyComponent;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
import com.first1444.frc.robot2020.actions.*;
import com.first1444.frc.robot2020.actions.positioning.*;
import com.first1444.frc.robot2020.autonomous.AutonomousChooserState;
import com.first1444.frc.robot2020.autonomous.AutonomousModeCreator;
import com.first1444.frc.robot2020.autonomous.creator.AutonomousActionCreator;
import com.first1444.frc.robot2020.input.RobotInput;
import com.first1444.frc.robot2020.packets.transfer.*;
import com.first1444.frc.robot2020.perspective.FirstPersonPerspectiveOverride;
import com.first1444.frc.robot2020.perspective.PerspectiveHandler;
import com.first1444.frc.robot2020.perspective.PerspectiveProviderMultiplexer;
import com.first1444.frc.robot2020.sound.PacketSenderSoundCreator;
import com.first1444.frc.robot2020.sound.SoundMap;
import com.first1444.frc.robot2020.subsystems.*;
import com.first1444.frc.robot2020.subsystems.balltrack.BallTracker;
import com.first1444.frc.robot2020.subsystems.swerve.SwerveModuleEvent;
import com.first1444.frc.robot2020.vision.VisionProvider;
import com.first1444.frc.robot2020.vision.VisionState;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.Transform2;
import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.drivetrain.swerve.FourWheelSwerveDrive;
import com.first1444.sim.api.drivetrain.swerve.FourWheelSwerveDriveData;
import com.first1444.sim.api.drivetrain.swerve.SwerveDrive;
import com.first1444.sim.api.drivetrain.swerve.SwerveModule;
import com.first1444.sim.api.frc.AdvancedIterativeRobotAdapter;
import com.first1444.sim.api.frc.FrcDriverStation;
import com.first1444.sim.api.frc.FrcLogger;
import com.first1444.sim.api.frc.FrcMode;
import com.first1444.sim.api.frc.sim.DriverStationSendable;
import com.first1444.sim.api.scheduler.match.DefaultMatchScheduler;
import com.first1444.sim.api.scheduler.match.MatchSchedulerRunnable;
import com.first1444.sim.api.scheduler.match.MatchTime;
import com.first1444.sim.api.sensors.OrientationHandler;
import me.retrodaredevil.action.*;
import me.retrodaredevil.controller.ControlConfig;
import me.retrodaredevil.controller.MutableControlConfig;
import me.retrodaredevil.controller.PartUpdater;
import me.retrodaredevil.controller.output.ControllerRumble;
import me.retrodaredevil.controller.types.ExtremeFlightJoystickControllerInput;
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
    private final Odometry odometry;
    private final SwerveDrive drive;
    private final Intake intake;
    private final Turret turret;
    private final BallShooter ballShooter;
    private final WheelSpinner wheelSpinner;
    private final Climber climber;
    private final BallTracker ballTracker;
    private final VisionProvider visionProvider;
    private final VisionState visionState;

    private final PartUpdater partUpdater = new PartUpdater();
    private final RobotInput robotInput;

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
            StandardControllerInput controller, ExtremeFlightJoystickControllerInput extremeJoystick, LogitechAttack3JoystickControllerInput attackJoystick, ControllerRumble rumble,
            OrientationHandler rawOrientationHandler,
            FourWheelSwerveDriveData fourWheelSwerveData,
            Intake intake, Turret turret, BallShooter ballShooter, WheelSpinner wheelSpinner, Climber climber,
            BallTracker ballTracker,
            VisionProvider visionProvider,
            VisionState visionState
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
        this.ballTracker = ballTracker;
        this.visionProvider = visionProvider;
        this.visionState = visionState;
        robotInput = new RobotInput(
                controller,
                extremeJoystick,
                attackJoystick, rumble
        );
        partUpdater.addPartAssertNotPresent(robotInput);
        partUpdater.updateParts(controlConfig); // update this so when calling get methods don't throw exceptions
        dashboardMap.getUserTab().add("PS4 Connected", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeBoolean(robotInput.isControllerConnected()))), (metadata) -> new ComponentMetadataHelper(metadata).setPosition(3, 4).setSize(1, 1));
        dashboardMap.getUserTab().add("Extreme Connected", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeBoolean(robotInput.isExtremeConnected()))), (metadata) -> new ComponentMetadataHelper(metadata).setPosition(4, 4).setSize(1, 1));
        dashboardMap.getUserTab().add("Attack Connected", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeBoolean(robotInput.isAttackConnected()))), (metadata) -> new ComponentMetadataHelper(metadata).setPosition(5, 4).setSize(1, 1));
        dashboardMap.getUserTab().add("Ball Count", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeDouble(ballTracker.getBallCount()))), (metadata) -> new ComponentMetadataHelper(metadata).setSize(1, 1).setPosition(4, 2));
        drive = new FourWheelSwerveDrive(fourWheelSwerveData);

        {
            PacketQueue packetQueue = ZMQPacketQueue.create(new ObjectMapper(), 5808);
            packetSender = ZMQPacketSender.create(new ObjectMapper(), 5809);
            packetQueueCreator = new PacketQueueMaster(packetQueue, true);
        }
        soundMap = new SoundMap(new PacketSenderSoundCreator(packetSender, false));

        odometry = new Odometry(rawOrientationHandler, fourWheelSwerveData, robotInput, dashboardMap);
        matchScheduler = new DefaultMatchScheduler(driverStation, clock);

        PerspectiveHandler perspectiveHandler = new PerspectiveHandler(dashboardMap);
        SwerveDriveAction swerveDriveAction = new SwerveDriveAction(
                clock, drive, odometry.getAbsoluteAndVisionOrientation(), odometry.getAbsoluteAndVisionDistanceAccumulator(), robotInput,
                new PerspectiveProviderMultiplexer(Arrays.asList(
                        new FirstPersonPerspectiveOverride(robotInput),
                        perspectiveHandler
                ))
        );
        perspectiveHandler.setPerspectiveLocation(Constants.DRIVER_STATION_2_DRIVER_LOCATION);

        periodicAction = new Actions.ActionMultiplexerBuilder(
                new FmsColorMonitorAction(driverStation, soundMap),
                new SurroundingPositionCorrectAction(clock, dashboardMap, visionProvider, visionState, odometry.getAbsoluteAndVisionOrientation(), odometry.getAbsoluteAndVisionDistanceAccumulator()),
                new AbsolutePositionPacketAction(packetQueueCreator.create(), odometry.getAbsoluteAndVisionDistanceAccumulator()),
                new PerspectiveLocationPacketAction(packetQueueCreator.create(), perspectiveHandler),
                new OutOfBoundsPositionCorrectAction(odometry.getAutonomousMovementDistanceAccumulator()),
                new OutOfBoundsPositionCorrectAction(odometry.getAbsoluteAndVisionDistanceAccumulator()),
                new SurroundingDashboardLoggerAction(clock, visionProvider, dashboardMap), // maybe only update this every .1 seconds if we get around to it
                new VisionEnablerAction(clock, robotInput, visionState),
                new BallCountMonitorAction(ballTracker, soundMap)
        ).build();
        actionChooser = Actions.createActionChooser(WhenDone.CLEAR_ACTIVE);

        teleopAction = new Actions.ActionMultiplexerBuilder(
                swerveDriveAction,
                new OperatorAction(this, robotInput)
        ).canBeDone(false).canRecycle(true).build();
        dynamicAction = new Actions.ActionMultiplexerBuilder().canBeDone(true).canRecycle(true).build();

        autonomousChooserState = new AutonomousChooserState(clock, new AutonomousModeCreator(new AutonomousActionCreator(this)), dashboardMap);

        var driverStationSendable = new DriverStationSendable(driverStation);
        dashboardMap.getUserTab().add("FMS", new SendableComponent<>((title, dashboard) -> {
            dashboard.get(".type").getStrictSetter().setString("FMSInfo"); // temporary fix until we update robo-sim
            return driverStationSendable.init(title, dashboard);
        }), (metadata) -> {
            new ComponentMetadataHelper(metadata).setSize(3, 1).setPosition(2, 3);
        }); // 2,3

        System.out.println("Finished constructor 1");
    }

    @Override
    public void close() {
        System.out.println("Closing");
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
    }
    // endregion

    // region Overridden Methods
    @Override
    public void robotPeriodic() {
        partUpdater.updateParts(controlConfig); // handles updating controller logic
        odometry.run(); // we want to make sure our odometry is correct when we use it
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
        if(robotInput.getBallCountIncrement().isJustPressed()){
            ballTracker.addBall();
        }
        if(robotInput.getBallCountDecrement().isJustPressed()){
            ballTracker.removeBall();
        }

        // update subsystems
        drive.run();
        intake.run();
        turret.run();
        ballShooter.run();
        wheelSpinner.run();
        climber.run();
        visionState.run();

        matchScheduler.run();

        // Publish absolute position data to network tables
        BasicDashboard dashboard = dashboardMap.getRawBundle().getRootDashboard().getSubDashboard("Absolute Position");
        Vector2 position = odometry.getAbsoluteAndVisionDistanceAccumulator().getPosition();
        dashboard.get("x").getStrictSetter().setDouble(position.getX());
        dashboard.get("y").getStrictSetter().setDouble(position.getY());
        dashboard.get("orientationRadians").getStrictSetter().setDouble(odometry.getAbsoluteAndVisionOrientation().getOrientationRadians());

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
    public void disabledPeriodic() {
        /*
        While the robot is disabled, the "absolute only" position/orientation gets synced to the "absolute and vision" variant
         */
        odometry.getAutonomousMovementDistanceAccumulator().setPosition(odometry.getAbsoluteAndVisionDistanceAccumulator().getPosition());
        odometry.getAbsoluteOrientation().setOrientation(odometry.getAbsoluteAndVisionOrientation().getOrientation());
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
        System.out.println("Autonomous init! match time: " + driverStation.getMatchTime());
        /*
        We use the "absolute and vision" variant because we assume that vision will be disabled when auto starts, so we assume that the
        drive team will reset the position correctly while vision is not overriding the position/orientation because it's turned off
        */
        Transform2 startingTransform = new Transform2(odometry.getAbsoluteAndVisionDistanceAccumulator().getPosition(), odometry.getAbsoluteAndVisionOrientation().getOrientation());
        actionChooser.setNextAction(autonomousChooserState.createAutonomousAction(startingTransform));
        soundMap.getAutonomousEnable().play();
//        climber.storedPosition(); TODO not as simple as setting climber to stored position
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
    public DashboardMap getDashboardMap(){ return dashboardMap; }

    public SwerveDrive getDrive(){ return drive; }
    public Intake getIntake() { return intake; }
    public BallShooter getBallShooter(){ return ballShooter; }
    public BallTracker getBallTracker(){ return ballTracker; }
    public Turret getTurret(){ return turret; }
    public Climber getClimber(){ return climber; }
    public Odometry getOdometry(){ return odometry; }
    public VisionProvider getVisionProvider(){
        return visionProvider;
    }
    public VisionState getVisionState(){ return visionState; }

    public SoundMap getSoundMap(){ return soundMap; }

    public double getBestEstimatedTargetRpm(){
        return BallShooter.MAX_RPM; // Maybe in the future we will want to adjust this, but right now max rpm is fine
    }
}
