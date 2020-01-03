package com.first1444.frc.robot2020.input;

import me.retrodaredevil.controller.implementations.*;
import me.retrodaredevil.controller.implementations.mappings.DefaultExtremeFlightJoystickInputCreator;
import me.retrodaredevil.controller.implementations.mappings.DefaultLogitechAttack3JoystickInputCreator;
import me.retrodaredevil.controller.implementations.mappings.PS4StandardControllerInputCreator;
import me.retrodaredevil.controller.options.OptionValues;
import me.retrodaredevil.controller.types.ExtremeFlightJoystickControllerInput;
import me.retrodaredevil.controller.types.LogitechAttack3JoystickControllerInput;
import me.retrodaredevil.controller.types.StandardControllerInput;

public final class InputUtil {
    private InputUtil(){ throw new UnsupportedOperationException(); }

    public static StandardControllerInput createPS4Controller(ControllerPartCreator controller){
        return createController(controller, new PS4StandardControllerInputCreator());
    }

    public static StandardControllerInput createController(ControllerPartCreator controller, StandardControllerInputCreator creator){
        Boolean physicalLocationSwapped = creator.getPhysicalLocationsSwapped();
        Boolean buttonNamesSwapped = creator.getButtonNamesSwapped();
        return new BaseStandardControllerInput(
                creator,
                controller,
                OptionValues.createImmutableBooleanOptionValue(physicalLocationSwapped == null ? false : physicalLocationSwapped),
                OptionValues.createImmutableBooleanOptionValue(buttonNamesSwapped == null ? false : buttonNamesSwapped)
        );
    }
    public static ExtremeFlightJoystickControllerInput createJoystick(ControllerPartCreator controller){
        return new BaseExtremeFlightJoystickControllerInput(
                new DefaultExtremeFlightJoystickInputCreator(),
                controller
        );
    }
    public static LogitechAttack3JoystickControllerInput createAttackJoystick(ControllerPartCreator controller){
        return new BaseLogitechAttack3JoystickControllerInput(new DefaultLogitechAttack3JoystickInputCreator(), controller);
    }
}
