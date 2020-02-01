package com.first1444.frc.robot2020.subsystems;

import com.first1444.sim.api.Rotation2;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;

public interface Turret extends Runnable {
    void setDesiredRotation(Rotation2 rotation);

    /**
     * @param speed The speed of rotation. Positive value rotates clockwise, negative value rotates counterclockwise
     */
    void setRawSpeed(double speed);

    Rotation2 MAX_ROTATION = Rotation2.DEG_90;
    Rotation2 MIN_ROTATION = Rotation2.DEG_270;

    class DesiredState {
        public static final DesiredState NEUTRAL = new DesiredState(null, 0.0);

        private final Rotation2 desiredRotation;
        private final Double rawSpeed;

        private DesiredState(Rotation2 desiredRotation, Double rawSpeed) {
            if(desiredRotation != null){
                if(desiredRotation.getRadians() > MAX_ROTATION.getRadians()){
                    this.desiredRotation = MAX_ROTATION;
                } else if(desiredRotation.getRadians() < MIN_ROTATION.getRadians()){
                    this.desiredRotation = MIN_ROTATION;
                } else {
                    this.desiredRotation = desiredRotation;
                }
            } else {
                this.desiredRotation = null;
            }
            this.rawSpeed = rawSpeed == null ? null : max(-1, min(1, rawSpeed));
        }
        public static DesiredState createDesiredRotation(Rotation2 rotation){
            return new DesiredState(requireNonNull(rotation), null);
        }
        public static DesiredState createRawSpeed(double speed){
            return new DesiredState(null, speed);
        }

        public Rotation2 getDesiredRotation() {
            return desiredRotation;
        }

        public Double getRawSpeed() {
            return rawSpeed;
        }

        @Override
        public String toString() {
            if(desiredRotation == null){
                return "DesiredState(rawSpeed=" + requireNonNull(rawSpeed) + ")";
            }
            return "DesiredState(desiredRotation=" + desiredRotation + ")";
        }
    }
}
