package com.first1444.frc.robot2020.packets.transfer;

import com.first1444.frc.robot2020.packets.Packet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PacketQueueMaster {
    private final PacketQueue packetQueue;
    private final List<SubQueue> subQueueList = new ArrayList<>();

    public PacketQueueMaster(PacketQueue packetQueue) {
        this.packetQueue = packetQueue;
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

    public PacketQueue create(){
        SubQueue r = new SubQueue();
        subQueueList.add(r);
        return r;
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
