package com.first1444.frc.robot2020.vision;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.SystemMillisClock;
import com.first1444.sim.api.surroundings.Surrounding;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VisionPacketParserTest {

    @Test
    void test() throws IOException {
        Path path = new File(getClass().getResource("example_json.json").getFile()).toPath();
        String json = Files.readString(path);
        System.out.println(json);
        VisionPacketParser parser = new VisionPacketParser(new ObjectMapper(), SystemMillisClock.INSTANCE, Map.of(1, Rotation2.ZERO));
        List<Surrounding> surroundings = parser.parseSurroundings(json);
        assertEquals(1, surroundings.size());
        Surrounding surrounding = surroundings.get(0);
        System.out.println(surrounding);
    }
}
