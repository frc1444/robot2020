package com.first1444.frc.robot2020;

import edu.wpi.first.hal.sim.DriverStationSim;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;

public class LiveSimulation {
	public static void main(String[] args){
		// I can't get this to work so don't try it
		RobotBase.startRobot(Main::createRobot);
		final var sim = new DriverStationSim();
		System.out.println("Simulation starting");
		
		sim.setDsAttached(false);
		sim.setEnabled(false);
		
		System.out.println("Autonomous starting");
		sim.setAutonomous(true);
		sim.setEnabled(true);
		
		Timer.delay(15);
		System.out.println("Teleop starting");
		sim.setEnabled(false);
		
		Timer.delay(.5);
		sim.setAutonomous(false);
		sim.setEnabled(true);
		
		Timer.delay(135);
		sim.setEnabled(false);
		System.out.println("Match ended");
		
	}
}
