# CAN IDs
* https://docs.wpilib.org/en/latest/docs/software/can-devices/can-addressing.html

CAN IDs use 6 bits, so they can range from 0 to 63. However, 63 is reserved.

Just some ideas for a convention for our CAN IDs: (We may not use this)
* Don't use 0 (devices default to 0)
* 1 through 19 are for testing
* 20 through 29 are used for drive train
  * 21 through 24 recommended Swerve Drive Modules (Talon SRX or Talon FX)
  * 25 through 28 recommended Swerve Steer Modules (Talon SRX)
* 30 through 39 are used for mechanisms using Talon SRXs, Talon FXs, and Victor SPXs
* 40 through 49 are used for mechanisms using Spark MAXs
* 50 through 59 are for any other devices
* 60 is our PDP
* Don't use 63
