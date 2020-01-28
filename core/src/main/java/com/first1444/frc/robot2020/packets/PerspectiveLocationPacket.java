package com.first1444.frc.robot2020.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.first1444.sim.api.Vector2;

@JsonTypeName("PERSPECTIVE_LOCATION")
public class PerspectiveLocationPacket implements Packet {
    private final Vector2 location;

    @JsonCreator
    public PerspectiveLocationPacket(@JsonUnwrapped @JsonProperty(required = true) Vector2 location) {
        this.location = location;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.PERSPECTIVE_LOCATION;
    }
    @JsonUnwrapped
    public Vector2 getLocation(){
        return location;
    }
}
