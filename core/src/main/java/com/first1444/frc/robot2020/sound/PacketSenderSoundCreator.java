package com.first1444.frc.robot2020.sound;

import com.first1444.frc.robot2020.packets.SoundPacket;
import com.first1444.frc.robot2020.packets.transfer.PacketSender;
import com.first1444.sim.api.sound.ActiveSound;
import com.first1444.sim.api.sound.Sound;
import com.first1444.sim.api.sound.SoundCreator;
import com.first1444.sim.api.sound.implementations.DummyActiveSound;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class PacketSenderSoundCreator implements SoundCreator {
    private final PacketSender packetSender;
    private final boolean closePacketSender;

    public PacketSenderSoundCreator(PacketSender packetSender, boolean closePacketSender) {
        this.packetSender = requireNonNull(packetSender);
        this.closePacketSender = closePacketSender;
    }

    @Override
    public void close() throws Exception {
        if(closePacketSender){
            packetSender.close();
        }
    }

    @NotNull
    @Override
    public Sound create(@NotNull String sound) {
        return new PacketSound(sound);
    }
    private class PacketSound implements Sound {
        private final String sound;

        private PacketSound(String sound) {
            this.sound = sound;
        }

        @NotNull
        @Override
        public ActiveSound play() {
            packetSender.send(new SoundPacket(sound));
            return DummyActiveSound.INSTANCE;
        }
    }
}
