package com.first1444.frc.robot2020.perspective;

public class SimpleMutablePerspectiveProvider implements MutablePerspectiveProvider {
    private Perspective perspective;
    @Override
    public void setPerspective(Perspective perspective) {
        this.perspective = perspective;
    }

    @Override
    public Perspective getPerspective() {
        return perspective;
    }
}
