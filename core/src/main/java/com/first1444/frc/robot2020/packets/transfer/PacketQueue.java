package com.first1444.frc.robot2020.packets.transfer;

import com.first1444.frc.robot2020.packets.Packet;

public interface PacketQueue extends AutoCloseable {
    Packet poll();
}
