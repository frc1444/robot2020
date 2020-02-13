# Configuring Motor Controllers
We use the CAN interface on our robot so we have to make sure all of our motor controllers 
(and PDP and any other CAN devices) have unique IDs.

## CTRE Motor Controllers (Talons, Victors)
* Use Phoenix Tuner on a Windows machine connected to the robot

You will be using the `TalonSRX`, `TalonFX` (falcon), or `VictorSPX` classes in the code.

## REV Robotics Motor Controllers (Sparks)
* Use Spark MAX Client Application
* https://www.revrobotics.com/sparkmax-software/#spark-max-client-application
* https://www.revrobotics.com/sparkmax-users-manual/#section-3-3-2
* https://www.revrobotics.com/sparkmax-quickstart/#mode-configuration
  * You have to manually change the type on the motor by holding the mode button

You'll be using the `CANSparkMax` class most likely.
