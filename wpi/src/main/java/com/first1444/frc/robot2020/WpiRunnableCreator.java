package com.first1444.frc.robot2020;

import com.first1444.dashboard.BasicDashboard;
import com.first1444.dashboard.bundle.ActiveDashboardBundle;
import com.first1444.dashboard.bundle.DefaultDashboardBundle;
import com.first1444.dashboard.wpi.NetworkTableInstanceBasicDashboard;
import com.first1444.frc.robot2020.input.InputUtil;
import com.first1444.frc.robot2020.subsystems.swerve.ModuleConfig;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.MutableValueMap;
import com.first1444.frc.util.valuemap.sendable.MutableValueMapSendable;
import com.first1444.sim.api.RobotRunnable;
import com.first1444.sim.api.RobotRunnableMultiplexer;
import com.first1444.sim.api.RunnableCreator;
import com.first1444.sim.api.drivetrain.swerve.FourWheelSwerveDriveData;
import com.first1444.sim.api.frc.AdvancedIterativeRobotBasicRobot;
import com.first1444.sim.api.frc.BasicRobotRunnable;
import com.first1444.sim.api.frc.FrcDriverStation;
import com.first1444.sim.api.sound.implementations.DummySoundCreator;
import com.first1444.sim.wpi.WpiClock;
import com.first1444.sim.wpi.frc.DriverStationLogger;
import com.first1444.sim.wpi.frc.WpiFrcDriverStation;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import me.retrodaredevil.controller.output.DualShockRumble;
import me.retrodaredevil.controller.wpi.WpiInputCreator;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;

public class WpiRunnableCreator implements RunnableCreator {

    @Override
    public void prematureInit() {
    }

    @NotNull
    @Override
    public RobotRunnable createRunnable() {
        BasicDashboard rootDashboard = new NetworkTableInstanceBasicDashboard(NetworkTableInstance.getDefault());
        ActiveDashboardBundle bundle = new DefaultDashboardBundle(rootDashboard);
        DashboardMap dashboardMap = new DefaultDashboardMap(bundle);
        FrcDriverStation driverStation = new WpiFrcDriverStation(DriverStation.getInstance());

        final MutableValueMapSendable<PidKey> drivePidSendable = new MutableValueMapSendable<>(PidKey.class);
        final MutableValueMapSendable<PidKey> steerPidSendable = new MutableValueMapSendable<>(PidKey.class);
//        MetadataEditor robotPreferencesEditor = (metadata) -> new ComponentMetadataHelper(metadata).setProperties(Constants.ROBOT_PREFERENCES_PROPERTIES);
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
        final SwerveSetup swerve = Constants.Swerve2019.INSTANCE;
        final int quadCounts = swerve.getQuadCountsPerRevolution();
        FourWheelSwerveDriveData data = new FourWheelSwerveDriveData(
                new TalonSwerveModule("front right", swerve.getFRDriveCAN(), swerve.getFRSteerCAN(), quadCounts, drivePid, steerPid,
                        swerve.setupFR(createModuleConfig(dashboardMap, "front right module")), dashboardMap),

                new TalonSwerveModule("front left", swerve.getFLDriveCAN(), swerve.getFLSteerCAN(), quadCounts, drivePid, steerPid,
                        swerve.setupFL(createModuleConfig(dashboardMap, "front left module")), dashboardMap),

                new TalonSwerveModule("rear left", swerve.getRLDriveCAN(), swerve.getRLSteerCAN(), quadCounts, drivePid, steerPid,
                        swerve.setupRL(createModuleConfig(dashboardMap, "rear left module")), dashboardMap),

                new TalonSwerveModule("rear right", swerve.getRRDriveCAN(), swerve.getRRSteerCAN(), quadCounts, drivePid, steerPid,
                        swerve.setupRR(createModuleConfig(dashboardMap, "rear right module")), dashboardMap),
                swerve.getWheelBase(), swerve.getTrackWidth()
        );
        final BNO055 gyro = new BNO055();

        final Robot[] robotReference = {null};
        Robot robot = new Robot(
                driverStation, DriverStationLogger.INSTANCE, new WpiClock(), dashboardMap,
                InputUtil.createPS4Controller(new WpiInputCreator(0)), new DualShockRumble(new WpiInputCreator(5).createRumble()),
                DummySoundCreator.INSTANCE, // TODO sounds
                new BNOOrientationHandler(gyro),
                data,
                Collections::emptyList // TODO vision
        );
        robotReference[0] = robot;
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
