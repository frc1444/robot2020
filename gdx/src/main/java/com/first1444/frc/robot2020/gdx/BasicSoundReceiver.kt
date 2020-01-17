package com.first1444.frc.robot2020.gdx

import com.first1444.sim.api.sound.Sound
import com.first1444.sim.api.sound.SoundCreator
import com.first1444.sim.gdx.CloseableUpdateable
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ

@Deprecated("")
class BasicSoundReceiver(
        private val soundCreator: SoundCreator,
        address: String
) : CloseableUpdateable {

    private val cache = HashMap<String, Sound>()

    private val context: ZContext
    private val socket: ZMQ.Socket
    init {
        context = ZContext()
        socket = context.createSocket(SocketType.SUB)
        socket.connect(address)
        socket.linger = 0
        socket.subscribe(byteArrayOf())
    }
    override fun update(delta: Float) {
        val string = socket.recvStr(ZMQ.DONTWAIT)
        if(string != null){
            val sound = cache.getOrPut(string) { soundCreator.create(string) }
            sound.play()
        }
    }

    override fun close() {
        context.close()
        socket.close()
    }
}
