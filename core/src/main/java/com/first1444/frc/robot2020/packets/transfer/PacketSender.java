package com.first1444.frc.robot2020.packets.transfer;

import com.first1444.frc.robot2020.packets.Packet;

public interface PacketSender extends AutoCloseable {
    void send(Packet packet);

    PacketSender NOTHING = new PacketSender() {
        @Override
        public void send(Packet packet) {

        }

        @Override
        public void close() throws Exception {

        }
    };
}
