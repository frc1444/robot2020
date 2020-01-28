package com.first1444.frc.robot2020.gdx

import com.badlogic.gdx.physics.box2d.*
import com.first1444.sim.gdx.Updateable
import com.first1444.sim.gdx.WorldManager
import com.first1444.sim.gdx.implementations.infiniterecharge2020.PowerCellUserData
import kotlin.math.max


class IntakeListener(
        private val worldManager: WorldManager,
        private val intakeSpeedGetter: () -> Double
) : Updateable, ContactListener {
    private val map = HashMap<PowerCellUserData, Float>()

    override fun beginContact(contact: Contact) {
        val data = getData(contact) ?: return
        map[data] = 0f
    }
    override fun endContact(contact: Contact) {
        val data = getData(contact) ?: return
        map.remove(data)
    }
    private fun getData(contact: Contact): PowerCellUserData? {
        val a = contact.fixtureA
        val b = contact.fixtureB
        return check(a, b) ?: check(b, a)
    }
    private fun check(intakeFixture: Fixture, powerCellFixture: Fixture): PowerCellUserData? {
        val intakeData = intakeFixture.userData
        val powerCellData = powerCellFixture.body.userData
        if(intakeData is IntakeUserData && powerCellData is PowerCellUserData){
            return powerCellData
        }
        return null
    }
    override fun update(delta: Float) {
        val toRemove = mutableListOf<PowerCellUserData>()
        for(entry in map){
            val newValue = entry.value + delta / .4f * -intakeSpeedGetter().toFloat()
            if(newValue >= 1.0f){
                toRemove.add(entry.key)
            } else {
                entry.setValue(max(0f, newValue))
            }
        }
        for(powerCellData in toRemove){
            map.remove(powerCellData)
            worldManager.remove(powerCellData.powerCell)
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {}
    override fun postSolve(contact: Contact, impulse: ContactImpulse) {}
    override fun close() {}
}
