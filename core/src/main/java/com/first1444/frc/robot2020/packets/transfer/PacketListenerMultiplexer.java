package com.first1444.frc.robot2020.packets.transfer;

import com.first1444.frc.robot2020.packets.Packet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PacketListenerMultiplexer implements PacketListener {
    private final List<PacketListener> packetListeners;

    public PacketListenerMultiplexer(Collection<? extends PacketListener> packetListeners) {
        this.packetListeners = new ArrayList<>(packetListeners);
    }

    @Override
    public void onPacketReceive(Packet packet) {
        for(PacketListener listener : packetListeners){
            listener.onPacketReceive(packet);
        }
    }
}
