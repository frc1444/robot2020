package com.first1444.frc.robot2020.packets.transfer;

public interface PacketQueueCreator extends AutoCloseable {
    PacketQueue create();
}
