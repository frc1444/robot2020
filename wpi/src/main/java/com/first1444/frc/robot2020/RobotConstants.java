package com.first1444.frc.robot2020;

public final class RobotConstants {
    private RobotConstants(){ throw new UnsupportedOperationException(); }

    public static final int PID_INDEX = 0;
    public static final int SLOT_INDEX = 0;
    public static final int INIT_TIMEOUT = 10;
    /** Should be used for the timeout when calling a CTRE function that is rarely called, and is not during initialization time*/
    public static final int LOOP_TIMEOUT = 3;
    /** Should be used for the timeout when calling a CTRE function that is constantly called*/
    public static final int SYNCHRONOUS_TIMEOUT = 0;

    /** The number of encoder counts per revolution on a drive wheel on the swerve drive*/
    public static final int SWERVE_DRIVE_ENCODER_COUNTS_PER_REVOLUTION = 534;

    /** Conversion of CTRE units of 100 units/ms*/
    public static final int CTRE_UNIT_CONVERSION = 600;
    private static final int MAX_CIM_RPM = 5300;
    /** The maximum RPM of a drive wheel on the swerve drive*/
    public static final int MAX_SWERVE_DRIVE_RPM = MAX_CIM_RPM;
    /** Talon SRX counts every edge of the quadrature encoder, so 4 * 20 */
    public static final int CIMCODER_COUNTS_PER_REVOLUTION = 80;
}
