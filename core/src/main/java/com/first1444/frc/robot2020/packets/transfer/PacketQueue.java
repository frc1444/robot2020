package com.first1444.frc.robot2020.packets.transfer;

import com.first1444.frc.robot2020.packets.Packet;

public interface PacketQueue extends AutoCloseable {
    Packet poll();

    PacketQueue NOTHING = new PacketQueue() {
        @Override
        public Packet poll() {
            return null;
        }

        @Override
        public void close() throws Exception {

        }
    };
}
