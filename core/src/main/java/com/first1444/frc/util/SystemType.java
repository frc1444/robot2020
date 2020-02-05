package com.first1444.frc.util;

public final class SystemType {
    private SystemType(){ throw new UnsupportedOperationException(); }
    public static boolean isUnixBased(){
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("nux") || osName.contains("nix") || osName.contains("aix") || osName.contains("mac");
    }
}
