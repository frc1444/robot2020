package com.first1444.frc.robot2020.gdx

import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef
import com.first1444.frc.robot2020.subsystems.Turret
import com.first1444.frc.robot2020.subsystems.implementations.BaseTurret
import com.first1444.sim.api.Clock
import com.first1444.sim.api.EnabledState
import com.first1444.sim.api.Rotation2
import com.first1444.sim.gdx.entity.BodyEntity
import com.first1444.sim.gdx.velocity.SetPointHandler

class GdxTurret(
        world: World,
        private val entity: BodyEntity,
        private val clock: Clock,
        private val enabledState: EnabledState,
        private val angleRadiansSetPointHandler: SetPointHandler
) : BaseTurret() {
    private var lastTimestamp: Double? = null

    @Suppress("JoinDeclarationAndAssignment")
    private val body: Body
    init {
        body = world.createBody(BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            position.set(entity.position)
        }).apply {
            createFixture(FixtureDef().apply {
                isSensor = true
                shape = EdgeShape().apply {
                    set(0f, 0f, .5f, 0f)
                }
            })
        }
        val joint = RevoluteJointDef().apply {
            bodyA = entity.body
            bodyB = body
            localAnchorA.set(0.0f, 0.0f)
            localAnchorB.set(0.0f, 0.0f)
            referenceAngle = 0.0f
        }
        world.createJoint(joint)
    }
    override fun run(desiredState: Turret.DesiredState) {
        val timestamp = clock.timeSeconds
        val lastTimestamp = this.lastTimestamp
        this.lastTimestamp = timestamp
        if(lastTimestamp != null) {
            val delta = timestamp - lastTimestamp

            val isEnabled = enabledState.isEnabled
            val desiredRotation = desiredState.desiredRotation
            if(desiredRotation != null){
                if(isEnabled){
                    angleRadiansSetPointHandler.setDesired(desiredRotation.radians.toFloat())
                    angleRadiansSetPointHandler.update(delta.toFloat())
                }
                body.setTransform(body.position, angleRadiansSetPointHandler.calculated + entity.rotationRadians)
            } else {
                val rawSpeed = -desiredState.rawSpeed!!
                body.setTransform(body.position, (delta * rawSpeed).toFloat() + entity.rotationRadians)
            }
        }
    }

}
