package com.first1444.frc.robot2020.autonomous.creator;

import com.first1444.frc.robot2020.Robot;
import com.first1444.frc.robot2020.actions.FirstActionDone;
import com.first1444.frc.robot2020.actions.TimedAction;
import com.first1444.frc.robot2020.actions.TimedDoneEndAction;
import com.first1444.frc.robot2020.autonomous.actions.ShootAllRpmAction;
import com.first1444.frc.robot2020.autonomous.actions.TurretAlign;
import com.first1444.frc.robot2020.subsystems.Intake;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;

public class OperatorActionCreator {
    private final Robot robot;

    public OperatorActionCreator(Robot robot) {
        this.robot = robot;
    }

    public Action createIntakeRunForever() {
        Intake intake = robot.getIntake();
        return Actions.createRunForever(() -> {
            intake.setIntakeSpeed(1.0);
            intake.setIndexerSpeed(1.0);
        });
    }

    public Action createTurretAlign() {
        return new TimedDoneEndAction(
                true, robot.getClock(), .5,
                new TurretAlign(robot.getTurret(), robot.getOrientation(), robot.getAbsoluteDistanceAccumulator())
        );
    }

    public Action createTurretAlignAndShootAll() {
        Action turretAlign = createTurretAlign(); // can be recycled
        return new Actions.ActionQueueBuilder(
                turretAlign, // align turret
                Actions.createDynamicActionRunner(() -> {
                    double rpm = robot.getBestEstimatedTargetRpm();
                    return Actions.createSupplementaryAction(
                            FirstActionDone.create(
                                    new ShootAllRpmAction(robot.getIntake(), robot.getBallShooter(), robot.getBallTracker(), rpm),
                                    new Actions.ActionQueueBuilder(
                                            new TimedAction(false, robot.getClock(), 5.0),
                                            Actions.createRunOnce(() -> System.out.println("Stopping shoot all rpm action because 5 seconds has passed."))
                                    ).build()
                            ),
                            turretAlign // keep turret aligned while shooting
                    );
                })
        ).build();
    }

}
