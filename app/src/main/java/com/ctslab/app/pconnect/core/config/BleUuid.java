package com.avis.app.ptalk.core.config;

import java.util.Locale;
import java.util.UUID;

public class BleUuid {
    private static UUID u16(int value) {
        return UUID.fromString(String.format(Locale.US, "0000%04X-0000-1000-8000-00805F9B34FB", value));
    }

    public static final UUID SVC_CONFIG = u16(0xFF01);
    public static final UUID CHR_DEVICE_NAME = u16(0xFF02);
    public static final UUID CHR_VOLUME = u16(0xFF03);
    public static final UUID CHR_BRIGHTNESS = u16(0xFF04);
    public static final UUID CHR_WIFI_SSID = u16(0xFF05);
    public static final UUID CHR_WIFI_PASS = u16(0xFF06);
    public static final UUID CHR_APP_VERSION = u16(0xFF07);
    public static final UUID CHR_BUILD_INFO = u16(0xFF08);
    public static final UUID CHR_SAVE_CMD = u16(0xFF09);
    public static final UUID CHR_DEVICE_ID = u16(0xFF0A);
    public static final UUID CHR_WIFI_LIST = u16(0xFF0B);
}