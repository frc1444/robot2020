package com.first1444.frc.robot2020.actions.positioning;

import com.first1444.dashboard.BasicDashboard;
import com.first1444.frc.robot2020.DashboardMap;
import com.first1444.frc.robot2020.vision.VisionInstant;
import com.first1444.frc.robot2020.vision.VisionProvider;
import com.first1444.sim.api.Clock;
import me.retrodaredevil.action.SimpleAction;

import static java.util.Objects.requireNonNull;

public class SurroundingDashboardLoggerAction extends SimpleAction {
    public static final String SURROUNDING_DEBUG_DASHBOARD = "Surrounding Debug";
    public static final String SURROUNDING_COUNT_KEY = "Surrounding Count";
    /** The key used to retrieve a double or boolean. If double, represents time difference. If boolean, will be false. Booleans represent no surroundings*/
    public static final String SURROUNDING_TIME_DIFFERENCE_KEY = "Time Difference";
    private final Clock clock;
    private final VisionProvider visionProvider;
    private final BasicDashboard debugDashboard;
    public SurroundingDashboardLoggerAction(Clock clock, VisionProvider visionProvider, DashboardMap dashboardMap) {
        super(true);
        this.clock = clock;
        this.visionProvider = requireNonNull(visionProvider);
        this.debugDashboard = dashboardMap.getRawBundle().getRootDashboard().getSubDashboard(SURROUNDING_DEBUG_DASHBOARD);
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        VisionInstant instant = visionProvider.getVisionInstant();
        if(instant != null){
            double timestamp = instant.getTimestamp();
            int size = instant.getSurroundings().size();
            double timeDifference = clock.getTimeSeconds() - timestamp;
            debugDashboard.get(SURROUNDING_TIME_DIFFERENCE_KEY).getForceSetter().setDouble(timeDifference);
            debugDashboard.get(SURROUNDING_COUNT_KEY).getStrictSetter().setNumber(size);
        } else {
            debugDashboard.get(SURROUNDING_COUNT_KEY).getStrictSetter().setNumber(0);
            debugDashboard.get(SURROUNDING_TIME_DIFFERENCE_KEY).getForceSetter().setBoolean(false);
        }
    }
}
