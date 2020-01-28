package com.first1444.frc.robot2020.gdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.fasterxml.jackson.databind.ObjectMapper
import com.first1444.dashboard.bundle.ActiveDashboardBundle
import com.first1444.frc.robot2020.Constants
import com.first1444.frc.robot2020.DefaultDashboardMap
import com.first1444.frc.robot2020.Robot
import com.first1444.frc.robot2020.packets.AbsolutePositionPacket
import com.first1444.frc.robot2020.packets.PerspectiveLocationPacket
import com.first1444.frc.robot2020.packets.transfer.PacketQueueMaster
import com.first1444.frc.robot2020.packets.transfer.ZMQPacketQueue
import com.first1444.frc.robot2020.packets.transfer.ZMQPacketSender
import com.first1444.frc.robot2020.subsystems.Intake
import com.first1444.frc.robot2020.subsystems.Turret
import com.first1444.frc.robot2020.subsystems.implementations.DummyBallShooter
import com.first1444.frc.robot2020.subsystems.implementations.DummyClimber
import com.first1444.frc.robot2020.subsystems.implementations.DummyIntake
import com.first1444.frc.robot2020.subsystems.implementations.DummyWheelSpinner
import com.first1444.frc.robot2020.vision.VisionPacketListener
import com.first1444.frc.robot2020.vision.VisionPacketParser
import com.first1444.frc.util.reportmap.DashboardReportMap
import com.first1444.sim.api.*
import com.first1444.sim.api.drivetrain.swerve.FourWheelSwerveDriveData
import com.first1444.sim.api.drivetrain.swerve.SwerveModule
import com.first1444.sim.api.frc.AdvancedIterativeRobotBasicRobot
import com.first1444.sim.api.frc.BasicRobotRunnable
import com.first1444.sim.api.frc.implementations.infiniterecharge.VisionType2020
import com.first1444.sim.api.frc.sim.DriverStationSendable
import com.first1444.sim.api.frc.sim.PrintStreamFrcLogger
import com.first1444.sim.api.sensors.DefaultOrientationHandler
import com.first1444.sim.gdx.*
import com.first1444.sim.gdx.drivetrain.swerve.BodySwerveModule
import com.first1444.sim.gdx.entity.ActorBodyEntity
import com.first1444.sim.gdx.entity.BodyEntity
import com.first1444.sim.gdx.entity.EntityOrientation
import com.first1444.sim.gdx.implementations.infiniterecharge2020.FieldSetup2020
import com.first1444.sim.gdx.implementations.infiniterecharge2020.surroundings.VisionProvider2020
import com.first1444.sim.gdx.implementations.infiniterecharge2020.surroundings.VisionTypeFilter
import com.first1444.sim.gdx.implementations.surroundings.EntityRangeVisionFilter
import com.first1444.sim.gdx.implementations.surroundings.VisionFilterMultiplexer
import com.first1444.sim.gdx.init.RobotCreator
import com.first1444.sim.gdx.init.UpdateableCreator
import com.first1444.sim.gdx.sound.GdxSoundCreator
import com.first1444.sim.gdx.velocity.AccelerateSetPointHandler
import edu.wpi.first.networktables.NetworkTableInstance
import me.retrodaredevil.controller.gdx.GdxControllerPartCreator
import me.retrodaredevil.controller.gdx.IndexedControllerProvider
import me.retrodaredevil.controller.implementations.BaseStandardControllerInput
import me.retrodaredevil.controller.implementations.InputUtil
import me.retrodaredevil.controller.implementations.mappings.DefaultLogitechAttack3JoystickInputCreator
import me.retrodaredevil.controller.implementations.mappings.DefaultStandardControllerInputCreator
import me.retrodaredevil.controller.implementations.mappings.LinuxPS4StandardControllerInputCreator
import me.retrodaredevil.controller.options.OptionValues
import me.retrodaredevil.controller.output.DisconnectedRumble
import java.lang.Math.toRadians
import kotlin.experimental.or

private const val GOES_UNDER_TRENCH = true
private val STARTING_POSITION = Vector2(0.0, 4.87)
private val STARTING_ANGLE_RADIANS = toRadians(90.0)

private const val maxVelocity = 3.35
private val SWERVE = Constants.Swerve2019.INSTANCE
private val WHEEL_BASE = SWERVE.wheelBase // length
private val TRACK_WIDTH = SWERVE.trackWidth // width
private val INTAKE_EXTEND = inchesToMeters(6.0f)
private val INTAKE_WIDTH = inchesToMeters(12.0f)

private fun createEntity(data: RobotCreator.Data, updateableData: UpdateableCreator.Data): BodyEntity {
    return ActorBodyEntity(updateableData.contentStage, updateableData.worldManager.world, BodyDef().apply {
        type = BodyDef.BodyType.DynamicBody
        position.set(STARTING_POSITION)
        angle = STARTING_ANGLE_RADIANS.toFloat()
    }, listOf(FixtureDef().apply {
        restitution = .2f
        shape = PolygonShape().apply {
            setAsBox((WHEEL_BASE / 2).toFloat(), (TRACK_WIDTH / 2).toFloat(), ZERO, 0.0f)
        }
        val area = WHEEL_BASE * TRACK_WIDTH
        density = 1.0f / area.toFloat()
    }, FixtureDef().apply { // line that points in direction robot is facing
        isSensor = true
        shape = EdgeShape().apply {
            set(0f, 0f, .3f, 0f)
        }
    }
    )).also { entity ->
        @Suppress("ConstantConditionIf")
        if(!GOES_UNDER_TRENCH){
            entity.body.createFixture(FixtureDef().apply { // the box that collides with the trench
                filter.categoryBits = FieldSetup2020.TRENCH_MASK_BITS or 1
                shape = PolygonShape().apply {
                    setAsBox(inchesToMeters(15.0f / 2), inchesToMeters(15.0f / 2), ZERO, 0.0f)
                }
            })
        }
        entity.body.createFixture(FixtureDef().apply {
            isSensor = true
            shape = PolygonShape().apply {
                setAsBox(INTAKE_EXTEND / 2, INTAKE_WIDTH / 2, gdxVector(WHEEL_BASE.toFloat() / 2 + INTAKE_EXTEND / 2, 0f), 0f)
            }
        }).apply {
            userData = IntakeUserData()
        }
    }
}
private fun createSwerveDriveData(enabledState: EnabledState, updateableData: UpdateableCreator.Data, entity: BodyEntity): FourWheelSwerveDriveData {
    val wheelBody = BodyDef().apply {
        type = BodyDef.BodyType.DynamicBody
    }
    val wheelFixture = FixtureDef().apply {
        val wheelDiameter = inchesToMeters(4.0f)
        val wheelWidth = inchesToMeters(1.0f)
        val area = wheelDiameter * wheelWidth
        shape = PolygonShape().apply {
            setAsBox(wheelDiameter / 2, wheelWidth / 2, ZERO, 0.0f) // 4 inches by 1 inch
        }
        density = 1.0f / area
    }


    val frPosition = Vector2(WHEEL_BASE / 2, -TRACK_WIDTH / 2)
    val flPosition = Vector2(WHEEL_BASE / 2, TRACK_WIDTH / 2)
    val rlPosition = Vector2(-WHEEL_BASE / 2, TRACK_WIDTH / 2)
    val rrPosition = Vector2(-WHEEL_BASE / 2, -TRACK_WIDTH / 2)

    val moduleList = ArrayList<SwerveModule>(4)
    for((moduleName, position) in listOf(
            Pair("front right", frPosition),
            Pair("front left", flPosition),
            Pair("rear left", rlPosition),
            Pair("rear right", rrPosition))){
        val wheelEntity = ActorBodyEntity(updateableData.contentStage, updateableData.worldManager.world, wheelBody, listOf(wheelFixture))
        wheelEntity.setTransformRadians(position.rotateRadians(STARTING_ANGLE_RADIANS) + STARTING_POSITION, STARTING_ANGLE_RADIANS.toFloat())
        val joint = RevoluteJointDef().apply {
            bodyA = entity.body
            bodyB = wheelEntity.body
            localAnchorA.set(position)
            localAnchorB.set(0.0f, 0.0f)
            referenceAngle = 0.0f
        }
        updateableData.worldManager.world.createJoint(joint)
        val module = BodySwerveModule(
                moduleName, wheelEntity.body, entity.body, maxVelocity, updateableData.clock, enabledState,
                AccelerateSetPointHandler(maxVelocity.toFloat() / .5f, maxVelocity.toFloat() / .2f),
                AccelerateSetPointHandler(MathUtils.PI2 / .5f)
        )
        moduleList.add(module)
    }
    return FourWheelSwerveDriveData(
            moduleList[0], moduleList[1], moduleList[2], moduleList[3],
            WHEEL_BASE, TRACK_WIDTH
    )
}
private fun createTurret(enabledState: EnabledState, updateableData: UpdateableCreator.Data, entity: BodyEntity): Turret {
    return GdxTurret(updateableData.worldManager.world, entity, updateableData.clock, enabledState, AccelerateSetPointHandler(MathUtils.PI / .5f)) // spin 180 in half a second
}

class MyRobotCreator(
        private val dashboardBundle: ActiveDashboardBundle
) : RobotCreator {
    override fun create(data: RobotCreator.Data, updateableData: UpdateableCreator.Data): Updateable {
        val networkTable = NetworkTableInstance.getDefault()
        networkTable.startServer()
        val driverStationActiveComponent = DriverStationSendable(data.driverStation).init("FMSInfo", dashboardBundle.rootDashboard.getSubDashboard("FMSInfo"))
        val shuffleboardMap = DefaultDashboardMap(dashboardBundle)
        val reportMap = DashboardReportMap(shuffleboardMap.debugTab.rawDashboard.getSubDashboard("Report Map"))

        val preciseClock = SystemNanosClock
        val entity = createEntity(data, updateableData);
        val swerveDriveData = createSwerveDriveData(data.driverStation, updateableData, entity)
        val turret = createTurret(data.driverStation, updateableData, entity)
        val intakeListener: IntakeListener
        val intake: Intake
        run {
            val dummyIntake = DummyIntake(reportMap)
            intake = dummyIntake
            intakeListener = IntakeListener(updateableData.worldManager, dummyIntake::getIntakeSpeed)
        }
        updateableData.worldManager.world.setContactListener(intakeListener)


        val playstationProvider = BestNameControllerProvider(listOf("sony", "ps4", "playstation", "wireless controller"))
        val defaultProvider = IndexedControllerProvider(0)
        val creator = GdxControllerPartCreator(
                if(playstationProvider.isConnected || !defaultProvider.isConnected) playstationProvider else defaultProvider,
                true
        )
        val controller = if(playstationProvider.isConnected || !defaultProvider.isConnected){
            val osName = System.getProperty("os.name").toLowerCase()
            if("nux" in osName || "nix" in osName || "aix" in osName || "mac" in osName) {
                println("*nix ps4")
                InputUtil.createController(creator, LinuxPS4StandardControllerInputCreator())
            } else {
                println("regular ps4")
                InputUtil.createPS4Controller(creator)
            }
        } else {
            println("default controller")
            // NOTE: I have "physicalLocationSwapped" set to true because I test with a Nintendo controller most of the time
            BaseStandardControllerInput(DefaultStandardControllerInputCreator(), creator, OptionValues.createImmutableBooleanOptionValue(true), OptionValues.createImmutableBooleanOptionValue(false))
        }
        val joystick = InputUtil.createAttackJoystick(GdxControllerPartCreator(IndexedControllerProvider(2)))

        val robotCreator = RunnableCreator.wrap {

            val visionPacketListener = VisionPacketListener(
                    VisionPacketParser(
                            ObjectMapper(),
                            preciseClock,
                            mapOf(Pair(1, Rotation2.ZERO))
                    ),
                    "tcp://10.134.223.107:5801" // temporary testing address
            )
            visionPacketListener.start()
            val robotRunnable = BasicRobotRunnable(AdvancedIterativeRobotBasicRobot(Robot(
                    data.driverStation, PrintStreamFrcLogger(System.err, System.err), preciseClock,
                    shuffleboardMap,
                    controller, joystick, DisconnectedRumble.getInstance(),
                    DefaultOrientationHandler(EntityOrientation(entity)),
                    swerveDriveData,
                    intake, turret, DummyBallShooter(reportMap), DummyWheelSpinner(reportMap),
                    DummyClimber(reportMap),
                    VisionProvider2020(VisionFilterMultiplexer(listOf(VisionTypeFilter(VisionType2020.POWER_PORT), EntityRangeVisionFilter(entity, 3.0))), entity, preciseClock)
//                    visionPacketListener
            )), data.driverStation)
            RobotRunnableMultiplexer(
                    listOf(robotRunnable, object : RobotRunnable {
                        override fun run() {
                            dashboardBundle.update()
                            driverStationActiveComponent.update()
                        }
                        override fun close() {
                            dashboardBundle.onRemove()
                            driverStationActiveComponent.onRemove()
                            networkTable.stopServer()
                            visionPacketListener.close()
                        }
                    })
            )
        }
        return UpdateableMultiplexer(listOf(
                entity,
                RobotUpdateable(robotCreator),
                intakeListener
        ))
    }

}
class MySupplementaryRobotCreator(
        private val dashboardBundle: ActiveDashboardBundle,
        private val serverName: String
) : RobotCreator {
    override fun create(data: RobotCreator.Data, updateableData: UpdateableCreator.Data): Updateable {
        val entity = createEntity(data, updateableData)
//        val swerveDriveData = createSwerveDriveData(data, updateableData, entity)

        val robotCreator = RunnableCreator.wrap {
            val networkTable = NetworkTableInstance.getDefault()
            networkTable.startClient(serverName)
            object : RobotRunnable {
                override fun run() {
                    dashboardBundle.update()
                    val positionDashboard = dashboardBundle.rootDashboard.getSubDashboard("Absolute Position")
                    val position = Vector2(
                            positionDashboard["x"].getter.getDouble(0.0),
                            positionDashboard["y"].getter.getDouble(0.0)
                    )
                    val orientationRadians = positionDashboard["orientationRadians"].getter.getDouble(0.0)
                    entity.simVector = position
                    entity.rotationRadians = orientationRadians.toFloat()
                }

                override fun close() {
                    dashboardBundle.onRemove()
                    networkTable.stopClient()
                }

            }
        }
        val packetSender = ZMQPacketSender.create(ObjectMapper(), "tcp://$serverName:5808")
        val packetQueueMaster = PacketQueueMaster(ZMQPacketQueue.create(ObjectMapper(), "tcp://$serverName:5809"), true)
        updateableData.uiStage.addListener(createClickListener(updateableData, 0) { _, x, y ->
            val count = tapCount
            if (count == 2) {
                packetSender.send(AbsolutePositionPacket(Vector2(x, y)))
            }
        })
        updateableData.uiStage.addListener(createClickListener(updateableData, 1) { _, x, y ->
            val count = tapCount
            if(count == 2){
                packetSender.send(PerspectiveLocationPacket(Vector2(x, y)))
            }
        })
        return UpdateableMultiplexer(listOf(
                entity,
                RobotUpdateable(robotCreator),
                PacketQueueSoundReceiver(GdxSoundCreator { Gdx.files.internal(it) }, packetQueueMaster.create()),
                object : Updateable {
                    override fun update(delta: Float) {}
                    override fun close() {
                        packetSender.close()
                        packetQueueMaster.close()
                    }
                }
        ))
    }
    private inline fun createClickListener(updateableData: UpdateableCreator.Data, button: Int, crossinline clicked: ClickListener.(event: InputEvent, x: Double, y: Double) -> Unit): ClickListener {
        return object : ClickListener(button){
            val temp1 = Vector3()
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                temp1.set(x, y, 0f)
                updateableData.uiStage.viewport.project(temp1) // now temp1 is in screen coordinates
                updateableData.contentStage.viewport.unproject(temp1) // temp1 is now in world coordinates
                temp1.y *= -1
                val absoluteX = temp1.x.toDouble()
                val absoluteY = temp1.y.toDouble()
                clicked(this, event, absoluteX, absoluteY)
            }
        }
    }

}
