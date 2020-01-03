package com.first1444.frc.util.reportmap;

import com.first1444.dashboard.BasicDashboard;

public class DashboardReportMap implements ReportMap {

    private final BasicDashboard dashboard;

    public DashboardReportMap(BasicDashboard dashboard) {
        this.dashboard = dashboard;
    }


    @Override
    public void report(String key, String value) {
        dashboard.get(key).getForceSetter().setString(value);
    }
}
