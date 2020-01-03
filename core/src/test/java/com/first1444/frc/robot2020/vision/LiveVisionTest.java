package com.first1444.frc.robot2020.vision;

public class LiveVisionTest {
    // TODO Make this work for the updated vision
    /*
	public static void main(String[] args){
		final VisionPacketListener packetListener = new VisionPacketListener(5801);
		packetListener.start();
		
		final VisionInstant[] lastInstants = new VisionInstant[2];
		while(true){
			for(int i = 0; i < 2; i++) {
				final int id = i;
				final VisionInstant instant = packetListener.getInstant(id);
				if (lastInstants[i] != instant) {
					lastInstants[i] = instant;
					System.out.println(instant.toString().replaceAll(",", ",\n"));
				}
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				break;
			}
		}
	}*/
}
