package com.first1444.frc.robot2020.perspective;

import com.first1444.dashboard.BasicDashboard;
import com.first1444.dashboard.advanced.implementations.chooser.ChooserSendable;
import com.first1444.dashboard.advanced.implementations.chooser.MutableMappedChooserProvider;
import com.first1444.dashboard.advanced.implementations.chooser.SimpleMappedChooserProvider;
import com.first1444.dashboard.shuffleboard.ComponentMetadataHelper;
import com.first1444.dashboard.shuffleboard.SendableComponent;
import com.first1444.frc.robot2020.DashboardMap;
import com.first1444.sim.api.Rotation2;
import com.first1444.sim.api.Vector2;

import static java.util.Objects.requireNonNull;

public class PerspectiveHandler implements PerspectiveProvider {
    public static final String PERSPECTIVE_DASHBOARD = "Perspective Data";
    public static final String LOCATION_KEY = "Location";
    public static final String IS_LOCATION_USED_KEY = "Location Used";

    private final BasicDashboard perspectiveDashboard;
    private final MutableMappedChooserProvider<DesiredPerspective> desiredPerspectiveChooser;

    private Vector2 perspectiveLocation = null;
    private Perspective perspective = Perspective.DRIVER_STATION;
    @SuppressWarnings("UnusedAssignment")
    private boolean readyToListen = false;

    public PerspectiveHandler(DashboardMap dashboardMap) {
        perspectiveDashboard = dashboardMap.getRawBundle().getRootDashboard().getSubDashboard(PERSPECTIVE_DASHBOARD);

        desiredPerspectiveChooser = new SimpleMappedChooserProvider<>(this::onDesiredPerspectiveChange);
        for(DesiredPerspective desiredPerspective : DesiredPerspective.values()){
            desiredPerspectiveChooser.addOption(desiredPerspective.name, desiredPerspective, desiredPerspective == DesiredPerspective.FIELD_ORIENTED);
        }
        dashboardMap.getUserTab().add(
                "Perspective Chooser",
                new SendableComponent<>(new ChooserSendable(desiredPerspectiveChooser)),
                metadata -> new ComponentMetadataHelper(metadata).setSize(2, 1).setPosition(6, 0)
        );
        update();
        readyToListen = true;
    }
    private void onDesiredPerspectiveChange(String key){
        if(!readyToListen){
            return;
        }
        update();
    }
    @Override
    public Perspective getPerspective() {
        return perspective;
    }

    public void setPerspectiveLocation(Vector2 location){
        perspectiveLocation = location;
        update();
    }
    public void setToPointOriented(){
        desiredPerspectiveChooser.setSelectedKey(DesiredPerspective.POINT_ORIENTED.name);
        update();
    }
    public void setToFieldOriented(){
        desiredPerspectiveChooser.setSelectedKey(DesiredPerspective.FIELD_ORIENTED.name);
        update();
    }
    public void setToFirstPerson(){
        desiredPerspectiveChooser.setSelectedKey(DesiredPerspective.FIRST_PERSON.name);
        update();
    }
    private void update(){
        final Perspective perspective;
        DesiredPerspective desiredPerspective = desiredPerspectiveChooser.getSelected();
        requireNonNull(desiredPerspective);
        if(desiredPerspective == DesiredPerspective.FIRST_PERSON){
            perspective = Perspective.ROBOT_FORWARD_CAM;
        } else if(desiredPerspective == DesiredPerspective.FIELD_ORIENTED){
            perspective = Perspective.DRIVER_STATION;
        } else if(desiredPerspective == DesiredPerspective.POINT_ORIENTED){
            perspective = new Perspective(Rotation2.ZERO, true, perspectiveLocation);
        } else throw new AssertionError("Unknown desired perspective " + desiredPerspective);
        this.perspective = perspective;
        Vector2 location = perspective.getLocation();

        perspectiveDashboard.get(IS_LOCATION_USED_KEY).getStrictSetter().setBoolean(location != null);
        if(location != null){
            perspectiveDashboard.get(LOCATION_KEY).getStrictSetter().setDoubleArray(new double[] { location.getX(), location.getY() });
        } else {
            perspectiveDashboard.get(LOCATION_KEY).getStrictSetter().setDoubleArray(new double[] { 0.0, 0.0 });
        }
    }
    private enum DesiredPerspective {
        FIRST_PERSON("First Person"),
        FIELD_ORIENTED("Field Oriented"),
        POINT_ORIENTED("Point Oriented");
        private final String name;

        DesiredPerspective(String name) {
            this.name = name;
        }
    }
}
