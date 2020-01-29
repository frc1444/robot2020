package com.first1444.frc.robot2020.perspective;

import com.first1444.dashboard.BasicDashboard;
import com.first1444.frc.robot2020.DashboardMap;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;

public class PerspectiveHandler implements PerspectiveProvider {
    public static final String PERSPECTIVE_DASHBOARD = "Perspective Data";
    public static final String LOCATION_KEY = "Location";
    public static final String IS_LOCATION_USED_KEY = "Location Used";

    private final BasicDashboard dashboard;

    private Perspective perspective = Perspective.DRIVER_STATION;

    public PerspectiveHandler(DashboardMap dashboardMap) {
        dashboard = dashboardMap.getRawBundle().getRootDashboard().getSubDashboard(PERSPECTIVE_DASHBOARD);
    }
    @Override
    public Perspective getPerspective() {
        return perspective;
    }

    public void setToLocation(Vector2 location){
        perspective = new Perspective(Rotation2.ZERO, true, location);
        updateDashboard();
    }
    public void setToDriverStation(){
        perspective = Perspective.DRIVER_STATION;
        updateDashboard();
    }
    private void updateDashboard(){
        Perspective perspective = this.perspective;
        Vector2 location = perspective.getLocation();
        dashboard.get(IS_LOCATION_USED_KEY).getStrictSetter().setBoolean(location != null);
        if(location != null){
            dashboard.get(LOCATION_KEY).getStrictSetter().setDoubleArray(new double[] { location.getX(), location.getY() });
        } else {
            dashboard.get(LOCATION_KEY).getStrictSetter().setDoubleArray(new double[] { 0.0, 0.0 });
        }
    }
}
