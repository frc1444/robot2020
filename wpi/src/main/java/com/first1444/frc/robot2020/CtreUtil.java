package com.first1444.frc.robot2020;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.ValueMap;

import java.util.Objects;
import java.util.function.*;

public final class CtreUtil {
    private CtreUtil() { throw new UnsupportedOperationException(); }
    public static void applyPid(BaseMotorController motor, ValueMap<PidKey> pid, int timeoutMs){
        applyPid(motor, pid, timeoutMs, CtreUtil::printGenericErrorMessage);
    }
    public static void printGenericErrorMessage(ErrorCode errorCode, Integer index){
        Objects.requireNonNull(errorCode);
        Objects.requireNonNull(index);
        if(errorCode != ErrorCode.OK) {
            System.err.println("Got error code: " + errorCode + " at index: " + index);
        }
    }

    public static void applyPid(BaseMotorController motor, ValueMap<PidKey> pid, int timeoutMs, BiConsumer<ErrorCode, Integer> errorCodeReport){
        errorCodeReport.accept(motor.config_kP(RobotConstants.SLOT_INDEX, pid.getDouble(PidKey.P), timeoutMs), 0);
        errorCodeReport.accept(motor.config_kI(RobotConstants.SLOT_INDEX, pid.getDouble(PidKey.I), timeoutMs), 1);
        errorCodeReport.accept(motor.config_kD(RobotConstants.SLOT_INDEX, pid.getDouble(PidKey.D), timeoutMs), 2);
        errorCodeReport.accept(motor.config_kF(RobotConstants.SLOT_INDEX, pid.getDouble(PidKey.F), timeoutMs), 3);
        errorCodeReport.accept(motor.configMaxIntegralAccumulator(RobotConstants.SLOT_INDEX, pid.getDouble(PidKey.MAX_I), timeoutMs), 4);
        errorCodeReport.accept(motor.configClosedloopRamp(pid.getDouble(PidKey.CLOSED_RAMP_RATE), timeoutMs), 5);
    }
    @SafeVarargs
    public static void reportError(BiConsumer<ErrorCode, Integer> errorCodeReport, Supplier<ErrorCode>... errorCodeSuppliers){
        int i = 0;
        for(Supplier<ErrorCode> supplier : errorCodeSuppliers){
            final ErrorCode error = Objects.requireNonNull(supplier.get());
            errorCodeReport.accept(error, i);
            i++;
        }
    }
    public static double rpmToNative(double rpm, double countsPerRevolution){
        return rpm * countsPerRevolution / (double) RobotConstants.CTRE_UNIT_CONVERSION;
    }
    public static double nativeToRpm(double nativeUnits, double countsPerRevolution){
        return nativeUnits * RobotConstants.CTRE_UNIT_CONVERSION / countsPerRevolution;
    }
}
