package com.axreng.backend.utils;

public class ExecutionTime {
    private final long startTime;

    public ExecutionTime() {
        this.startTime = System.nanoTime();
    }

    public String getTimeDurationSecAndMs(){
        long execTimeMs =  (System.nanoTime() - this.startTime) / 1000000;
        long execTimeSec = execTimeMs % 1000;
        execTimeMs /= 1000;

        return execTimeSec + "s" + ":"+execTimeMs+"ms";
    }
}
