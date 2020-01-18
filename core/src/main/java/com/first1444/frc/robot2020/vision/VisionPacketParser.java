package com.first1444.frc.robot2020.vision;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Transform2;
import com.first1444.sim.api.frc.implementations.infiniterecharge.Extra2020;
import com.first1444.sim.api.frc.implementations.infiniterecharge.VisionType2020;
import com.first1444.sim.api.surroundings.Surrounding;
import com.first1444.sim.api.surroundings.Surrounding3DExtra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VisionPacketParser {
    private final ObjectMapper mapper;
    private final Clock clock;
    // TODO instead of using a map, maybe create an interface so that if we have a turret, we can adjust the angle based on that
    private final Map<Integer, Rotation2> cameraOffsetMap;

    public VisionPacketParser(ObjectMapper mapper, Clock clock, Map<Integer, Rotation2> cameraOffsetMap) {
        this.mapper = mapper;
        this.clock = clock;
        this.cameraOffsetMap = cameraOffsetMap;
    }

    public List<Surrounding> parseSurroundings(String jsonString) throws IOException {
        List<VisionInstant> instants = mapper.readValue(jsonString, mapper.getTypeFactory().constructCollectionType(ArrayList.class, VisionInstant.class));
        return parseSurroundings(clock.getTimeSeconds(), instants);
    }
    private List<Surrounding> parseSurroundings(double timestamp, List<VisionInstant> instants) throws IOException {
        final List<Surrounding> surroundings = new ArrayList<>();
        for(VisionInstant instant : instants){
            final Rotation2 offset = cameraOffsetMap.get(instant.cameraId);
            if(offset == null){
                throw new IOException("The JSON requested cameraId=" + instant.cameraId + " but we don't have an offset rotation definedin cameraOffsetMap=" + cameraOffsetMap);
            }
            for (VisionPacket packet : instant.packets) {
                // TODO Do we actually need to rotate stuff 90 degrees?
                final Surrounding surrounding = new Surrounding(
                        Transform2.fromDegrees(
                                packet.xMeters,
                                packet.zMeters,
                                packet.yawDegrees
                        ).rotate(offset),
                        timestamp,
                        new Extra2020(VisionType2020.POWER_PORT)
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
//        private final int status;
        private final double imageX, imageY;
        private final double xMeters, yMeters, zMeters;
        private final double yawDegrees, pitchDegrees, rollDegrees;

        @JsonIgnoreProperties({"theta_deg", "dist_m"})
        @JsonCreator
        private VisionPacket(
//                @JsonProperty(value = "status", required = true) int status,
                @JsonProperty(value = "imageX_px", required = true) double imageX,
                @JsonProperty(value = "imageY_px", required = true) double imageY,

                @JsonProperty(value = "x_m", required = true) double xMeters,
                @JsonProperty(value = "y_m", required = true) double yMeters,
                @JsonProperty(value = "z_m", required = true) double zMeters,

                @JsonProperty(value = "yaw_deg", required = true) double yawDegrees,
                @JsonProperty(value = "pitch_deg", required = true) double pitchDegrees,
                @JsonProperty(value = "roll_deg", required = true) double rollDegrees
        ) {
//            this.status = status;
            this.imageX = imageX;
            this.imageY = imageY;
            this.xMeters = xMeters;
            this.yMeters = yMeters;
            this.zMeters = zMeters;
            this.yawDegrees = yawDegrees;
            this.pitchDegrees = pitchDegrees;
            this.rollDegrees = rollDegrees;
        }
    }

    /**
     * More info <a href="https://github.com/frc1444/robot2020-vision/blob/master/VisionStatus.hpp">here</a>
     */
    private enum VisionStatus { // TODO use this
        TARGET_FOUND,
        NO_TARGET_FOUND,
        CAMERA_ERROR,
        PROCESSING_ERROR,
        NOT_RUNNING
    }

}
