package com.first1444.frc.util.periodic;

import com.first1444.sim.api.Clock;
import com.first1444.sim.api.frc.FrcLogger;

public class SimplePeriodicNotifier implements PeriodicNotifier, Runnable {
    private final Clock clock;
    private final FrcLogger logger;

    public SimplePeriodicNotifier(Clock clock, FrcLogger logger) {
        this.clock = clock;
        this.logger = logger;
    }

    @Override
    public void run() {

    }

    @Override
    public void addPeriodic(Level level, Frequency frequency, String toNotify) {

    }

    @Override
    public void addPersistent(Level level, Frequency frequency, String toNotify) {

    }

    @Override
    public void removePersistent(String toNotify) {

    }
}
