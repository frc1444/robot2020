package com.first1444.frc.robot2020.vision;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.first1444.frc.robot2020.vision.offset.OffsetProvider;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Transform2;
import com.first1444.sim.api.frc.implementations.infiniterecharge.Extra2020;
import com.first1444.sim.api.frc.implementations.infiniterecharge.VisionType2020;
import com.first1444.sim.api.surroundings.Surrounding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VisionPacketParser {
    private final ObjectMapper mapper;
    private final OffsetProvider offsetProvider;

    private final JavaType visionInstantArrayListType;

    public VisionPacketParser(ObjectMapper mapper, OffsetProvider offsetProvider) {
        this.mapper = mapper;
        this.offsetProvider = offsetProvider;
        visionInstantArrayListType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, VisionInstant.class);
    }

    public List<Surrounding> parseSurroundings(double timestamp, String jsonString) throws IOException {
        List<VisionInstant> instants = mapper.readValue(jsonString, visionInstantArrayListType);
        return parseSurroundings(timestamp, instants);
    }
    private List<Surrounding> parseSurroundings(double timestamp, List<VisionInstant> instants) throws IOException {
        final List<Surrounding> surroundings = new ArrayList<>();
        for(VisionInstant instant : instants){
            final Rotation2 offset;
            try {
                offset = offsetProvider.getOffset(instant.cameraId);
            } catch (InvalidCameraIdException e) {
                throw new IOException(e);
            }
            for (VisionPacket packet : instant.packets) {
                final Surrounding surrounding = new Surrounding(
                        Transform2.fromDegrees(
                                packet.zMeters,
                                -packet.xMeters,
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
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class VisionPacket {
//        private final int status;
        private final double imageX, imageY;
        private final double xMeters, yMeters, zMeters;
        private final double yawDegrees, pitchDegrees, rollDegrees;

//        @JsonIgnoreProperties({"theta_deg", "dist_mm"})
        @JsonCreator
        private VisionPacket(
//                @JsonProperty(value = "status", required = true) int status,
                @JsonProperty(value = "imageX_px", required = true) double imageX,
                @JsonProperty(value = "imageY_px", required = true) double imageY,

                @JsonProperty(value = "x_mm", required = true) double xMeters,
                @JsonProperty(value = "y_mm", required = true) double yMeters,
                @JsonProperty(value = "z_mm", required = true) double zMeters,

                @JsonProperty(value = "yaw_deg", required = true) double yawDegrees,
                @JsonProperty(value = "pitch_deg", required = true) double pitchDegrees,
                @JsonProperty(value = "roll_deg", required = true) double rollDegrees
        ) {
//            this.status = status;
            this.imageX = imageX;
            this.imageY = imageY;
            this.xMeters = xMeters / 1000.0;
            this.yMeters = yMeters / 1000.0;
            this.zMeters = zMeters / 1000.0;
            this.yawDegrees = yawDegrees;
            this.pitchDegrees = pitchDegrees;
            this.rollDegrees = rollDegrees;
        }
    }

    /**
     * More info <a href="https://github.com/frc1444/robot2020-vision/blob/master/VisionStatus.hpp">here</a>
     */
    private enum VisionStatus { // use this if we find it useful later
        TARGET_FOUND,
        NO_TARGET_FOUND,
        CAMERA_ERROR,
        PROCESSING_ERROR,
        NOT_RUNNING
    }

}
