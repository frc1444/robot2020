package com.first1444.frc.robot2020;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.first1444.dashboard.BasicDashboard;
import com.first1444.dashboard.bundle.ActiveDashboardBundle;
import com.first1444.dashboard.bundle.DefaultDashboardBundle;
import com.first1444.dashboard.wpi.NetworkTableInstanceBasicDashboard;
import com.first1444.frc.robot2020.input.InputUtil;
import com.first1444.frc.robot2020.subsystems.implementations.DummyBallShooter;
import com.first1444.frc.robot2020.subsystems.implementations.DummyClimber;
import com.first1444.frc.robot2020.subsystems.implementations.DummyIntake;
import com.first1444.frc.robot2020.subsystems.implementations.DummyWheelSpinner;
import com.first1444.frc.robot2020.subsystems.swerve.DummySwerveModule;
import com.first1444.frc.robot2020.subsystems.swerve.ModuleConfig;
import com.first1444.frc.robot2020.vision.VisionPacketListener;
import com.first1444.frc.robot2020.vision.VisionPacketParser;
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
import com.first1444.sim.wpi.WpiClock;
import com.first1444.sim.wpi.frc.DriverStationLogger;
import com.first1444.sim.wpi.frc.WpiFrcDriverStation;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import me.retrodaredevil.controller.output.DualShockRumble;
import me.retrodaredevil.controller.wpi.WpiInputCreator;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

public class WpiRunnableCreator implements RunnableCreator {
    private static final boolean DUMMY_SWERVE = false;
    private static final SwerveSetup SWERVE = Constants.Swerve2019.INSTANCE;

    @Override
    public void prematureInit() {
    }

    @NotNull
    @Override
    public RobotRunnable createRunnable() {
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
        final BNO055 gyro = new BNO055();

        final Clock clock = new WpiClock();
        VisionPacketListener visionPacketListener = new VisionPacketListener(
                new VisionPacketParser(
                        new ObjectMapper(),
                        clock,
                        Map.of(1, Rotation2.ZERO)
                ),
                "tcp://10.14.44.5:5801"
        );
        visionPacketListener.start();
        Robot robot = new Robot(
                driverStation, DriverStationLogger.INSTANCE, clock, dashboardMap,
                InputUtil.createPS4Controller(new WpiInputCreator(0)), new DualShockRumble(new WpiInputCreator(5).createRumble()),
                new BNOOrientationHandler(gyro),
                data,
                new DummyIntake(reportMap), new DummyBallShooter(reportMap), new DummyWheelSpinner(reportMap), new DummyClimber(reportMap),
                visionPacketListener
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
