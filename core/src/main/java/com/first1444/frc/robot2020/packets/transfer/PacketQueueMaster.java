package com.first1444.frc.robot2020.packets.transfer;

import com.first1444.frc.robot2020.packets.Packet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PacketQueueMaster implements PacketQueueCreator {
    private final PacketQueue packetQueue;
    private final boolean closePacketQueue;
    private final List<SubQueue> subQueueList = new ArrayList<>();

    public PacketQueueMaster(PacketQueue packetQueue, boolean closePacketQueue) {
        this.packetQueue = packetQueue;
        this.closePacketQueue = closePacketQueue;
    }
    public PacketQueueMaster(PacketQueue packetQueue) {
        this(packetQueue, true);
    }

    private void update(){
        List<Packet> packets = new ArrayList<>();
        while(true){
            Packet packet = packetQueue.poll();
            if(packet == null) break;
            packets.add(packet);
        }
        if(!packets.isEmpty()) {
            for (SubQueue subQueue : subQueueList) {
                subQueue.queue.addAll(packets);
            }
        }
    }

    @Override
    public PacketQueue create(){
        SubQueue r = new SubQueue();
        subQueueList.add(r);
        return r;
    }

    @Override
    public void close() throws Exception {
        if(closePacketQueue) {
            packetQueue.close();
        }
    }

    private class SubQueue implements PacketQueue {
        private final Queue<Packet> queue = new LinkedList<>();
        @Override
        public Packet poll() {
            update();
            return queue.poll();
        }

        @Override
        public void close() { }
    }

}
