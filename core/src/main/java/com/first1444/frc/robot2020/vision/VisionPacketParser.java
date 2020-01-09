package com.first1444.frc.robot2020.vision;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Transform2;
import com.first1444.sim.api.surroundings.Surrounding;
import com.first1444.sim.api.surroundings.Surrounding3DExtra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VisionPacketParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Clock clock;
    private final Map<Integer, Rotation2> cameraOffsetMap;

    public VisionPacketParser(Clock clock, Map<Integer, Rotation2> cameraOffsetMap) {
        this.clock = clock;
        this.cameraOffsetMap = cameraOffsetMap;
    }

    public List<Surrounding> parseSurroundings(String jsonString) throws JsonProcessingException {
        List<VisionInstant> instants = MAPPER.readValue(jsonString, MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, VisionInstant.class));
        return parseSurroundings(clock.getTimeSeconds(), instants);
    }
    private List<Surrounding> parseSurroundings(double timestamp, List<VisionInstant> instants){
        final List<Surrounding> surroundings = new ArrayList<>();
        for(VisionInstant instant : instants){
            final Rotation2 offset = cameraOffsetMap.get(instant.cameraId);
            for (VisionPacket packet : instant.packets) {
                final Transform2 correctTransform = new Transform2(
                        packet.xMeters, packet.zMeters,
                        Rotation2.ZERO
                ).getReversed();
                final Surrounding surrounding = new Surrounding(
                        Transform2.fromDegrees(
                                correctTransform.getX(),
                                correctTransform.getY(),
                                packet.yawDegrees
                        ).rotate(offset),
                        timestamp,
                        Surrounding3DExtra.fromDegrees(
                                packet.yMeters,
                                packet.pitchDegrees,
                                packet.rollDegrees
                        )
                );
                surroundings.add(surrounding);
            }
        }
        return surroundings;
    }
    private static class VisionInstant {
        private final List<VisionPacket> packets;
        private final int cameraId;

        private VisionInstant(
                @JsonProperty(value = "packets", required = true) @JsonDeserialize(as = ArrayList.class) List<VisionPacket> packets,
                @JsonProperty(value = "cameraId", required = true) int cameraId
        ) {
            this.packets = packets;
            this.cameraId = cameraId;
        }
    }
    private static class VisionPacket {
        private final double xMeters, yMeters, zMeters;
        private final double yawDegrees, pitchDegrees, rollDegrees;

        private VisionPacket(
                @JsonProperty(value = "xMeters", required = true) double xMeters,
                @JsonProperty(value = "yMeters", required = true) double yMeters,
                @JsonProperty(value = "zMeters", required = true) double zMeters,
                @JsonProperty(value = "yawDegrees", required = true) double yawDegrees,
                @JsonProperty(value = "pitchDegrees", required = true) double pitchDegrees,
                @JsonProperty(value = "rollDegrees", required = true) double rollDegrees
        ) {
            this.xMeters = xMeters;
            this.yMeters = yMeters;
            this.zMeters = zMeters;
            this.yawDegrees = yawDegrees;
            this.pitchDegrees = pitchDegrees;
            this.rollDegrees = rollDegrees;
        }
    }

}
