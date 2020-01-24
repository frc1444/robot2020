package com.first1444.frc.robot2020.actions.positioning;

import com.first1444.dashboard.BasicDashboard;
import com.first1444.frc.robot2020.DashboardMap;
import com.first1444.sim.api.Clock;
import com.first1444.sim.api.surroundings.Surrounding;
import com.first1444.sim.api.surroundings.SurroundingProvider;
import me.retrodaredevil.action.SimpleAction;

import java.util.List;

public class SurroundingDashboardLoggerAction extends SimpleAction {
    public static final String SURROUNDING_DEBUG_DASHBOARD = "Surrounding Debug";
    public static final String SURROUNDING_COUNT_KEY = "Surrounding Count";
    /** The key used to retreive a double or boolean. If double, represents time difference. If boolean, will be false. Booleans represent no surroundings*/
    public static final String SURROUNDING_TIME_DIFFERENCE_KEY = "Time Difference";
    private final Clock clock;
    private final SurroundingProvider surroundingProvider;
    private final BasicDashboard debugDashboard;
    public SurroundingDashboardLoggerAction(Clock clock, SurroundingProvider surroundingProvider, DashboardMap dashboardMap) {
        super(true);
        this.clock = clock;
        this.surroundingProvider = surroundingProvider;
        this.debugDashboard = dashboardMap.getRawBundle().getRootDashboard().getSubDashboard(SURROUNDING_DEBUG_DASHBOARD);
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        List<Surrounding> surroundings = surroundingProvider.getSurroundings();
        final Double timestamp;
        if(surroundings.isEmpty()){
            timestamp = null;
        } else {
            timestamp = surroundings.get(0).getTimestamp();
        }
        debugDashboard.get(SURROUNDING_COUNT_KEY).getStrictSetter().setNumber(surroundings.size());
        if(timestamp == null){
            debugDashboard.get(SURROUNDING_TIME_DIFFERENCE_KEY).getForceSetter().setBoolean(false);
        } else {
            double timeDifference = clock.getTimeSeconds() - timestamp;
            debugDashboard.get(SURROUNDING_TIME_DIFFERENCE_KEY).getForceSetter().setDouble(timeDifference);
        }
    }
}
