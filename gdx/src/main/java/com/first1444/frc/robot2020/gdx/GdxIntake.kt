package com.first1444.frc.robot2020.gdx

import com.first1444.frc.robot2020.subsystems.balltrack.BallTracker
import com.first1444.frc.robot2020.subsystems.implementations.DummyIntake
import com.first1444.frc.util.reportmap.ReportMap
import com.first1444.sim.api.Clock
import com.first1444.sim.api.EnabledState
import kotlin.math.max

private const val SPACE_BETWEEN = .25
private const val INDEXER_SPACE = .67
private const val FEEDER_SPACE = .33
private const val INDEXER_SPEED = 1.0
private const val FEEDER_SPEED = 2.0

private class Ball {
    /** A number between 0 and 2. 0 to 1 is in indexer, 1 to 2 is in feeder*/
    var position: Double = 0.0

    override fun toString(): String {
        return "Ball(position=$position)"
    }

}

class GdxIntake(
        reportMap: ReportMap,
        private val enabledState: EnabledState,
        private val clock: Clock,
        private val shootBall: () -> Boolean
) : DummyIntake(reportMap), BallTracker {
    private var lastTimestamp: Double? = null
    private val balls = mutableListOf<Ball>()

    override fun setBallCount(ballCount: Int) {
        require(ballCount >= 0) { "ballCount < 0! ballCount=$ballCount" }
        while(balls.size > ballCount){
            removeBallBottom()
        }
    }
    override fun getBallCount(): Int {
        return balls.size
    }
    override fun addBall(){
        balls.add(Ball())
        println("Added ball")
    }

    override fun removeBallTop() {
        if(balls.isNotEmpty()){
            balls.removeAt(0)
        }
    }
    override fun removeBallBottom() {
        if(balls.isNotEmpty()){
            balls.removeAt(balls.size - 1)
        }
    }
    override fun removeBall() {
        removeBallTop()
    }

    override fun run() {
        val timestamp = clock.timeSeconds
        val lastTimestamp = lastTimestamp
        this.lastTimestamp = timestamp
        if(lastTimestamp != null && enabledState.isEnabled){
            val delta = timestamp - lastTimestamp
            val iterator = balls.iterator()
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
