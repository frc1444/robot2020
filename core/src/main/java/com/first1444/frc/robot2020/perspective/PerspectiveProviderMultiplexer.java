package com.first1444.frc.robot2020.perspective;

import java.util.List;

public class PerspectiveProviderMultiplexer implements PerspectiveProvider {
    private final List<PerspectiveProvider> perspectiveProviderList;

    public PerspectiveProviderMultiplexer(List<PerspectiveProvider> perspectiveProviderList) {
        this.perspectiveProviderList = perspectiveProviderList;
    }

    @Override
    public Perspective getPerspective() {
        for(PerspectiveProvider provider : perspectiveProviderList){
            Perspective perspective = provider.getPerspective();
            if(perspective != null){
                return perspective;
            }
        }
        return null;
    }
}
