package com.first1444.frc.robot2020.autonomous;

import com.first1444.dashboard.advanced.implementations.chooser.ChooserSendable;
import com.first1444.dashboard.advanced.implementations.chooser.MutableMappedChooserProvider;
import com.first1444.dashboard.advanced.implementations.chooser.SimpleMappedChooserProvider;
import com.first1444.dashboard.shuffleboard.ComponentMetadataHelper;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.dashboard.shuffleboard.ShuffleboardContainer;
import com.first1444.dashboard.shuffleboard.implementations.ShuffleboardLayoutComponent;
import com.first1444.frc.robot2020.Constants;
import com.first1444.frc.robot2020.DashboardMap;
import com.first1444.frc.robot2020.autonomous.actions.AutonomousInputWaitAction;
import com.first1444.frc.robot2020.autonomous.options.AutonomousSettings;
import com.first1444.frc.robot2020.autonomous.options.AutonomousType;
import com.first1444.frc.robot2020.autonomous.options.BasicMovementType;
import com.first1444.frc.util.valuemap.ValueMap;
import com.first1444.frc.util.valuemap.sendable.MutableValueMapSendable;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.Transform2;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;

import java.util.HashMap;
import java.util.Map;

public class AutonomousChooserState {
    private final Clock clock;
    private final AutonomousModeCreator autonomousModeCreator;

    @SuppressWarnings("UnusedAssignment") // this is used in the constructor and it's initialization to false is necessary
    private boolean readyToListen = false;
    private final MutableMappedChooserProvider<AutonomousType> autonomousChooser;
    private final MutableMappedChooserProvider<BasicMovementType> basicMovementChooser;

    private final ValueMap<AutonomousConfigKey> autonomousConfigKeyValueMap;

    public AutonomousChooserState(
            Clock clock, AutonomousModeCreator autonomousModeCreator,
            DashboardMap dashboardMap
    ) {
        this.clock = clock;
        this.autonomousModeCreator = autonomousModeCreator;

        final ShuffleboardContainer layout = dashboardMap.getUserTab()
                .add("Autonomous", ShuffleboardLayoutComponent.LIST, (metadata) -> new ComponentMetadataHelper(metadata)
                        .setSize(2, 5)
                        .setPosition(0, 0));
        autonomousChooser = new SimpleMappedChooserProvider<>(key -> onAutonomousChange());
        basicMovementChooser = new SimpleMappedChooserProvider<>();
        final var valueMapSendable = new MutableValueMapSendable<>(AutonomousConfigKey.class);
        layout.add("Config", new SendableComponent<>(valueMapSendable), (metadata) -> new ComponentMetadataHelper(metadata)
                .setProperties(Constants.ROBOT_PREFERENCES_PROPERTIES));
        autonomousConfigKeyValueMap = valueMapSendable.getMutableValueMap();
        addAutoOptions();
        updateBasicMovementChooser();

        layout.add(
                "Autonomous Chooser", new SendableComponent<>(new ChooserSendable(autonomousChooser)),
                metadata -> new ComponentMetadataHelper(metadata).setSize(2, 1).setPosition(0, 0)
        );
        layout.add(
                "Basic Movement", new SendableComponent<>(new ChooserSendable(basicMovementChooser)),
                metadata -> new ComponentMetadataHelper(metadata).setSize(2, 1).setPosition(0, 1)
        );

        readyToListen = true;
    }
    private void onAutonomousChange(){
        if(!readyToListen){
            return;
        }
        updateBasicMovementChooser();
    }

    public Action createAutonomousAction(Transform2 startingTransform){
        try {
            return new Actions.ActionQueueBuilder(
                    new AutonomousInputWaitAction(clock, autonomousConfigKeyValueMap.getDouble(AutonomousConfigKey.WAIT_TIME), () -> false, () -> false),
                    autonomousModeCreator.createAction(new AutonomousSettings(AutonomousType.DO_NOTHING, BasicMovementType.BACKWARD), startingTransform)
            ).build();
        } catch(IllegalArgumentException ex){
            ex.printStackTrace();
            System.out.println("One of our chooser must not have been set correctly!");
        }
        return Actions.createRunOnce(() -> System.out.println("This is the autonomous action because we got an error creating the one we wanted."));
    }
    private void addAutoOptions(){
        for(AutonomousType type : AutonomousType.values()){
            autonomousChooser.addOption(type.getDisplayName(), type, type == AutonomousType.DO_NOTHING);
        }
    }
    private void updateBasicMovementChooser(){
        AutonomousType autonomousType = autonomousChooser.getSelected();
        Map<String, BasicMovementType> map = new HashMap<>();
        BasicMovementType defaultMovement = null;
        for(BasicMovementType basicMovementType : autonomousType.getBasicMovementTypes()){
            defaultMovement = basicMovementType;
            map.put(basicMovementType.getDisplayName(), basicMovementType);
        }
        basicMovementChooser.set(map, defaultMovement == null ? "" : defaultMovement.getDisplayName());
    }
}
