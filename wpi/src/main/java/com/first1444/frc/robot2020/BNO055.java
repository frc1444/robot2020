package com.first1444.frc.robot2020;


import edu.wpi.first.wpilibj.I2C;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static java.util.Objects.requireNonNull;

/**
 * A class that Mike made in 2018 that Josh copied in 2019
 */
public class BNO055 implements AutoCloseable{
    private final I2C i2c;
    /** The current {@link Page} or null if invalid */
    private Page pageCache = null;
    /** The mode cache. Once set to a non-null value, this should never be set back to null, instead use {@link #modeCacheInvalid} = true */
    private IMUMode currentMode;
    private boolean modeCacheInvalid = true;

    /**
     * Construct a BNO055 IMU object and initialize.
     * @param addr Specify address of IMU (0x28 default or 0x29)
     */
    public BNO055(IMUMode startingMode, int addr) {
        i2c = new I2C(I2C.Port.kOnboard, addr);
        currentMode = startingMode;
        reinitialize();
    }

    public BNO055(IMUMode startingMode){
        this(startingMode, 0x28);
    }

    public BNO055() {
        this(IMUMode.NDOF);
    }
    public void clearCache(){
        pageCache = null;
        modeCacheInvalid = true;
    }
    public void reinitialize(){
        clearCache();
        setPage(Page.PAGE0);
        //Set units for m/s2, Deg/s, degrees, degC,
        //Pitch: -180 to 180 Clockwise+, Roll: -90 - 90, Heading/Yaw: 0-360 clockwise+
        i2c.write(REG_PAGE0.UNIT_SELECT, 0x0);
        IMUMode mode = currentMode;
        if(mode != null){
            setMode(mode);
        }
    }

    @Override
    public void close() {
        i2c.close();
    }

    private void setPage(Page page){
        requireNonNull(page);
        if(pageCache == page){
            return;
        }
        i2c.write(REG_PAGE0.PAGE_ID, page.value);
        pageCache = page;
    }

    public boolean isConnected(){
        return i2c.addressOnly();
    }

    /**
     * Check state of System Calibration
     * @return Returns true if Fully Calibrated
     */
    public boolean isCalibrated() {
        return getCalibrationData().sys;
    }
    public CalibrationData getCalibrationData(){

        byte[] result = {0};

        setPage(Page.PAGE0);
        i2c.read(REG_PAGE0.CALIB_STAT, 1, result);
        //This actually returns <SYS><GYR><ACC><MAG>
        //3=Calibrated, 0=not. We check SYS status.

        return new CalibrationData(result[0]);
    }

    /**
     * Set IMU operating mode
     * @param mode Operating mode for IMU.
     */
    public void setMode(IMUMode mode) {
        requireNonNull(mode);
        if(!modeCacheInvalid && currentMode == mode){
            return;
        }
        setPage(Page.PAGE0);
        i2c.write(REG_PAGE0.SYS_MODE, mode.val);
        currentMode = mode;
        modeCacheInvalid = false;
    }

    /**
     * Read and return latest Euler vector data from IMU.
     * All units are in Radians.<br>
     * Heading - Yaw/Heading value: 0 - 2PI Clockwise+.<br>
     * Roll - Roll Value: -PI/2 - PI/2 Clockwise+.<br>
     * Pitch - Pitch Value: -PI - PI.<br>
     * @return a EulerData class representing the 3 Euler angles
     */
    public EulerData getEulerData() {
        ByteBuffer result = ByteBuffer.allocateDirect(6);
        setPage(Page.PAGE0);
        i2c.read(REG_PAGE0.EUL_HEAD_LB, 6, result);
        result.order(ByteOrder.LITTLE_ENDIAN);

        return new EulerData(result);
    }


    // region Orientation Implementation
    // endregion

    /**
     * Object for holding Euler heading data.
     * heading - Yaw/Heading value: 0 - 360 Clockwise+.
     * Roll - Roll Value: -90 - 90 Clockwise+.
     * Pitch - Pitch Value: -180 - 180.
     */
    public static class EulerData {
        final double heading;
        final double roll;
        final double pitch;

        public EulerData(ByteBuffer data) {
            heading = (data.getShort(0) / 16.0);
            roll = data.getShort(2) / 16.0;
            pitch = data.getShort(4) / 16.0;
        }
    }
    public static class CalibrationData {
        private final boolean sys, gyro, accel, mag;

        private CalibrationData(byte result) {
            sys = ((result >> 6) & 3) != 0;
            gyro = ((result >> 4) & 3) != 0;
            accel = ((result >> 2) & 3) != 0;
            mag = (result & 3) != 0;
        }
        public boolean isSystem(){ return sys; }
        public boolean isGyro(){ return gyro; }
        public boolean isAccelerometer(){ return accel; }
        public boolean isMagnetometer(){ return mag; }
    }

    @SuppressWarnings("unused")
    public enum IMUMode {
        CONFIG(0),
        ACCONLY(1),
        MAG_ONLY(2),
        GYR_ONLY(3),
        ACC_MAG(4),
        ACC_GYR(5),
        MAG_GYR(6),
        A_M_G(7),
        IMU(8),
        COMPASS(9),
        M4G(10),
        NDOF_FMC_OFF(11),
        NDOF(12);

        private final byte val;

        IMUMode(int x){
            this.val = (byte)x;
        }
    }

    private enum Page {
        PAGE0(0), PAGE1(1);
        private final int value;

        Page(int value) {
            this.value = value;
        }
    }

    //Most config is on PAGE 1
    @SuppressWarnings("unused")
    private static class REG_PAGE1 {
        static final int PAGE_ID = 7;    //Write this to change PAGE (0,1)
        static final int ACC_SET = 8;    //Ah to have structs...
        static final int MAG_SET = 9;
        static final int GYR_SET0  = 10;
        static final int GYR_SET1  = 11;
        static final int ACC_SLEEP = 12;
        static final int GYR_SLEEP = 13;
        static final int RSVD14 = 14;
        static final int INT_MASK = 15;
        static final int INT_ENABLE = 16;
        static final int ACC_AM_THRESH = 17;
        static final int ACC_INT_SET = 18;
        static final int ACC_HG_DURATION  = 19;
        static final int ACC_HG_THRESH = 20;
        static final int ACC_NM_THRESH = 21;
        static final int ACC_NM_SET = 22;
        static final int GYR_INT_SET = 23;
        static final int GYR_HR_X_SET = 24;
        static final int GYR_DUR_X = 25;
        static final int GYR_HR_Y_SET = 26;
        static final int GYR_DUR_Y = 27;
        static final int GYR_HR_Z_SET = 28;
        static final int GYR_DUR_Z = 29;
        static final int GYR_AM_THRESH = 30;
        static final int GYR_AM_SET = 31;

        static final int UNIQUE_ID = 0x50;    //Either 0x50 or 0x50-0x5F. Datasheet ambiguous
    }

    //Most of the Data Output is on Page 0
    @SuppressWarnings("unused")
    private static class REG_PAGE0 {
        static final int CHIP_ID = 0;
        static final int ACC_ID = 1;
        static final int MAG_ID = 2;
        static final int GYR_ID = 3;
        static final int SW_REV_LB = 4;
        static final int SW_REV_HB = 5;
        static final int BL_REV = 6;
        static final int PAGE_ID = 7;
        static final int ACC_X_LB = 8;
        static final int ACC_X_HB = 9;
        static final int ACC_Y_LB = 10;
        static final int ACC_Y_HB = 11;
        static final int ACC_Z_LB = 12;
        static final int ACC_Z_HB = 13;
        static final int MAG_X_LB = 14;
        static final int MAG_X_HB = 15;
        static final int MAG_Y_LB = 16;
        static final int MAG_Y_HB = 17;
        static final int MAG_Z_LB = 18;
        static final int MAG_Z_HB = 19;
        static final int GYR_X_LB = 20;
        static final int GYR_X_HB = 21;
        static final int GYR_Y_LB = 22;
        static final int GYR_Y_HB = 23;
        static final int GYR_Z_LB = 24;
        static final int GYR_Z_HB = 25;
        static final int EUL_HEAD_LB = 26;
        static final int EUL_HEAD_HB = 27;
        static final int EUL_ROLL_LB = 28;
        static final int EUL_ROLL_HB = 29;
        static final int EUL_PITCH_LB = 30;
        static final int EUL_PITCH_HB = 31;
        static final int QUA_W_LB = 32;
        static final int QUA_W_HB = 33;
        static final int QUA_X_LB = 34;
        static final int QUA_X_HB = 35;
        static final int QUA_Y_LB = 36;
        static final int QUA_Y_HB = 37;
        static final int QUA_Z_LB = 38;
        static final int QUA_Z_HB = 39;
        static final int LIA_X_LB = 40;
        static final int LIA_X_HB = 41;
        static final int LIA_Y_LB = 42;
        static final int LIA_Y_HB = 43;
        static final int LIA_Z_LB = 44;
        static final int LIA_Z_HB = 45;
        static final int GRV_X_LB = 46;
        static final int GRV_X_HB = 47;
        static final int GRV_Y_LB = 48;
        static final int GRV_Y_HB = 49;
        static final int GRV_Z_LB = 50;
        static final int GRV_Z_HB = 51;
        static final int TEMPERATURE = 52;
        static final int CALIB_STAT = 53;
        static final int ST_RESULT = 54;
        static final int INT_STAT = 55;
        static final int CLK_STAT = 56;
        static final int SYS_STATUS = 57;
        static final int SYS_ERROR = 58;
        static final int UNIT_SELECT = 59;
        static final int RSVD60 = 60;
        static final int SYS_MODE = 61;
        static final int POWER_MODE = 62;
        static final int SYS_TRIGGER = 63;
        static final int TEMP_SOURCE = 64;
        static final int AXIS_CONFIG = 65;
        static final int AXIS_SIGN = 66;
        //Yeah there's more
        static final int ACC_X_OFF_LB = 85;
        static final int ACC_X_OFF_HB = 86;
        static final int ACC_Y_OFF_LB = 87;
        static final int ACC_Y_OFF_HB = 88;
        static final int ACC_Z_OFF_LB = 89;
        static final int ACC_Z_OFF_HB = 90;
        static final int MAG_X_OFF_LB = 91;
        static final int MAG_X_OFF_HB = 92;
        static final int MAG_Y_OFF_LB = 93;
        static final int MAG_Y_OFF_HB = 94;
        static final int MAG_Z_OFF_LB = 95;
        static final int MAG_Z_OFF_HB = 96;
        static final int GYR_X_OFF_LB = 97;
        static final int GYR_X_OFF_HB = 98;
        static final int GYR_Y_OFF_LB = 99;
        static final int GYR_Y_OFF_HB = 100;
        static final int GYR_Z_OFF_LB = 101;
        static final int GYR_Z_OFF_HB = 102;
        static final int ACC_RAD_LB = 103;
        static final int ACC_RAD_HB = 104;
        static final int MAG_RAD_LB = 105;
        static final int MAG_RAD_HB = 106;
    }

}
