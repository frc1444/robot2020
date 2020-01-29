package com.first1444.frc.robot2020.perspective;

public interface PerspectiveProvider {
    /**
     * May be null. However, some receivers of a {@link PerspectiveProvider} may not accept null values. If null values are returned, be careful
     * where you pass this instance to.
     * @return The desired perspective or in some implementations, null.
     */
    Perspective getPerspective();
}
