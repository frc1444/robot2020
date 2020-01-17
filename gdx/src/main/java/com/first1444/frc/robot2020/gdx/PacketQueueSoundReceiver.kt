package com.first1444.frc.robot2020.gdx

import com.first1444.frc.robot2020.packets.Packet
import com.first1444.frc.robot2020.packets.SoundPacket
import com.first1444.frc.robot2020.packets.transfer.PacketQueue
import com.first1444.sim.api.sound.Sound
import com.first1444.sim.api.sound.SoundCreator
import com.first1444.sim.gdx.Updateable

class PacketQueueSoundReceiver(
        private val soundCreator: SoundCreator,
        private val packetQueue: PacketQueue
) : Updateable {
    private val cache = HashMap<String, Sound>()
    override fun update(delta: Float) {
        while(true){
            val packet = packetQueue.poll() ?: break
            if(packet is SoundPacket){
                play(packet.sound)
            }
        }
    }
    private fun play(string: String){
        val sound = cache.getOrPut(string) { soundCreator.create(string) }
        sound.play()
    }

}
