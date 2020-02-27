package com.first1444.frc.robot2020.subsystems;

/**
 * Represents the intake, indexer, and feeder
 */
public interface Intake extends Runnable {
    /**
     * This must be called continuously
     * @param speed A value from -1 to 1. Normally in range -1 to 0. Positive values suck the ball in
     */
    void setIntakeSpeed(double speed);

    /**
     * This must be called continuously
     * <p>
     * This delivers the ball to the feeder
     * @param speed The indexer speed. Positive values moves ball towards shooter
     */
    void setIndexerSpeed(double speed);
    /**
     * This must be called continuously
     * <p>
     * This feeds ball into the shooter
     * @param speed The feeder speed. Positive values deliver ball to shooter
     */
    void setFeederSpeed(double speed);

    void setControl(Control control);

    enum Control {
        MANUAL(false, false, false),
        /** Feeds balls into shooter. Runs indexer and feeder*/
        FEED_ALL(false, true, true),
        /** Feeds balls into shooter and intakes. Runs all*/
        FEED_ALL_AND_INTAKE(true, true, true),
        /** Stores balls as close to shooter as possible. Runs indexer and feeder*/
        STORE(false, true, true),
        /** Intakes balls. Runs intake and indexer*/
        INTAKE(true, true, false),
        /** Intakes balls and gets them as close to shooter as possible. Runs all */
        STORE_AND_INTAKE(true, true, true)
        ;
        private final boolean automaticIntake;
        private final boolean automaticIndexer;
        private final boolean automaticFeeder;

        Control(boolean automaticIntake, boolean automaticIndexer, boolean automaticFeeder) {
            this.automaticIntake = automaticIntake;
            this.automaticIndexer = automaticIndexer;
            this.automaticFeeder = automaticFeeder;
        }

        public double getDefaultIntakeSpeed(){
            return automaticIntake ? 1 : 0;
        }
        public double getDefaultIndexerSpeed(){
            return automaticIndexer ? 1 : 0;
        }
        public double getDefaultFeederSpeed(){
            return automaticFeeder ? 1 : 0;
        }
        public boolean isAutomaticIntake(){
            return automaticIntake;
        }
        public boolean isAutomaticIndexer(){
            return automaticIndexer;
        }
        public boolean isAutomaticFeeder() {
            return automaticFeeder;
        }
    }

}
