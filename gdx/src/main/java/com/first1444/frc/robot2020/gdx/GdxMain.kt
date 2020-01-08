package com.first1444.frc.robot2020.gdx

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.first1444.sim.gdx.init.*
import edu.wpi.first.networktables.NetworkTableInstance

private fun createSelectionCreator(uiSkin: Skin, changer: ScreenChanger): ScreenCreator {
    Controllers.getControllers() // we want to initialize this as soon as possible even if we won't use it yet
    val creator = MyRobotCreator
    val exitButtonUpdateableCreator = ExitButtonCreator(Runnable {
        changer.change(createSelectionCreator(uiSkin, changer).create(changer))
        NetworkTableInstance.getDefault().close()
    })
    return SelectionScreenCreator(
            uiSkin,
            FieldScreenCreator(uiSkin, UpdateableCreatorMultiplexer(listOf(
                    PracticeUpdateableCreator(creator),
                    Field2020Creator,
                    exitButtonUpdateableCreator
            ))),
            RealConfigScreenCreator(uiSkin) { _, config: RealConfig ->
                changer.change(FieldScreenCreator(uiSkin, UpdateableCreatorMultiplexer(listOf(
                        RealUpdateableCreator(uiSkin, config, creator),
                        Field2020Creator,
                        exitButtonUpdateableCreator
                ))).create(changer))
            }
    )
}

fun createScreen(): ApplicationListener {
    return SimpleGame { changer ->
        Gdx.graphics.setTitle("Infinite Recharge - RoboSim - 2020")
        val uiSkin = Skin(Gdx.files.classpath("skins/sgx/sgx-ui.json"))
        createSelectionCreator(uiSkin, changer).create(changer)
    }
}
