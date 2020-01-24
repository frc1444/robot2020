package com.first1444.frc.robot2020.gdx

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.first1444.dashboard.BasicDashboard
import com.first1444.dashboard.value.BasicValueType
import com.first1444.frc.robot2020.Constants
import com.first1444.frc.robot2020.actions.positioning.SurroundingDashboardLoggerAction
import com.first1444.sim.gdx.CloseableUpdateable
import com.first1444.sim.gdx.init.UpdateableCreator

class VisionDebugInfoUpdateableCreator(
        private val rootDashboard: BasicDashboard
) : UpdateableCreator {
    override fun create(data: UpdateableCreator.Data): CloseableUpdateable {
        val group = Table().apply {
            setFillParent(true)
        }
        val label: Label

        val table = Table()
        val skin = data.uiSkin // Maybe we don't want to use ui skin here, but it's nice and simple
        group.addActor(table)
        table.apply {
            setFillParent(true)
            center()
            right()
            label = Label("text", skin)
            add(label)
        }
        data.uiStage.addActor(table)
        return object : CloseableUpdateable {
            override fun update(delta: Float) {
                val dashboard = rootDashboard.getSubDashboard(SurroundingDashboardLoggerAction.SURROUNDING_DEBUG_DASHBOARD)
                val timeDifferenceValue = dashboard[SurroundingDashboardLoggerAction.SURROUNDING_TIME_DIFFERENCE_KEY].getter.value
                if(timeDifferenceValue != null && timeDifferenceValue.type == BasicValueType.DOUBLE){
                    val timeDifference = (timeDifferenceValue.value as Number).toDouble()
                    val visionTargetCount = dashboard[SurroundingDashboardLoggerAction.SURROUNDING_COUNT_KEY].getter.getNumber(0).toInt()
                    label.setText("Vision: $visionTargetCount\n${Constants.DECIMAL_FORMAT.format(timeDifference)}s ago")
                } else {
                    label.setText("No Vision")
                }
            }

            override fun close() {
            }
        }
    }

}
