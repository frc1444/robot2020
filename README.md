# robot2020
Our robot code for Infinite Recharge.

This project uses robo-sim and is not a standard WPILib project.

### Setup
You will need **Java 11** installed. Not 10, not 12, **11**.

Run the `./gradlew build` and `./gradlew wpi:downloadAll` commands described below.

It is recommended to use either IntelliJ or VS Code.

### Commands
NOTE: These commands work on Linux/Mac but must be altered on Windows.
To alter them on Windows, replace `./gradlew` with `gradlew.bat`

```shell script
# Building: (Make sure code is correct)
./gradlew build

# Downloading everything for WPI (Do this if you aren't going to have internet! (Like before competition!))
./gradlew wpi:downloadAll

# Deploying:
./gradlew wpi:deploy

# Launching Shuffleboard:
./gradlew wpi:shuffleboard

# Running RoboSim Simlation:
./gradlew desktop:run

# Download Vendor Dependencies
./gradlew wpi:downloadDepsPreemptively
```

### Project Structure
This project has four main modules:
* `core` - Main code that is shared in all modules
* `wpi` - WPI specific code that is deployed to the robot
* `gdx` - RoboSim simulation specific code
* `desktop` - Contains a class to launch the `gdx` module on the desktop
