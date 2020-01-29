package com.first1444.frc.robot2020.gdx

import com.badlogic.gdx.physics.box2d.Body
import com.first1444.dashboard.bundle.DashboardBundle
import com.first1444.frc.robot2020.perspective.PerspectiveHandler
import com.first1444.sim.api.Vector2
import com.first1444.sim.gdx.Updateable

class PerspectiveBodyUpdater(
        bundle: DashboardBundle,
        private val body: Body
) : Updateable {
    companion object {
        private val DEFAULT_ARRAY = doubleArrayOf(0.0, 0.0)
    }
    private val dashboard = bundle.rootDashboard.getSubDashboard(PerspectiveHandler.PERSPECTIVE_DASHBOARD)
    override fun update(delta: Float) {
        var used = dashboard[PerspectiveHandler.IS_LOCATION_USED_KEY].getter.getBoolean(false)
        val locationArray = dashboard[PerspectiveHandler.LOCATION_KEY].getter.getDoubleArray(DEFAULT_ARRAY)
        val location: Vector2?
        if(locationArray.size == 2){
            location = Vector2(locationArray[0], locationArray[1])
        } else {
            location = null
            used = false
        }
        if(used){
            location!!
            body.setTransform(location.x.toFloat(), location.y.toFloat(), 0f)
            body.isActive = true
        } else {
            body.isActive = false
        }
    }

    override fun close() {
    }
}
