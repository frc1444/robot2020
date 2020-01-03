package com.first1444.frc.util.reportmap;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class PrintWriterReportMap implements ReportMap, Runnable{
    private final Map<String, String> map = new HashMap<>();
    private final PrintWriter out;

    public PrintWriterReportMap(PrintWriter out) {
        this.out = out;
    }
    public PrintWriterReportMap(PrintStream printStream){
        this(new PrintWriter(printStream));
    }

    @Override
    public void report(String key, String value) {
        map.put(key, value);
    }

    @Override
    public void run() {
        out.println("=== Start Report ===");
        for(Map.Entry<String, String> entry : map.entrySet()){
            out.println(entry.getKey() + ": " + entry.getValue());
        }
        out.println("=== End   Report ===");
        out.println();
    }
}
