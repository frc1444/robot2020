package com.first1444.frc.robot2020.packets.transfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.first1444.frc.robot2020.packets.Packet;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import static java.util.Objects.requireNonNull;

public class ZMQPacketHandler implements PacketHandler {
    private final ObjectMapper mapper;
    private final ZContext context;
    private final ZMQ.Socket socket;

    public ZMQPacketHandler(ObjectMapper mapper, ZContext context, ZMQ.Socket socket) {
        this.mapper = requireNonNull(mapper);
        this.context = requireNonNull(context);
        this.socket = requireNonNull(socket);
    }
    public static ZMQPacketHandler createPublisher(ObjectMapper mapper, int port){
        ZContext context = new ZContext();
        final ZMQ.Socket socket;
        try {
            socket = context.createSocket(SocketType.PUB);
            socket.bind("tcp://*:" + port);
            socket.setLinger(0);
        } catch(Throwable t){
            context.close();
            throw t;
        }
        return new ZMQPacketHandler(mapper, context, socket);
    }
    public static ZMQPacketHandler createSubscriber(ObjectMapper mapper, String address){
        ZContext context = new ZContext();
        final ZMQ.Socket socket;
        try {
            socket = context.createSocket(SocketType.SUB);
            socket.connect(address);
            socket.subscribe("".getBytes());
            socket.setLinger(0);
        } catch(Throwable t){
            context.close();
            throw t;
        }
        return new ZMQPacketHandler(mapper, context, socket);
    }

    @Override
    public Packet poll() {
        String data = socket.recvStr(ZMQ.DONTWAIT);
        if(data == null){
            return null;
        }
        final Packet packet;
        try {
            packet = mapper.readValue(data, Packet.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
        return packet;
    }

    @Override
    public void send(Packet packet) {
        final String data;
        try {
            data = mapper.writeValueAsString(packet);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        socket.send(data, ZMQ.DONTWAIT);
    }

    @Override
    public void close() {
        context.close();
        socket.close();
    }
}
