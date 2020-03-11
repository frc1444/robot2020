package com.first1444.frc.robot2020.autonomous.creator;

import com.first1444.frc.robot2020.Robot;
import com.first1444.frc.robot2020.actions.FirstActionDone;
import com.first1444.frc.robot2020.actions.TimedAction;
import com.first1444.frc.robot2020.actions.TimedDoneEndAction;
import com.first1444.frc.robot2020.autonomous.actions.ShootAllRpmAction;
import com.first1444.frc.robot2020.autonomous.actions.TurretAlign;
import com.first1444.frc.robot2020.subsystems.Intake;
import com.first1444.frc.robot2020.vision.VisionInstant;
import me.retrodaredevil.action.*;

public class OperatorActionCreator {
    private final Robot robot;

    public OperatorActionCreator(Robot robot) {
        this.robot = robot;
    }

    public Action createRequireVision(double timeoutSeconds, Action successAction, Action failAction){
        return Actions.createLinkedActionRunner(new RequireVisionAction(timeoutSeconds, successAction, failAction), WhenDone.BE_DONE, false);
    }
    public Action createTurnOnVision(){
        return Actions.createRunOnce(() -> robot.getVisionState().setEnabled(true));
    }
    public Action createTurnOffVision(){
        return Actions.createRunOnce(() -> robot.getVisionState().setEnabled(false));
    }

    public Action createIntakeRunForever() {
        Intake intake = robot.getIntake();
        return Actions.createRunForeverRecyclable(() -> intake.setControl(Intake.Control.INTAKE));
    }

    public Action createTurretAlign() {
        return new TimedDoneEndAction(
                true, robot.getClock(), .5,
                new TurretAlign(robot.getTurret(), robot.getOdometry().getAbsoluteAndVisionOrientation(), robot.getOdometry().getAbsoluteAndVisionDistanceAccumulator())
        );
    }

    /**
     * @return An action that ends instantly, but sets the climb to store itself with a timeout of 5 seconds
     */
    public Action createStoreClimb() {
        return Actions.createRunOnce(() -> robot.getClimber().storedPosition(7.0));
    }
    public Action createRequireClimbStored(double timeoutSeconds, Action successAction, Action failAction) {
        return Actions.createLinkedActionRunner(new RequireClimbStoredAction(timeoutSeconds, successAction, failAction), WhenDone.BE_DONE, false);
    }
    public Action createRequireIntakeDownAction(double timeoutSeconds, Action successAction, Action failAction) {
        return Actions.createLinkedActionRunner(new RequireIntakeDownAction(timeoutSeconds, successAction, failAction), WhenDone.BE_DONE, false);
    }
    public Action createSetShooterRpm(double rpm){
        return Actions.createRunOnceRecyclable(() -> robot.getBallShooter().setDesiredRpm(rpm));
    }

    public Action createTurretAlignAndShootAll() {
        Action turretAlign = createTurretAlign(); // can be recycled
        return new Actions.ActionQueueBuilder(
                turretAlign, // align turret
                Actions.createDynamicActionRunner(() -> {
                    double rpm = robot.getBestEstimatedTargetRpm();
                    return Actions.createSupplementaryAction(
                            FirstActionDone.create(
                                    new TimedDoneEndAction( // keep running the shooter even if we think we've shot all of our balls
                                            false, robot.getClock(), 1.4,
                                            new ShootAllRpmAction(robot.getClock(), robot.getIntake(), robot.getBallShooter(), robot.getBallTracker(), rpm)
                                    ),
                                    new Actions.ActionQueueBuilder(
                                            new TimedAction(false, robot.getClock(), 5.0),
                                            Actions.createRunOnce(() -> {
                                                robot.getBallTracker().setBallCount(0);
                                                System.out.println("Stopping shoot all rpm action because 5 seconds has passed.");
                                            })
                                    ).build()
                            ),
                            turretAlign // keep turret aligned while shooting
                    );
                })
        ).build();
    }

    private class RequireVisionAction extends SimpleAction implements LinkedAction {
        private final Action successAction;
        private final Action failAction;
        private final Action timeoutAction;

        private Action nextAction;
        public RequireVisionAction(double timeoutSeconds, Action successAction, Action failAction) {
            super(true);
            this.successAction = successAction;
            this.failAction = failAction;
            timeoutAction = new TimedAction(true, robot.getClock(), timeoutSeconds);
        }

        @Override
        protected void onUpdate() {
            super.onUpdate();
            timeoutAction.update();
            VisionInstant instant = robot.getVisionProvider().getVisionInstant();
            if(robot.getVisionState().isEnabled() && instant != null && !instant.getSurroundings().isEmpty() && instant.getTimestamp() + 1.0 > robot.getClock().getTimeSeconds()) { // vision is recent (within 1 second
                nextAction = successAction;
                setDone(true);
            } else if(timeoutAction.isDone()){
                nextAction = failAction;
                setDone(true);
            }
        }

        @Override
        protected void onEnd(boolean peacefullyEnded) {
            super.onEnd(peacefullyEnded);
            timeoutAction.end();
        }

        @Override
        public Action getNextAction() {
            return nextAction;
        }
    }
    private class RequireClimbStoredAction extends SimpleAction implements LinkedAction {

        private final Action successAction;
        private final Action failAction;
        private final Action timeoutAction;

        private Action nextAction;
        public RequireClimbStoredAction(double timeoutSeconds, Action successAction, Action failAction) {
            super(true);
            this.successAction = successAction;
            this.failAction = failAction;
            timeoutAction = new TimedAction(true, robot.getClock(), timeoutSeconds);
        }

        @Override
        protected void onUpdate() {
            super.onUpdate();
            timeoutAction.update();
            if(robot.getClimber().isStored()) {
                nextAction = successAction;
                setDone(true);
            } else if(timeoutAction.isDone()){
                nextAction = failAction;
                setDone(true);
            }
        }

        @Override
        protected void onEnd(boolean peacefullyEnded) {
            super.onEnd(peacefullyEnded);
            timeoutAction.end();
        }

        @Override
        public Action getNextAction() {
            return nextAction;
        }
    }
    private class RequireIntakeDownAction extends SimpleAction implements LinkedAction {

        private final Action successAction;
        private final Action failAction;
        private final Action timeoutAction;

        private Action nextAction;
        public RequireIntakeDownAction(double timeoutSeconds, Action successAction, Action failAction) {
            super(true);
            this.successAction = successAction;
            this.failAction = failAction;
            timeoutAction = new TimedAction(true, robot.getClock(), timeoutSeconds);
        }

        @Override
        protected void onUpdate() {
            super.onUpdate();
            timeoutAction.update();
            if(robot.getClimber().isIntakeDown()) {
                nextAction = successAction;
                setDone(true);
            } else if(timeoutAction.isDone()){
                nextAction = failAction;
                setDone(true);
            }
        }

        @Override
        protected void onEnd(boolean peacefullyEnded) {
            super.onEnd(peacefullyEnded);
            timeoutAction.end();
        }

        @Override
        public Action getNextAction() {
            return nextAction;
        }
    }
}
