package com.first1444.frc.robot2020;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.first1444.dashboard.BasicDashboard;
import com.first1444.dashboard.bundle.ActiveDashboardBundle;
import com.first1444.dashboard.bundle.DefaultDashboardBundle;
import com.first1444.dashboard.wpi.NetworkTableInstanceBasicDashboard;
import com.first1444.frc.robot2020.subsystems.balltrack.BallTracker;
import com.first1444.frc.robot2020.subsystems.balltrack.SimpleBallTracker;
import com.first1444.frc.robot2020.subsystems.implementations.*;
import com.first1444.frc.robot2020.subsystems.swerve.DummySwerveModule;
import com.first1444.frc.robot2020.subsystems.swerve.ModuleConfig;
import com.first1444.frc.robot2020.vision.VisionPacketListener;
import com.first1444.frc.robot2020.vision.VisionPacketParser;
import com.first1444.frc.util.DummyOrientation;
import com.first1444.frc.util.SystemType;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.reportmap.DashboardReportMap;
import com.first1444.frc.util.reportmap.ReportMap;
import com.first1444.frc.util.valuemap.MutableValueMap;
import com.first1444.frc.util.valuemap.sendable.MutableValueMapSendable;
import com.first1444.sim.api.*;
import com.first1444.sim.api.drivetrain.swerve.FourWheelSwerveDriveData;
import com.first1444.sim.api.frc.AdvancedIterativeRobotBasicRobot;
import com.first1444.sim.api.frc.BasicRobotRunnable;
import com.first1444.sim.api.frc.FrcDriverStation;
import com.first1444.sim.api.sensors.DefaultOrientationHandler;
import com.first1444.sim.api.sensors.OrientationHandler;
import com.first1444.sim.wpi.WpiClock;
import com.first1444.sim.wpi.frc.DriverStationLogger;
import com.first1444.sim.wpi.frc.WpiFrcDriverStation;
import edu.wpi.first.hal.FRCNetComm;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.util.WPILibVersion;
import me.retrodaredevil.controller.implementations.InputUtil;
import me.retrodaredevil.controller.implementations.mappings.LinuxPS4StandardControllerInputCreator;
import me.retrodaredevil.controller.output.DualShockRumble;
import me.retrodaredevil.controller.types.StandardControllerInput;
import me.retrodaredevil.controller.wpi.WpiInputCreator;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

public class WpiRunnableCreator implements RunnableCreator {
    private static final boolean DUMMY_SWERVE = true;
    private static final boolean DUMMY_GYRO = DUMMY_SWERVE;
    private static final SwerveSetup SWERVE = Constants.Swerve2019.INSTANCE;

    @Override
    public void prematureInit() {
    }

    @NotNull
    @Override
    public RobotRunnable createRunnable() {
        HAL.report(FRCNetComm.tResourceType.kResourceType_Language, FRCNetComm.tInstances.kLanguage_Kotlin, 0, WPILibVersion.Version); // All of robo-sim is in Kotlin and this uses Kotlin code in some places.
        HAL.report(FRCNetComm.tResourceType.kResourceType_Framework, FRCNetComm.tInstances.kFramework_Timed);

        BasicDashboard rootDashboard = new NetworkTableInstanceBasicDashboard(NetworkTableInstance.getDefault());
        ActiveDashboardBundle bundle = new DefaultDashboardBundle(rootDashboard);
        DashboardMap dashboardMap = new DefaultDashboardMap(bundle);
        ReportMap reportMap = new DashboardReportMap(dashboardMap.getDebugTab().getRawDashboard().getSubDashboard("Report Map"));
        FrcDriverStation driverStation = new WpiFrcDriverStation(DriverStation.getInstance());

        final MutableValueMapSendable<PidKey> drivePidSendable = new MutableValueMapSendable<>(PidKey.class);
        final MutableValueMapSendable<PidKey> steerPidSendable = new MutableValueMapSendable<>(PidKey.class);
        dashboardMap.getLiveWindow().add("Drive PID", drivePidSendable);
        dashboardMap.getLiveWindow().add("Steer PID", steerPidSendable);
        dashboardMap.getLiveWindow().setEnabled(true);

        final MutableValueMap<PidKey> drivePid = drivePidSendable.getMutableValueMap();
        final MutableValueMap<PidKey> steerPid = steerPidSendable.getMutableValueMap();
        drivePid
                .setDouble(PidKey.P, 1.5)
                .setDouble(PidKey.F, 1.0)
                .setDouble(PidKey.CLOSED_RAMP_RATE, .25);
        steerPid
                .setDouble(PidKey.P, 12)
                .setDouble(PidKey.I, .03);
        final FourWheelSwerveDriveData data;
        if(DUMMY_SWERVE){
            data = new FourWheelSwerveDriveData(
                    new DummySwerveModule(), new DummySwerveModule(), new DummySwerveModule(), new DummySwerveModule(),
                    SWERVE.getWheelBase(), SWERVE.getTrackWidth()
            );
        } else {
            final int quadCounts = SWERVE.getQuadCountsPerRevolution();
            data = new FourWheelSwerveDriveData(
                    new TalonSwerveModule("front right", SWERVE.getFRDriveCAN(), SWERVE.getFRSteerCAN(), quadCounts, drivePid, steerPid,
                            SWERVE.setupFR(createModuleConfig(dashboardMap, "front right module")), dashboardMap),

                    new TalonSwerveModule("front left", SWERVE.getFLDriveCAN(), SWERVE.getFLSteerCAN(), quadCounts, drivePid, steerPid,
                            SWERVE.setupFL(createModuleConfig(dashboardMap, "front left module")), dashboardMap),

                    new TalonSwerveModule("rear left", SWERVE.getRLDriveCAN(), SWERVE.getRLSteerCAN(), quadCounts, drivePid, steerPid,
                            SWERVE.setupRL(createModuleConfig(dashboardMap, "rear left module")), dashboardMap),

                    new TalonSwerveModule("rear right", SWERVE.getRRDriveCAN(), SWERVE.getRRSteerCAN(), quadCounts, drivePid, steerPid,
                            SWERVE.setupRR(createModuleConfig(dashboardMap, "rear right module")), dashboardMap),
                    SWERVE.getWheelBase(), SWERVE.getTrackWidth()
            );
        }
        final Clock clock = new WpiClock();
        VisionPacketListener visionPacketListener = new VisionPacketListener(
                clock,
                new VisionPacketParser(
                        new ObjectMapper(),
                        Map.of(1, Rotation2.ZERO)
                ),
                "tcp://10.14.44.5:5801"
        );
        visionPacketListener.start();
        BallTracker ballTracker = new SimpleBallTracker();

        final StandardControllerInput controller;
        if(RobotBase.isSimulation() && SystemType.isUnixBased()){
            controller = InputUtil.createController(new WpiInputCreator(0), new LinuxPS4StandardControllerInputCreator());
        } else {
            controller = InputUtil.createPS4Controller(new WpiInputCreator(0));
        }
        final OrientationHandler orientationHandler;
        if(DUMMY_GYRO){
            orientationHandler = new DefaultOrientationHandler(new DummyOrientation(Rotation2.ZERO));
        } else {
            orientationHandler = new BNOOrientationHandler(new BNO055());
        }
        Robot robot = new Robot(
                driverStation, DriverStationLogger.INSTANCE, clock, dashboardMap,
                controller, InputUtil.createAttackJoystick(new WpiInputCreator(2)), new WpiInputCreator(3), new DualShockRumble(new WpiInputCreator(5).createRumble(), .5, .6, true),
                orientationHandler,
                data,
                new DummyIntake(reportMap),
//                new DummyTurret(reportMap),
                new MotorTurret(dashboardMap),
//                new MotorBallShooter(ballTracker, dashboardMap),
                new DummyBallShooter(reportMap),
                new DummyWheelSpinner(reportMap), new DummyClimber(reportMap),
                ballTracker,
                visionPacketListener
//                VisionProvider.NOTHING
        );
        return new RobotRunnableMultiplexer(Arrays.asList(
                new BasicRobotRunnable(new AdvancedIterativeRobotBasicRobot(robot), driverStation),
                new RobotRunnable() {
                    @Override
                    public void run() {
                        bundle.update();
                    }

                    @Override
                    public void close() {
                        bundle.onRemove();
                        visionPacketListener.close();
                    }
                }
        ));
    }
    private MutableValueMap<ModuleConfig> createModuleConfig(DashboardMap dashboardMap, String name){
        final MutableValueMapSendable<ModuleConfig> config = new MutableValueMapSendable<>(ModuleConfig.class);
        dashboardMap.getLiveWindow().add(name, config);
        return config.getMutableValueMap();
    }

}
