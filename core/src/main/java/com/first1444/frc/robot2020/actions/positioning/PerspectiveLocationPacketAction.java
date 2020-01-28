package com.first1444.frc.robot2020.actions.positioning;

import com.first1444.frc.robot2020.Perspective;
import com.first1444.frc.robot2020.actions.SwerveDriveAction;
import com.first1444.frc.robot2020.packets.AbsolutePositionPacket;
import com.first1444.frc.robot2020.packets.Packet;
import com.first1444.frc.robot2020.packets.PerspectiveLocationPacket;
import com.first1444.frc.robot2020.packets.transfer.PacketQueue;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;
import com.first1444.sim.api.distance.MutableDistanceAccumulator;
import me.retrodaredevil.action.SimpleAction;

public class PerspectiveLocationPacketAction extends SimpleAction {
    private final PacketQueue packetQueue;
    private final SwerveDriveAction swerveDriveAction;
    public PerspectiveLocationPacketAction(PacketQueue packetQueue, SwerveDriveAction swerveDriveAction) {
        super(true);
        this.packetQueue = packetQueue;
        this.swerveDriveAction = swerveDriveAction;
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
                swerveDriveAction.setPerspective(new Perspective(Rotation2.ZERO, true, location));
                System.out.println("Changed perspective location to " + location);
            }
        }
    }
}
