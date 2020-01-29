package com.first1444.frc.robot2020.gdx

import com.badlogic.gdx.physics.box2d.Body
import com.first1444.frc.robot2020.packets.PerspectiveLocationPacket
import com.first1444.frc.robot2020.packets.transfer.PacketQueue
import com.first1444.sim.gdx.Updateable

class PerspectiveBodyUpdater(
        private val packetQueue: PacketQueue,
        private val body: Body
) : Updateable {
    override fun update(delta: Float) {
        while(true){
            val packet = packetQueue.poll() ?: break
            if(packet is PerspectiveLocationPacket){
                val location = packet.location
                body.setTransform(location.x.toFloat(), location.y.toFloat(), 0f)
            }
        }
    }

    override fun close() {
    }
}
