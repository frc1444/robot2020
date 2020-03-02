package com.first1444.frc.robot2020;

import com.first1444.dashboard.value.BasicValue;
import com.first1444.frc.robot2020.subsystems.swerve.ModuleConfig;
import com.first1444.frc.util.valuemap.MutableValueMap;
import com.first1444.sim.api.Vector2;

import java.text.DecimalFormat;
import java.util.Map;

import static com.first1444.sim.api.MeasureUtil.inchesToMeters;

public final class Constants {

    private Constants(){ throw new UnsupportedOperationException(); }

    public static final Map<String, BasicValue> ROBOT_PREFERENCES_PROPERTIES = Map.of("Show search box", BasicValue.FALSE);

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(" #0.00;-#0.00");

    private static final double PERSPECTIVE_BACK = 2.0;
    public static final Vector2 DRIVER_STATION_1_DRIVER_LOCATION = new Vector2(-3.667, -8.000 - PERSPECTIVE_BACK);
    public static final Vector2 DRIVER_STATION_2_DRIVER_LOCATION = new Vector2(-0.667, -8.500 - PERSPECTIVE_BACK);
    public static final Vector2 DRIVER_STATION_3_DRIVER_LOCATION = new Vector2(-3.042, -8.300 - PERSPECTIVE_BACK);

    public enum Swerve2018 implements SwerveSetup{
        INSTANCE;

        @Override
        public DriveType getDriveType() {
            return DriveType.CIM;
        }

        @Override public int getFRDriveCAN() { return 3; }
        @Override public int getFLDriveCAN() { return 4; }
        @Override public int getRLDriveCAN() { return 2; }
        @Override public int getRRDriveCAN() { return 1; }

        @Override public int getFRSteerCAN() { return 7; }
        @Override public int getFLSteerCAN() { return 8; }
        @Override public int getRLSteerCAN() { return 6; }
        @Override public int getRRSteerCAN() { return 5; }

        @Override
        public double getWheelBase() {
            return inchesToMeters(27.375);
        }

        @Override
        public double getTrackWidth() {
            return inchesToMeters(22.25);
        }

        @Override
        public double getQuadCountsPerRevolution() {
            return 1657;
        }

        @Override
        public MutableValueMap<ModuleConfig> setupFR(MutableValueMap<ModuleConfig> config) {
            return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 58)
                    .setDouble(ModuleConfig.MAX_ENCODER_VALUE, 891)
                    .setDouble(ModuleConfig.MIN_ENCODER_VALUE, 12);
        }

        @Override
        public MutableValueMap<ModuleConfig> setupFL(MutableValueMap<ModuleConfig> config) {
            throw new UnsupportedOperationException("We need to get the offset for this again!");
//            return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 516)
//                    .setDouble(ModuleConfig.MAX_ENCODER_VALUE, 899)
//                    .setDouble(ModuleConfig.MIN_ENCODER_VALUE, 10);
        }

        @Override
        public MutableValueMap<ModuleConfig> setupRL(MutableValueMap<ModuleConfig> config) {
            return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 122)
                    .setDouble(ModuleConfig.MAX_ENCODER_VALUE, 872)
                    .setDouble(ModuleConfig.MIN_ENCODER_VALUE, 13);
        }

        @Override
        public MutableValueMap<ModuleConfig> setupRR(MutableValueMap<ModuleConfig> config) {
            return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 824)
                    .setDouble(ModuleConfig.MAX_ENCODER_VALUE, 895)
                    .setDouble(ModuleConfig.MIN_ENCODER_VALUE, 9);
        }
    }
    public enum Swerve2019 implements SwerveSetup {
        INSTANCE;

        @Override
        public DriveType getDriveType() {
            return DriveType.CIM;
        }

        // Yup, we're using some of the same constants as last year, that doesn't mean everything will be the same, though!
        @Override public int getFRDriveCAN() { return 3; }
        @Override public int getFLDriveCAN() { return 4; }
        @Override public int getRLDriveCAN() { return 2; }
        @Override public int getRRDriveCAN() { return 1; }

        @Override public int getFRSteerCAN() { return 7; }
        @Override public int getFLSteerCAN() { return 8; }
        @Override public int getRLSteerCAN() { return 6; }
        @Override public int getRRSteerCAN() { return 5; }

        @Override
        public double getWheelBase() {
            return inchesToMeters(22.75);
        }

        @Override
        public double getTrackWidth() {
            return inchesToMeters(24.0);
        }

        @Override
        public double getQuadCountsPerRevolution() {
            return 628;
        }

        @Override
        public MutableValueMap<ModuleConfig> setupFR(MutableValueMap<ModuleConfig> config) {
            return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 74)
                    .setDouble(ModuleConfig.MAX_ENCODER_VALUE, 891)
                    .setDouble(ModuleConfig.MIN_ENCODER_VALUE, 10);
        }

        @Override
        public MutableValueMap<ModuleConfig> setupFL(MutableValueMap<ModuleConfig> config) {
            return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 131)
                    .setDouble(ModuleConfig.MAX_ENCODER_VALUE, 883)
                    .setDouble(ModuleConfig.MIN_ENCODER_VALUE, 10);
        }

        @Override
        public MutableValueMap<ModuleConfig> setupRL(MutableValueMap<ModuleConfig> config) {
            return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 600)
                    .setDouble(ModuleConfig.MAX_ENCODER_VALUE, 896)
                    .setDouble(ModuleConfig.MIN_ENCODER_VALUE, 10);
        }

        @Override
        public MutableValueMap<ModuleConfig> setupRR(MutableValueMap<ModuleConfig> config) {
            return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 55)
                    .setDouble(ModuleConfig.MAX_ENCODER_VALUE, 858)
                    .setDouble(ModuleConfig.MIN_ENCODER_VALUE, 10);
        }
    }
    public enum Swerve2020 implements SwerveSetup {
        INSTANCE;

        @Override
        public DriveType getDriveType() {
            return DriveType.FALCON;
        }

        @Override public int getFRDriveCAN() { return 21; }
        @Override public int getFLDriveCAN() { return 22; }
        @Override public int getRLDriveCAN() { return 23; }
        @Override public int getRRDriveCAN() { return 24; }

        @Override public int getFRSteerCAN() { return 25; }
        @Override public int getFLSteerCAN() { return 26; }
        @Override public int getRLSteerCAN() { return 27; }
        @Override public int getRRSteerCAN() { return 28; }

        @Override
        public double getWheelBase() {
            return inchesToMeters(24.0);
        }

        @Override
        public double getTrackWidth() {
            return inchesToMeters(22.75);
        }

        @Override
        public double getQuadCountsPerRevolution() {
//            return 628;
            return 1657;
        }

        @Override
        public MutableValueMap<ModuleConfig> setupFR(MutableValueMap<ModuleConfig> config) {
            return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 357);
        }
        @Override
        public MutableValueMap<ModuleConfig> setupFL(MutableValueMap<ModuleConfig> config) {
            return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 599);
        }
        @Override
        public MutableValueMap<ModuleConfig> setupRL(MutableValueMap<ModuleConfig> config) {
            return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 102);
        }
        @Override
        public MutableValueMap<ModuleConfig> setupRR(MutableValueMap<ModuleConfig> config) {
            return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 100);
        }
    }

}
