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

# Launching Shuffleboard: (The dashboard we use)
./gradlew wpi:shuffleboard

# Launching OutlineViewer: (Good for debugging NetworkTable values)
./gradlew wpi:outlineviewer

# Running RoboSim Simlation:
./gradlew desktop:run

# Running WPI Simulation
./gradlew wpi:simulateJava

# Running RIOLog
./gradlew wpi:riolog

# Helpful for debugging
tail -f wpi/build/stdout/simulateJava.log | grep -Ev "Talon|CTR|Victor|Stop"
./gradlew wpi:riolog | grep -Ev "CTR|Stack"

```

### Project Structure
This project has four main modules:
* `core` - Main code that is shared in all modules
* `wpi` - WPI specific code that is deployed to the robot
* `gdx` - RoboSim simulation specific code
* `desktop` - Contains a class to launch the `gdx` module on the desktop

### Classes
You should put classes that require WPI/RoboRIO interaction in the `wpi` module.

When initializing things for `wpi`, you can change [WpiRunnableCreator.java](wpi/src/main/java/com/first1444/frc/robot2020/WpiRunnableCreator.java) when you
need to pass data to [Robot.java](core/src/main/java/com/first1444/frc/robot2020/Robot.java)

If you are brave, you can change [MyRobotCreator.kt](gdx/src/main/java/com/first1444/frc/robot2020/gdx/MyRobotCreator.kt) to pass different values to
[Robot.java](core/src/main/java/com/first1444/frc/robot2020/Robot.java) for the simulation. It does, however, use Kotlin.

Why do most of the classes implement Runnable? Runnable is a nice interface to use to have a common "update" method.
It probably isn't necessary to have most classes inherit Runnable, but it does make the code have some consistency.

# TODO
