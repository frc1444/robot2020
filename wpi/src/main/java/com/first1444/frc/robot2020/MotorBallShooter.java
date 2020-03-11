package com.first1444.frc.robot2020;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.first1444.dashboard.shuffleboard.PropertyComponent;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.dashboard.value.BasicValue;
import com.first1444.dashboard.value.ValueProperty;
import com.first1444.frc.robot2020.subsystems.BallShooter;
import com.first1444.frc.robot2020.subsystems.balltrack.BallTracker;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.sendable.MutableValueMapSendable;
import com.first1444.sim.api.Clock;

import java.util.Arrays;

import static com.first1444.frc.robot2020.CtreUtil.nativeToRpm;
import static com.first1444.frc.robot2020.CtreUtil.rpmToNative;
import static java.lang.Math.abs;

public class MotorBallShooter implements BallShooter {
    private enum State {
        SPIN_UP,
        RECOVERY,
        READY
    }
    public static final double AT_SETPOINT_DEADBAND = 600;
    public static final double RECOVERY_DEADBAND = 800;
    private final BallTracker ballTracker;
    private final Clock clock;
    private final DashboardMap dashboardMap;
    private final TalonFX talon;
    private final SoundHandler soundHandler;

    private double rpm;
    private double currentRpm;

    private State state = State.SPIN_UP;

    public MotorBallShooter(BallTracker ballTracker, Clock clock, DashboardMap dashboardMap) {
        this.ballTracker = ballTracker;
        this.clock = clock;
        this.dashboardMap = dashboardMap;
        talon = new TalonFX(RobotConstants.CAN.SHOOTER);
        talon.configFactoryDefault(RobotConstants.INIT_TIMEOUT);
        talon.setInverted(InvertType.InvertMotorOutput);
        talon.setSensorPhase(true);
        talon.configPeakOutputReverse(0.0); // this can never go backwards
        talon.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed); // hack to make never go backwards
        talon.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, RobotConstants.PID_INDEX, RobotConstants.INIT_TIMEOUT);

        soundHandler = new SoundHandler(clock, dashboardMap, talon);

        var sendable = new MutableValueMapSendable<>(PidKey.class);
        var pidConfig = sendable.getMutableValueMap();
        pidConfig.setDouble(PidKey.CLOSED_RAMP_RATE, .25);
//        pidConfig.setDouble(PidKey.P, .2);
//        pidConfig.setDouble(PidKey.I, .00009);
        pidConfig.setDouble(PidKey.P, .54);
        pidConfig.setDouble(PidKey.F, .044);

        CtreUtil.applyPid(talon, pidConfig, RobotConstants.INIT_TIMEOUT);
        pidConfig.addListener(key -> CtreUtil.applyPid(talon, pidConfig, RobotConstants.LOOP_TIMEOUT));

        dashboardMap.getDebugTab().add("Shooter PID", new SendableComponent<>(sendable));

        dashboardMap.getDebugTab().add("Shooter Actual (Native)", new PropertyComponent(ValueProperty.createGetOnly(() -> BasicValue.makeDouble(talon.getSelectedSensorVelocity()))));
    }

    @Override
    public void setDesiredRpm(double rpm) {
        this.rpm = rpm;
    }

    @Override
    public void run() {
        final double rpm = this.rpm;
        if(rpm != 0){
            double velocity = rpmToNative(rpm, RobotConstants.FALCON_ENCODER_COUNTS_PER_REVOLUTION);
            talon.set(ControlMode.Velocity, velocity);
            dashboardMap.getDebugTab().getRawDashboard().get("Shooter Desired RPM").getStrictSetter().setDouble(rpm);
            dashboardMap.getDebugTab().getRawDashboard().get("Shooter Desired Velocity").getStrictSetter().setDouble(velocity);
            double rpmDifference = abs(currentRpm - rpm);
            if(rpmDifference < AT_SETPOINT_DEADBAND){
                state = State.READY;
            } else if(rpmDifference > RECOVERY_DEADBAND){
                if(state == State.READY){
                    ballTracker.onShootBall();
                    System.out.println("Ball shot");
                }
                state = State.RECOVERY;
            }
        } else {
            if (talon.getControlMode() == ControlMode.Velocity) {
                talon.set(ControlMode.PercentOutput, 0);
            }

            dashboardMap.getDebugTab().getRawDashboard().get("Shooter Desired RPM").getStrictSetter().setDouble(0.0);
            dashboardMap.getDebugTab().getRawDashboard().get("Shooter Desired Velocity").getStrictSetter().setDouble(0.0);
            state = State.SPIN_UP;

            soundHandler.update();
        }
        dashboardMap.getDebugTab().getRawDashboard().get("up to speed").getForceSetter().setString(state.toString());
        double currentRpm = nativeToRpm(talon.getSelectedSensorVelocity(), RobotConstants.FALCON_ENCODER_COUNTS_PER_REVOLUTION);
        this.currentRpm = currentRpm;
        dashboardMap.getDebugTab().getRawDashboard().get("Shooter Actual (RPM)").getStrictSetter().setDouble(currentRpm);
    }

    @Override
    public double getCurrentRpm() {
        return currentRpm;
    }

    @Override
    public boolean atSetpoint() {
        return state == State.READY;
    }
}
