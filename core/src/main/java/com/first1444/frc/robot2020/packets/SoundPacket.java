package com.first1444.frc.robot2020.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import static java.util.Objects.requireNonNull;

@JsonTypeName("SOUND")
public class SoundPacket implements Packet {
    private final String sound;

    @JsonCreator
    public SoundPacket(@JsonProperty(value = "sound", required = true) String sound) {
        this.sound = requireNonNull(sound, "sound is null");
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.SOUND;
    }

    public String getSound() {
        return sound;
    }
}
