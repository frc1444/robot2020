package com.first1444.frc.robot2020.perspective;

import com.first1444.dashboard.BasicDashboard;
import com.first1444.frc.robot2020.DashboardMap;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;

public class PerspectiveHandler implements PerspectiveProvider {
    public static final String PERSPECTIVE_DASHBOARD = "Perspective Data";

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
        System.out.println("Changed perspective location to " + location);
    }
    public void setToDriverStation(){
        perspective = Perspective.DRIVER_STATION;
    }
    private void updateDashboard(){

    }
}
