# Configuring Motor Controllers
We use the CAN interface on our robot so we have to make sure all of our motor controllers 
(and PDP and any other CAN devices) have unique IDs.

## CTRE Motor Controllers (Talons, Victors)
* Use Phoenix Tuner on a Windows machine connected to the robot

You will be using the `TalonSRX`, `TalonFX` (falcon), or `VictorSPX` classes in the code.

* You can find the latest releases for things here: https://github.com/CrossTheRoadElec/Phoenix-Releases/releases

Using a breakout board? This will help: https://andymark-weblinc.netdna-ssl.com/media/W1siZiIsIjIwMTgvMTEvMjYvMTIvMzAvMDkvYjI0YzY3ZjEtNjcxNC00YTA4LTk2ZmMtZDMzZmY5ZjVjYWY5L2FtLTMyODEgU1JYIFVuaXZlcnNhbCBCcmVha291dCBVc2VyIEd1aWRlLnBkZiJdXQ/am-3281%20SRX%20Universal%20Breakout%20User%20Guide.pdf?sha=6b7ca31eb8965304

## REV Robotics Motor Controllers (Sparks)
* Use Spark MAX Client Application
* https://www.revrobotics.com/sparkmax-software/#spark-max-client-application
* https://www.revrobotics.com/sparkmax-users-manual/#section-3-3-2
* https://www.revrobotics.com/sparkmax-quickstart/#mode-configuration
  * You have to manually change the type on the motor by holding the mode button

You'll be using the `CANSparkMax` class most likely.
