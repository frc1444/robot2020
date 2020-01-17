package com.first1444.frc.robot2020.sound;

import com.first1444.sim.api.sound.ActiveSound;
import com.first1444.sim.api.sound.Sound;
import com.first1444.sim.api.sound.SoundCreator;
import com.first1444.sim.api.sound.implementations.DummyActiveSound;
import org.jetbrains.annotations.NotNull;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

@Deprecated
public class ZMQSoundCreator implements SoundCreator {

    private final ZContext context;
    private final ZMQ.Socket socket;

    public ZMQSoundCreator(int port){
        context = new ZContext();
        try {
            socket = context.createSocket(SocketType.PUB);
            socket.bind("tcp://*:" + port);
        } catch(Throwable t){
            context.close();
            throw t;
        }
    }

    @Override
    public void close() {
        context.close();
        socket.close();
    }

    @NotNull
    @Override
    public Sound create(@NotNull String string) {
        return new ZMQSound(string);
    }
    private class ZMQSound implements Sound {
        private final String sound;

        private ZMQSound(String sound) {
            this.sound = sound;
        }

        @NotNull
        @Override
        public ActiveSound play() {
            socket.send(sound);
            return DummyActiveSound.INSTANCE;
        }
    }
}
