package com.first1444.frc.robot2020.packets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.first1444.sim.api.Vector2;
import org.junit.jupiter.api.Test;

public class PacketsTest {
    @Test
    void testJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(new AbsolutePositionPacket(new Vector2(3.0, 4.0))));
    }
}
