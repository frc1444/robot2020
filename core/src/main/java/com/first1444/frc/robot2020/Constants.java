package com.first1444.frc.robot2020;

import com.first1444.dashboard.value.BasicValue;
import com.first1444.frc.robot2020.subsystems.swerve.ModuleConfig;
import com.first1444.frc.util.valuemap.MutableValueMap;

import java.text.DecimalFormat;
import java.util.Map;

import static com.first1444.sim.api.MeasureUtil.inchesToMeters;

public final class Constants {

    private Constants(){ throw new UnsupportedOperationException(); }

    public static final Map<String, BasicValue> ROBOT_PREFERENCES_PROPERTIES = Map.of("Show search box", BasicValue.FALSE);

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(" #0.00;-#0.00");

    public static final int PID_INDEX = 0;
    public static final int SLOT_INDEX = 0;
    public static final int INIT_TIMEOUT = 10;
    public static final int LOOP_TIMEOUT = 3;

    /** The number of encoder counts per revolution on a drive wheel on the swerve drive*/
    public static final int SWERVE_DRIVE_ENCODER_COUNTS_PER_REVOLUTION = 534;

    // endregion

    /** Conversion of CTRE units of 100 units/ms*/
    public static final int CTRE_UNIT_CONVERSION = 600;
    private static final int MAX_CIM_RPM = 5300;
    /** The maximum RPM of a drive wheel on the swerve drive*/
    public static final int MAX_SWERVE_DRIVE_RPM = MAX_CIM_RPM;
    /** Talon SRX counts every edge of the quadrature encoder, so 4 * 20 */
    public static final int CIMCODER_COUNTS_PER_REVOLUTION = 80;

    public enum Swerve2019 implements SwerveSetup {
        INSTANCE;

        // Yup, we're using some of the same constants as last year, that doesn't mean everything will be the same, though!
        @Override public int getFLDriveCAN() { return 4; }
        @Override public int getFRDriveCAN() { return 3; }
        @Override public int getRLDriveCAN() { return 2; }
        @Override public int getRRDriveCAN() { return 1; }

        @Override public int getFLSteerCAN() { return 8; }
        @Override public int getFRSteerCAN() { return 7; }
        @Override public int getRLSteerCAN() { return 6; }
        @Override public int getRRSteerCAN() { return 5; }

        @Override
        public double getWheelBase() {
            return 22.75;
        }

        @Override
        public double getTrackWidth() {
            return 24;
        }

        @Override
        public int getQuadCountsPerRevolution() {
            return 628;
        }

        @Override
        public MutableValueMap<ModuleConfig> setupFL(MutableValueMap<ModuleConfig> config) {
            return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 131)
                    .setDouble(ModuleConfig.MAX_ENCODER_VALUE, 883)
                    .setDouble(ModuleConfig.MIN_ENCODER_VALUE, 10);
        }

        @Override
        public MutableValueMap<ModuleConfig> setupFR(MutableValueMap<ModuleConfig> config) {
            return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 113)
                    .setDouble(ModuleConfig.MAX_ENCODER_VALUE, 891)
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
            return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 869)
                    .setDouble(ModuleConfig.MAX_ENCODER_VALUE, 858)
                    .setDouble(ModuleConfig.MIN_ENCODER_VALUE, 10);
        }
    }

}
