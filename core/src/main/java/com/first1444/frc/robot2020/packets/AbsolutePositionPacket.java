package com.first1444.frc.robot2020.packets;

import com.fasterxml.jackson.annotation.*;
import com.first1444.sim.api.Vector2;

@JsonTypeName("ABSOLUTE_POSITION")
public class AbsolutePositionPacket implements Packet {
    private final Vector2 position;

    @JsonCreator
    public AbsolutePositionPacket(@JsonUnwrapped @JsonProperty(required = true) Vector2 position) {
        this.position = position;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.ABSOLUTE_POSITION;
    }
    @JsonUnwrapped
    public Vector2 getPosition(){
        return position;
    }
}
