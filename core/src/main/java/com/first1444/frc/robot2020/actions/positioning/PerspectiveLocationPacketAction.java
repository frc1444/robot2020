package com.first1444.frc.robot2020.actions.positioning;

import com.first1444.frc.robot2020.packets.Packet;
import com.first1444.frc.robot2020.packets.PerspectiveLocationPacket;
import com.first1444.frc.robot2020.packets.transfer.PacketQueue;
import com.first1444.frc.robot2020.perspective.PerspectiveHandler;
import com.first1444.sim.api.Vector2;
import me.retrodaredevil.action.SimpleAction;

/**
 * Listens for {@link PerspectiveLocationPacket}s.
 */
public class PerspectiveLocationPacketAction extends SimpleAction {
    private final PacketQueue packetQueue;
    private final PerspectiveHandler perspectiveHandler;
    public PerspectiveLocationPacketAction(PacketQueue packetQueue, PerspectiveHandler perspectiveHandler) {
        super(true);
        this.packetQueue = packetQueue;
        this.perspectiveHandler = perspectiveHandler;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        while(true){
            Packet packet = packetQueue.poll();
            if(packet == null) break;

            if(packet instanceof PerspectiveLocationPacket){
                PerspectiveLocationPacket absolutePositionPacket = (PerspectiveLocationPacket) packet;
                Vector2 location = absolutePositionPacket.getLocation();
                perspectiveHandler.setPerspectiveLocation(location);
                perspectiveHandler.setToPointOriented();
            }
        }
    }
}
