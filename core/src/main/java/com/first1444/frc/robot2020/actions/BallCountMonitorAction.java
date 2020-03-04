package com.first1444.frc.robot2020.actions;

import com.first1444.frc.robot2020.sound.SoundMap;
import com.first1444.frc.robot2020.subsystems.balltrack.BallTracker;
import me.retrodaredevil.action.SimpleAction;

public class BallCountMonitorAction extends SimpleAction {
    private final BallTracker ballTracker;
    private final SoundMap soundMap;

    private int lastBallCount = 0;

    public BallCountMonitorAction(BallTracker ballTracker, SoundMap soundMap) {
        super(true);
        this.ballTracker = ballTracker;
        this.soundMap = soundMap;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        int ballCount = ballTracker.getBallCount();
        if(lastBallCount != ballCount){
            lastBallCount = ballCount;
            switch(ballCount){
                case 0:
                    soundMap.getBallCount0().play();
                    break;
                case 1:
                    soundMap.getBallCount1().play();
                    break;
                case 2:
                    soundMap.getBallCount2().play();
                    break;
                case 3:
                    soundMap.getBallCount3().play();
                    break;
                case 4:
                    soundMap.getBallCount4().play();
                    break;
                case 5:
                    soundMap.getBallCount5().play();
                    break;
            }
        }
    }
}
