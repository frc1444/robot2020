package com.first1444.frc.robot2020.vision;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.first1444.sim.api.surroundings.Surrounding;
import com.first1444.sim.api.surroundings.SurroundingProvider;
import org.jetbrains.annotations.NotNull;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Collections;
import java.util.List;

public class VisionPacketListener implements SurroundingProvider, AutoCloseable {
    private final String address;
    private final VisionPacketParser parser;
    private final Thread thread;
    private List<Surrounding> surroundingList = null;
    public VisionPacketListener(VisionPacketParser parser, String address){
        this.parser = parser;
        this.address = address;
        Thread thread = new Thread(this::run);
        thread.setDaemon(true);
        this.thread = thread;
    }

    public void start(){
        thread.start();
    }

    @Override
    public void close() {
        thread.interrupt();
    }

    @NotNull
    @Override
    public List<Surrounding> getSurroundings() {
        final List<Surrounding> r;
        synchronized (this) {
            r = this.surroundingList;
        }
        if(r == null){
            return Collections.emptyList();
        }
        return r;
    }

    private void run() {
        try(ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(SocketType.SUB);
            socket.connect(address);
            socket.setLinger(0);
            socket.subscribe("".getBytes());

            while (!Thread.currentThread().isInterrupted()) {
                final String reply = socket.recvStr(0);
                if(reply != null) {
                    try {
                        List<Surrounding> surroundings = parser.parseSurroundings(reply);
                        synchronized (this){
                            surroundingList = surroundings;
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
