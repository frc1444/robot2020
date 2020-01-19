package com.first1444.frc.robot2020.actions.positioning;

import com.first1444.frc.robot2020.packets.AbsolutePositionPacket;
import com.first1444.frc.robot2020.packets.Packet;
import com.first1444.frc.robot2020.packets.transfer.PacketQueue;
import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.distance.MutableDistanceAccumulator;
import me.retrodaredevil.action.SimpleAction;

/**
 * An action that "listens" for {@link AbsolutePositionPacket}s and updates the absolute position.
 */
public class AbsolutePositionPacketAction extends SimpleAction {
    private final PacketQueue packetQueue;
    private final MutableDistanceAccumulator absoluteDistanceAccumulator;
    public AbsolutePositionPacketAction(PacketQueue packetQueue, MutableDistanceAccumulator absoluteDistanceAccumulator) {
        super(true);
        this.packetQueue = packetQueue;
        this.absoluteDistanceAccumulator = absoluteDistanceAccumulator;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        while(true){
            Packet packet = packetQueue.poll();
            if(packet == null) break;

            if(packet instanceof AbsolutePositionPacket){
                AbsolutePositionPacket absolutePositionPacket = (AbsolutePositionPacket) packet;
                Vector2 position = absolutePositionPacket.getPosition();
                absoluteDistanceAccumulator.setPosition(position);
            }
        }
    }
}
