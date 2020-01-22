package com.first1444.frc.robot2020.autonomous;

import com.first1444.dashboard.shuffleboard.ComponentMetadataHelper;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.dashboard.shuffleboard.ShuffleboardContainer;
import com.first1444.dashboard.shuffleboard.implementations.ShuffleboardLayoutComponent;
import com.first1444.frc.robot2020.Constants;
import com.first1444.frc.robot2020.DashboardMap;
import com.first1444.frc.robot2020.autonomous.actions.AutonomousInputWaitAction;
import com.first1444.frc.util.valuemap.ValueMap;
import com.first1444.frc.util.valuemap.sendable.MutableValueMapSendable;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.Transform2;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;

import java.io.PrintWriter;

public class AutonomousCreator {
    private final Clock clock;
    private final AutonomousModeCreator autonomousModeCreator;

    private final ValueMap<AutonomousConfigKey> autonomousConfigKeyValueMap;

    public AutonomousCreator(
            Clock clock, AutonomousModeCreator autonomousModeCreator,
            DashboardMap dashboardMap
    ) {
        this.clock = clock;
        this.autonomousModeCreator = autonomousModeCreator;

        final ShuffleboardContainer layout = dashboardMap.getUserTab()
                .add("Autonomous", ShuffleboardLayoutComponent.LIST, (metadata) -> new ComponentMetadataHelper(metadata)
                        .setSize(2, 5)
                        .setPosition(0, 0));

        final var valueMapSendable = new MutableValueMapSendable<>(AutonomousConfigKey.class);
        layout.add("Config", new SendableComponent<>(valueMapSendable), (metadata) -> new ComponentMetadataHelper(metadata)
                .setProperties(Constants.ROBOT_PREFERENCES_PROPERTIES));
        autonomousConfigKeyValueMap = valueMapSendable.getMutableValueMap();
    }

    public Action createAutonomousAction(Transform2 startingTransform){
        try {
            return Actions.createLogAndEndTryCatchAction(
                    new Actions.ActionQueueBuilder(
                            new AutonomousInputWaitAction(clock, autonomousConfigKeyValueMap.getDouble(AutonomousConfigKey.WAIT_TIME), () -> false, () -> false),
                            autonomousModeCreator.createAction(new AutonomousSettings(), startingTransform)
                    ).build(),
                    Throwable.class, new PrintWriter(System.err)
            );
        } catch(IllegalArgumentException ex){
            ex.printStackTrace();
            System.out.println("One of our chooser must not have been set correctly!");
        }
        return Actions.createRunOnce(() -> System.out.println("This is the autonomous action because we got an error creating the one we wanted."));
    }
}
