package com.first1444.frc.robot2020.subsystems;

import com.first1444.sim.api.Rotation2;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;

public interface Turret extends Runnable {

    /**
     * @param trim The trim to use when the desired state is set to use desired rotation control
     */
    void setDesiredTrim(Rotation2 trim);

    /**
     * If this uses raw speed control, this must be set each iteration
     * @param desiredState The desired state to set.
     */
    void setDesiredState(DesiredState desiredState);

    Rotation2 getCurrentRotation();

    default boolean isClearToRaiseClimb(){
        Rotation2 rotation = getCurrentRotation();
        return rotation.getDegrees() > -25; // if we turn the turret more than 25 degrees clockwise, the falcon will hit the climb
    }

//    Rotation2 MAX_ROTATION = Rotation2.fromDegrees(35); use this when we add wheel spinner
    Rotation2 MAX_ROTATION = Rotation2.fromDegrees(80);
    Rotation2 MIN_ROTATION = Rotation2.fromDegrees(-80);
//    Rotation2 MIN_ROTATION = Rotation2.fromDegrees(-70);

    class DesiredState {
        public static final DesiredState NEUTRAL = new DesiredState(null, 0.0);

        private final Rotation2 desiredRotation;
        private final Double rawSpeedClockwise;

        private DesiredState(Rotation2 desiredRotation, Double rawSpeedClockwise) {
            if(desiredRotation != null){
                if(desiredRotation.getRadians() >= MAX_ROTATION.getRadians()){
                    this.desiredRotation = MAX_ROTATION;
                } else if(desiredRotation.getRadians() <= MIN_ROTATION.getRadians()){
                    this.desiredRotation = MIN_ROTATION;
                } else {
                    this.desiredRotation = desiredRotation;
                }
            } else {
                this.desiredRotation = null;
            }
            this.rawSpeedClockwise = rawSpeedClockwise == null ? null : max(-1, min(1, rawSpeedClockwise));
        }
        public static DesiredState createDesiredRotation(Rotation2 rotation){
            return new DesiredState(requireNonNull(rotation), null);
        }
        public static DesiredState createRawSpeedClockwise(double speed){
            return new DesiredState(null, speed);
        }
        public static DesiredState createRawSpeedCounterClockwise(double speed){
            return new DesiredState(null, -speed);
        }

        public Rotation2 getDesiredRotation() {
            return desiredRotation;
        }

        public Double getRawSpeedClockwise() {
            return rawSpeedClockwise;
        }
        public Double getRawSpeedCounterClockwise(){
            return -rawSpeedClockwise;
        }

        @Override
        public String toString() {
            if(desiredRotation == null){
                return "DesiredState(rawSpeed=" + requireNonNull(rawSpeedClockwise) + ")";
            }
            return "DesiredState(desiredRotation=" + desiredRotation + ")";
        }
    }
}
