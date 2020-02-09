package com.first1444.frc.robot2020.packets.transfer;

public interface PacketQueueCreator extends AutoCloseable {
    PacketQueue create();
    PacketQueueCreator NOTHING = new PacketQueueCreator() {
        @Override
        public PacketQueue create() {
            return PacketQueue.NOTHING;
        }

        @Override
        public void close() throws Exception {

        }
    };
}
