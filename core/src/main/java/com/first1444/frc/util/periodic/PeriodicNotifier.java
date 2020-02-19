package com.first1444.frc.util.periodic;

public interface PeriodicNotifier {

    enum Level {
        WARN,
        NORMAL,
        DEBUG
    }
    enum Frequency {
        HIGH,
        NORMAL,
        LOW
    }

    void addPeriodic(Level level, Frequency frequency, String toNotify);

    void addPersistent(Level level, Frequency frequency, String toNotify);
    void removePersistent(String toNotify);

    PeriodicNotifier NOTHING = new PeriodicNotifier() {
        @Override
        public void addPeriodic(Level level, Frequency frequency, String toNotify) { }
        @Override
        public void addPersistent(Level level, Frequency frequency, String toNotify) { }
        @Override
        public void removePersistent(String toNotify) { }
    };
}
