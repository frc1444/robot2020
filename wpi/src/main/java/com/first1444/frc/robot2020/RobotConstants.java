package com.first1444.frc.robot2020;

public final class RobotConstants {
    private RobotConstants(){ throw new UnsupportedOperationException(); }

    public static final int PID_INDEX = 0;
    public static final int SLOT_INDEX = 0;
    public static final int INIT_TIMEOUT = 30;
    /** Should be used for the timeout when calling a CTRE function that is rarely called, and is not during initialization time*/
    public static final int LOOP_TIMEOUT = 3;
    /** Should be used for the timeout when calling a CTRE function that is constantly called*/
    public static final int SYNCHRONOUS_TIMEOUT = 0;


    /** Conversion of CTRE units of 100 units/ms*/
    public static final double CTRE_UNIT_CONVERSION = 600;
    public static final double MAX_CIM_RPM = 5300;
    /** Talon SRX counts every edge of the quadrature encoder, so 4 * 20 */
    public static final double CIMCODER_COUNTS_PER_REVOLUTION = 80;

    /** The number of encoder counts in a single revolution on a Falcon 500 using Talon FX*/
    public static final double FALCON_ENCODER_COUNTS_PER_REVOLUTION = 2048;

    /** The number of encoder counts per revolution on a drive wheel on the swerve drive*/
    public static final double CIM_SWERVE_DRIVE_ENCODER_COUNTS_PER_REVOLUTION = 534;
    public static final double FALCON_SWERVE_DRIVE_ENCODER_COUNTS_PER_REVOLUTION = 534.0 * FALCON_ENCODER_COUNTS_PER_REVOLUTION / CIMCODER_COUNTS_PER_REVOLUTION;

    /**
     * Contains constants for CAN Devices. Note swerve CAN IDs can be found in {@link Constants}
     */
    public static final class CAN {
        private CAN() { throw new UnsupportedOperationException(); }

        public static final int TURRET = 30;
        public static final int SHOOTER = 31;


        public static final int INTAKE = 32;
        public static final int INDEXER = 41;
        public static final int FEEDER = 42;

        public static final int CLIMBER = 43;
    }
    public static final class DIO {
        private DIO() { throw new UnsupportedOperationException(); }
        public static final int TURRET_ENCODER = 0;
        public static final int INTAKE_SENSOR = 1;
        public static final int TRANSFER_SENSOR = 2;
        public static final int FEEDER_SENSOR = 3;
        public static final int VISION_LED = 4;
        public static final int CLIMB_REVERSE_LIMIT_SWITCH_NORMALLY_OPEN = 5;
    }
}
