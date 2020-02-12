package com.first1444.frc.robot2020.gdx

import com.badlogic.gdx.scenes.scene2d.ui.*
import com.first1444.frc.robot2020.subsystems.balltrack.BallTracker
import com.first1444.frc.robot2020.subsystems.implementations.DummyIntake
import com.first1444.frc.util.reportmap.ReportMap
import com.first1444.sim.api.Clock
import com.first1444.sim.api.EnabledState
import com.first1444.sim.gdx.Updateable
import com.first1444.sim.gdx.WorldManager
import com.first1444.sim.gdx.clickDownListener
import com.first1444.sim.gdx.init.UpdateableCreator
import java.text.DecimalFormat
import kotlin.math.max

private const val SPACE_BETWEEN = .25
private const val INDEXER_SPACE = .67
private const val FEEDER_SPACE = .33
private const val INDEXER_SPEED = 1.0
private const val FEEDER_SPEED = 2.0
private val FORMAT = DecimalFormat("0.000")

class IntakeBall {
    /** A number between 0 and 2. 0 to 1 is in indexer, 1 to 2 is in feeder*/
    var position: Double = 0.0

    override fun toString(): String {
        return "Ball(position=${FORMAT.format(position)})"
    }
}

class GdxIntake(
        reportMap: ReportMap,
        private val enabledState: EnabledState,
        private val clock: Clock,
        private val ballTracker: BallTracker,
        private val shootBall: () -> Boolean
) : DummyIntake(reportMap) {
    private var lastTimestamp: Double? = null
    private val mutableBalls = mutableListOf<IntakeBall>()

    fun onIntakeBall(){
        ballTracker.addBall()
        mutableBalls.add(IntakeBall())
    }
    fun resetBalls(){
        ballTracker.ballCount = 0
        mutableBalls.clear()
    }
    fun addBall(){
        mutableBalls.add(IntakeBall())
        ballTracker.ballCount = mutableBalls.size
    }
    fun removeBall(){
        if(mutableBalls.isNotEmpty()) {
            mutableBalls.removeAt(mutableBalls.size - 1)
        }
    }
    val balls: List<IntakeBall>
        get() = mutableBalls

    override fun run() {
        val timestamp = clock.timeSeconds
        val lastTimestamp = lastTimestamp
        this.lastTimestamp = timestamp
        if(lastTimestamp != null && enabledState.isEnabled){
            val delta = timestamp - lastTimestamp
            val iterator = mutableBalls.iterator()
            var lastPosition: Double? = null
            while(iterator.hasNext()){
                val ball = iterator.next()
                var position = ball.position
                position += delta * (if(position < INDEXER_SPACE) INDEXER_SPEED * indexerSpeed else FEEDER_SPEED * feederSpeed)
                if(position >= INDEXER_SPACE + FEEDER_SPACE){
                    position = INDEXER_SPACE + FEEDER_SPACE
                    if(shootBall()){
                        iterator.remove()
                    }
                }
                if(lastPosition != null && position > lastPosition - SPACE_BETWEEN){
                    position = max(0.0, lastPosition - SPACE_BETWEEN)
                }
                ball.position = position
                lastPosition = position
            }
        }
        super.run() // sets indexerSpeed and feederSpeed to 0, so we have to call super.run() after we use them
    }
}
class GdxBallRenderUpdateable(
        private val intake: GdxIntake,
        private val updateableData: UpdateableCreator.Data
) : Updateable {

    private val table: Table = Table()
    private val textLabel: Label = Label("ERROR", updateableData.uiSkin)
    private val resetButton: Button = TextButton("Reset Balls", updateableData.uiSkin)
    private val addButton: Button = TextButton("Add", updateableData.uiSkin)
    private val removeButton: Button = TextButton("Remove", updateableData.uiSkin)
    private val setToThree: Button = TextButton("Set to 3", updateableData.uiSkin)
    private val ballPositionLabel = Label("ERROR", updateableData.uiSkin)

    init {
        table.setFillParent(true)
        table.center().right()
        table.add(textLabel)
        table.row()
        table.add(resetButton)
        table.row()
        table.add(Table().apply {
            add(addButton)
            add(removeButton)
        })
        table.row()
        table.add(setToThree)
        table.row()
        table.add(ballPositionLabel)

        resetButton.addListener(clickDownListener {
            intake.resetBalls()
        })
        addButton.addListener(clickDownListener {
            intake.addBall()
        })
        removeButton.addListener(clickDownListener {
            intake.removeBall()
        })
        setToThree.addListener(clickDownListener {
            intake.resetBalls()
            intake.addBall()
            intake.addBall()
            intake.addBall()
        })
    }

    override fun update(delta: Float) {
        updateableData.uiStage.addActor(table)
        textLabel.setText("Ball Count: " + intake.balls.size)
        val builder = StringBuilder()
        val balls = intake.balls
        for(ball in balls){
            builder.append("$ball\n")
        }
        for(i in 1..(5 - balls.size)){
            builder.append('\n')
        }
        ballPositionLabel.setText(builder.toString())
    }

    override fun close() {
    }
}
