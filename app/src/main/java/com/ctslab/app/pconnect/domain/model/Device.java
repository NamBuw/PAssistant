package com.avis.app.ptalk.domain.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "devices")
public class Device {
    @PrimaryKey()
    @NonNull
    private String macAddress;
    private String name;
    private String deviceId = null;  // UUID from BLE characteristic 0xFF0A
    private String appVersion = null;
    private String buildInfo = null;

    public Device(String name, @NonNull String macAddress) {
        this.macAddress = macAddress;
        this.name = name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @NonNull
    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(@NonNull String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getBuildInfo() {
        return buildInfo;
    }

    public void setBuildInfo(String buildInfo) {
        this.buildInfo = buildInfo;
    }
}