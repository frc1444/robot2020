package com.first1444.frc.robot2020;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.ValueMap;

import java.util.Objects;
import java.util.function.*;

public final class CTREUtil {
    private CTREUtil() { throw new UnsupportedOperationException(); }
    public static void applyPID(BaseMotorController motor, ValueMap<PidKey> pid, int timeoutMs){
        applyPID(motor, pid, timeoutMs, CTREUtil::printGenericErrorMessage);
    }
    public static void printGenericErrorMessage(ErrorCode errorCode, Integer index){
        Objects.requireNonNull(errorCode);
        Objects.requireNonNull(index);
        if(errorCode != ErrorCode.OK) {
            System.err.println("Got error code: " + errorCode + " at index: " + index);
        }
    }

    public static void applyPID(BaseMotorController motor, ValueMap<PidKey> pid, int timeoutMs, BiConsumer<ErrorCode, Integer> errorCodeReport){
        motor.config_kP(Constants.SLOT_INDEX, pid.getDouble(PidKey.P), timeoutMs);
        motor.config_kI(Constants.SLOT_INDEX, pid.getDouble(PidKey.I), timeoutMs);
        motor.config_kD(Constants.SLOT_INDEX, pid.getDouble(PidKey.D), timeoutMs);
        motor.config_kF(Constants.SLOT_INDEX, pid.getDouble(PidKey.F), timeoutMs);
        motor.configClosedloopRamp(pid.getDouble(PidKey.CLOSED_RAMP_RATE), timeoutMs);
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
}
