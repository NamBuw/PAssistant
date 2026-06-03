package com.avis.app.ptalk.domain.model;

import com.avis.app.ptalk.domain.define.DeviceConnectionStatus;

public class DeviceState {
    private String label;
    private int lastSeenMinutes;
    private DeviceConnectionStatus status;
    private int rssi = -100; // default
    private int batteryPercent = 100; // default

    public DeviceState(String label, DeviceConnectionStatus status, int lastSeenMinutes) {
        this.label = label;
        this.status = status;
        this.lastSeenMinutes = lastSeenMinutes;
    }

    public DeviceState() {}

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getLastSeenMinutes() {
        return lastSeenMinutes;
    }

    public void setLastSeenMinutes(int lastSeenMinutes) {
        this.lastSeenMinutes = lastSeenMinutes;
    }

    public DeviceConnectionStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceConnectionStatus status) {
        this.status = status;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getBatteryPercent() {
        return batteryPercent;
    }

    public void setBatteryPercent(int batteryPercent) {
        this.batteryPercent = batteryPercent;
    }
}
