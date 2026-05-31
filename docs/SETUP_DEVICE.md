# Hướng dẫn Setup thiết bị PTalk thật với PAssistant

Tài liệu này hướng dẫn chi tiết cách kết nối, cấu hình và vận hành thiết bị PTalk thật với ứng dụng PAssistant. Áp dụng cho người mới setup lần đầu cũng như cấu hình lại thiết bị đã dùng.

---

## Mục lục

1. [Thiết bị nào kết nối được với app](#1-thiết-bị-nào-kết-nối-được-với-app)
2. [Spec kỹ thuật mà thiết bị phải đáp ứng](#2-spec-kỹ-thuật-mà-thiết-bị-phải-đáp-ứng)
3. [Checklist trước khi bắt đầu](#3-checklist-trước-khi-bắt-đầu)
4. [Workflow setup chi tiết](#4-workflow-setup-chi-tiết)
   - [Phase A — Đưa thiết bị vào BLE pairing mode](#phase-a--đưa-thiết-bị-vào-ble-pairing-mode)
   - [Phase B — Cài app và đăng nhập](#phase-b--cài-app-và-đăng-nhập)
   - [Phase C — Quét và chọn thiết bị qua BLE](#phase-c--quét-và-chọn-thiết-bị-qua-ble)
   - [Phase D — Cấu hình WiFi, Volume, Brightness](#phase-d--cấu-hình-wifi-volume-brightness)
   - [Phase E — Verify thiết bị online qua MQTT](#phase-e--verify-thiết-bị-online-qua-mqtt)
5. [Smoke test các tính năng sau khi online](#5-smoke-test-các-tính-năng-sau-khi-online)
6. [Troubleshooting](#6-troubleshooting)
7. [Khuyến nghị môi trường](#7-khuyến-nghị-môi-trường)

---

## 1. Thiết bị nào kết nối được với app

App **chỉ** hoạt động với **thiết bị PTalk firmware riêng** do Học viện Công nghệ Bưu chính Viễn thông (PTIT) phát triển. Cụ thể:

- **Có dùng được**:
  - Loa PTalk (assistant tiếng Việt) — bo điều khiển ESP32 hoặc Linux SBC chạy firmware PTalk.
  - KidMentor (loa cho trẻ em) — cùng firmware platform, khác `productSource` ở phía dashboard.
- **KHÔNG dùng được**:
  - Smart speaker thông thường (Echo, Google Home, …).
  - Smart bulb, smart plug, các IoT khác trên thị trường.
  - Bo phần cứng tự build mà không có firmware PTalk (sẽ không advertise đúng BLE service và không nói được giao thức MQTT của hệ thống này).

Lý do app khắt khe: app filter BLE scan theo service UUID cụ thể của firmware PTalk và protocol MQTT topic được hard-code theo đúng convention firmware này.

---

## 2. Spec kỹ thuật mà thiết bị phải đáp ứng

### 2.1 Lớp BLE (giai đoạn provisioning WiFi)

Tham chiếu: `core/config/BleUuid.java`, `core/ble/impl/PTalkBleClient.kt`.

- Thiết bị phải advertise **một custom GATT service** với UUID:
  ```
  0000FF01-0000-1000-8000-00805F9B34FB
  ```
  (16-bit UUID `0xFF01`).
- Service phải có **10 characteristic** với UUID `0xFF02` đến `0xFF0B`:

| UUID | Tên | Operation cần hỗ trợ |
|---|---|---|
| `0xFF02` | Device Name | Read + Write |
| `0xFF03` | Volume (0–100) | Write |
| `0xFF04` | Brightness (0–100) | Write |
| `0xFF05` | WiFi SSID | Write |
| `0xFF06` | WiFi Password | Write |
| `0xFF07` | App Version | Read |
| `0xFF08` | Build Info | Read |
| `0xFF09` | Save Command (write `0x01` để commit) | Write |
| `0xFF0A` | Device ID (UUID) | Read |
| `0xFF0B` | WiFi List (stream từng SSID rồi `"END"`) | Read/Notify |

Nếu thiết bị **không advertise đúng** UUID `0xFF01`, app sẽ không bao giờ thấy thiết bị trên radar (filter cứng `setServiceUuid(BleUuid.SVC_CONFIG)`).

### 2.2 Lớp MQTT (sau khi đã online qua WiFi)

Tham chiếu: `app/build.gradle.kts:53`, `core/mqtt/MqttClient.kt`, `domain/service/DeviceControlService.kt`.

- Broker MQTT v5: `tcp://171.226.10.121:8443` (cleartext, không TLS).
- Credentials: hard-coded trong build (`ptalk` / `ptalk123`).
- `deviceId`:
  - Nếu Room có `deviceId` (UUID đọc từ characteristic `0xFF0A`) → dùng UUID đó.
  - Nếu không, dùng MAC bỏ dấu `:` viết hoa (ví dụ MAC `AA:BB:CC:11:22:33` → deviceId `AABBCC112233`).
- Topic protocol:
  - Thiết bị **publish** trạng thái lên: `devices/{deviceId}/status`
    - Payload JSON: `batteryLevel`, `volume`, `brightness`, `deviceName`, `firmwareVersion`, `wifiSsid`, `wifiRssi`, `connectivityState` (`"ONLINE"`/`"OFFLINE"`), `uptimeSec`.
  - App **publish** lệnh xuống: `devices/{deviceId}/cmd`
    - Action: `request_status`, `set_volume`, `set_brightness`, `set_device_name`, `reboot`, `request_ble_config`, `ota_update`.
- Yêu cầu WiFi: **chỉ băng tần 2.4 GHz** — 5 GHz không được hỗ trợ.

### 2.3 Phần mềm dashboard (optional)

- App gọi `https://dashboard.ctslab.net/api/v1/chat/sessions|messages` để xem lịch sử chat.
- Nếu thiết bị không upload log lên dashboard này, tab Chat trong DeviceDetail sẽ trống nhưng phần điều khiển vẫn hoạt động bình thường.

---

## 3. Checklist trước khi bắt đầu

Verify 6 mục dưới đây trước khi mở app, để tránh debug lòng vòng:

| # | Hạng mục | Cách kiểm tra | Cần đạt |
|---|---|---|---|
| 1 | Firmware đúng | Power on thiết bị, quan sát đèn báo BLE | Đèn BLE đang advertise |
| 2 | WiFi 2.4 GHz | Vào router hoặc test bằng điện thoại check 2 SSID | Có SSID 2.4GHz, biết password chính xác. KHÔNG dùng 5GHz |
| 3 | Mạng routable tới broker | Từ PC nối cùng WiFi: `nc -vz 171.226.10.121 8443` | Connection succeeded |
| 4 | Tài khoản SSO Authentik | Mở `https://auth.ctslab.net/if/user/` đăng nhập | Login OK, có quyền với app `p-assistant` |
| 5 | Điện thoại Android | Settings → About → Android version | API 33+ (Android 13+) |
| 6 | Bluetooth + GPS điện thoại | Quick settings | Bật cả hai |

> Nếu mục 3 fail (firewall doanh nghiệp/trường chặn), thiết bị **provision được qua BLE (Phase A–D)** nhưng sẽ kẹt ở "Đang chờ thiết bị" ở Phase E. Cần xử lý firewall với network admin trước khi tiếp tục.

---

## 4. Workflow setup chi tiết

### Phase A — Đưa thiết bị vào BLE pairing mode

**Trường hợp 1: Thiết bị mới (chưa từng cấu hình)**

- Power on → firmware tự vào pairing mode trong vài giây đầu (specific theo build firmware).
- Đèn báo: thường nháy nhanh hoặc đổi màu so với chế độ vận hành thường.

**Trường hợp 2: Thiết bị đã từng cấu hình, muốn config lại WiFi mới**

Có 3 cách, chọn theo tình huống:

1. **Qua app (recommended nếu thiết bị đang online)**:
   Home → tap device card → ControlScreen → "Chế độ cấu hình BLE".
   App publish MQTT command `request_ble_config`, firmware switch về BLE mode.

2. **Qua app, từ Profile sheet**:
   Home → icon Profile → "Quản lý thiết bị" → nút Xóa device.
   App cũng gửi `request_ble_config` rồi xóa khỏi Room nội bộ.

3. **Reset cứng**:
   Bấm và giữ nút Reset trên thiết bị 5–10 giây (xem datasheet phần cứng). Dùng khi thiết bị offline + 2 cách trên không khả dụng.

**Trường hợp 3: Power-on cycle**

- Một số firmware tự vào pairing mode nếu power-on mà không kết nối được WiFi đã lưu trong N giây.
- Power off → đợi 5 giây → power on lại.

---

### Phase B — Cài app và đăng nhập

1. Build và install APK debug:
   ```sh
   ./gradlew :app:installDebug
   ```
   Hoặc Run từ Android Studio (chọn variant `debug`).

2. Mở app → splash chạy ~2.5s → LoginScreen.

3. Bấm **"Đăng nhập với SSO"** → Chrome Custom Tab mở tới `https://auth.ctslab.net/application/o/authorize/`.

4. Đăng nhập với tài khoản Authentik của bạn → redirect về app qua scheme `app://passistant/callback`.

5. App điều hướng vào Home (lần đầu chưa có device nào, sẽ thấy empty state với CTA "Bắt đầu cấu hình").

> **Lưu ý**: Có một bug đã biết — đăng nhập rồi nhưng mở lại app vẫn vào Login. Đây là do `TokenManager` (legacy) ↔ `OIDCSessionManager` chưa sync trong codebase. Workaround: cứ đăng nhập lại, không ảnh hưởng functionality.

---

### Phase C — Quét và chọn thiết bị qua BLE

1. Home → bấm FAB **"Cấu hình thiết bị"** ở góc phải dưới.

2. Lần đầu, app xin 3 quyền runtime (theo `AndroidManifest.xml:5-12`):
   - `BLUETOOTH_SCAN`
   - `BLUETOOTH_CONNECT`
   - `ACCESS_FINE_LOCATION` (Android yêu cầu cho BLE scan, dù app không dùng vị trí thật)

   Cấp tất cả.

3. Nếu Bluetooth hoặc GPS đang tắt, app hiển thị warning card với nút **"Mở cài đặt"** dẫn thẳng tới đúng trang Settings. Bật, quay lại app — lifecycle observer (`ScanDeviceScreen.kt:106`) sẽ tự refresh state.

4. Bấm **"Quét thiết bị"** ở đáy màn.

5. Đợi 2–5 giây. Thiết bị PTalk hợp lệ sẽ hiện dưới dạng marker "P" màu xanh trên radar.
   - **Gần tâm radar** = signal mạnh (RSSI ≥ -50 dBm).
   - **Xa tâm** = yếu (RSSI ≤ -85 dBm, có thể fail khi connect).
   - Toggle icon góc phải trên → chuyển sang **List view** xem chi tiết RSSI + tên BLE.

6. Tap marker → app stop scan, connect GATT, đọc Device ID (`0xFF0A`), stream WiFi list (`0xFF0B`).

> **Nếu không thấy thiết bị sau 10 giây**:
> - Verify đèn báo BLE đang nháy.
> - Đặt điện thoại cách thiết bị < 1m.
> - Tắt Bluetooth điện thoại 5 giây rồi bật lại.
> - Verify firmware advertise đúng service UUID `0000FF01-...` bằng app **nRF Connect** (Play Store) — search service `FF01`.

---

### Phase D — Cấu hình WiFi, Volume, Brightness

Dialog cấu hình mở ra với 4 phần:

1. **Device ID** — read-only, là UUID đọc từ thiết bị. Verify đúng thiết bị bạn đang cầm.

2. **WiFi SSID**:
   - Dropdown auto-populate từ các SSID mà thiết bị quét được.
   - Nếu danh sách trống → bấm nút Refresh.
   - Hoặc nhập tay (cần thiết cho hidden SSID).

3. **WiFi Password**: nhập, có toggle hiện/ẩn.

4. **Volume** (default 60%) và **Brightness** (default 100%) — kéo slider.

Bấm **"Lưu cấu hình"**. App ghi tuần tự BLE characteristic:

```
WIFI_SSID (0xFF05)
  → WIFI_PASS (0xFF06)
  → VOLUME (0xFF03)
  → BRIGHTNESS (0xFF04)
  → SAVE_CMD = 0x01 (0xFF09)
```

Khi thấy dialog **"Cấu hình thiết bị hoàn tất! Thiết bị sẽ khởi động lại và kết nối WiFi."**, bấm **Hoàn thành**.

Best-effort, app cũng đăng ký device lên 2 endpoint backend:
- `POST {API_BASE_URL}device/create`
- `POST {DASHBOARD_BASE_URL}api/devices/register`

Nếu fail (mạng kém, server tạm xuống) — app vẫn lưu vào Room nội bộ và silent skip, không chặn flow.

---

### Phase E — Verify thiết bị online qua MQTT

1. Thiết bị reboot, connect WiFi, connect broker MQTT. Tổng thời gian thường ~10–30 giây.

2. Sau ~30 giây tổng, mở app → Home thấy device card với tên đã đặt.

3. Tap card → ControlScreen.

4. App tự động:
   - Connect broker MQTT v5 ở `tcp://171.226.10.121:8443` (`ptalk` / `ptalk123`).
   - Subscribe topic `devices/{deviceId}/status`.
   - Publish `devices/{deviceId}/cmd` với payload `{"action":"request_status",...}` lên đến 3 lần × 3 giây.

5. **Nếu thành công**:
   - Status chip "Đang trực tuyến" (xanh).
   - Battery donut chart hiển thị %.
   - Info chips: WiFi SSID + RSSI + Uptime + Firmware.
   - Slider Volume/Brightness được enable.

6. **Nếu kẹt ở "Đang chờ thiết bị"** (orange):
   - MQTT đã connect thành công (từ phía app), nhưng device chưa publish status.
   - Khả năng cao: thiết bị chưa connect được WiFi (sai password? 5GHz?), hoặc chưa connect broker (firewall mạng device đang dùng).
   - Hành động: SSH/serial vào thiết bị nếu có, đọc log firmware xem MQTT có connect được không.

7. **Nếu kẹt ở "Đang kết nối MQTT"** (đỏ):
   - App chưa connect được broker từ phía điện thoại.
   - Test bằng app **Termux**: `nc -vz 171.226.10.121 8443`.
   - Verify `network_security_config.xml` allow cleartext cho `171.226.10.121` (đã có trong codebase).

---

## 5. Smoke test các tính năng sau khi online

| Tính năng | Cách thử | Kỳ vọng |
|---|---|---|
| Volume | Kéo slider Volume → thả ra | Loa thiết bị thay đổi âm lượng ngay |
| Brightness | Kéo slider Brightness → thả | Màn hình thiết bị (nếu có) đổi độ sáng |
| Đổi tên | Icon Edit ở TopAppBar → nhập tên mới → Lưu | Tên cập nhật trong Home + đồng bộ tới thiết bị qua MQTT |
| Reboot | Advanced → "Khởi động lại thiết bị" | Thiết bị reboot, ~30s sau lại ONLINE |
| Reset BLE | Advanced → "Chế độ cấu hình BLE" | Thiết bị về BLE pairing mode, có thể quét lại từ ScanDevice |
| Lịch sử chat | Home → icon chat trên device card → DeviceDetail | Nếu thiết bị đã upload chat lên dashboard, sẽ thấy session list |

---

## 6. Troubleshooting

### 6.1 Radar không thấy thiết bị

**Nguyên nhân thường gặp**:
- Thiết bị không ở pairing mode.
- Firmware advertise sai UUID.
- Bluetooth điện thoại lỗi.

**Hành động**:
1. Reset thiết bị (Phase A).
2. Dùng app **nRF Connect** verify advertise UUID `0xFF01`.
3. Tắt/bật Bluetooth điện thoại 5 giây.
4. Quét lại.

### 6.2 Connect BLE fail (popup "Không thể kết nối")

**Nguyên nhân**:
- RSSI quá yếu.
- Thiết bị đã connect device khác.
- Lỗi Bluetooth tạm thời.

**Hành động**:
1. Lại gần thiết bị < 1m.
2. Restart thiết bị.
3. Tắt/bật Bluetooth điện thoại.
4. Thử connect lại.

### 6.3 WiFi list trống trong dialog

**Nguyên nhân**:
- BLE chưa stable.
- Thiết bị chưa quét xong WiFi.

**Hành động**:
1. Bấm nút Refresh.
2. Đợi 5 giây.
3. Nếu vẫn trống, nhập tay SSID.

### 6.4 Sau cấu hình, thiết bị không lên ONLINE

**Nguyên nhân**:
- Sai password WiFi.
- WiFi 5 GHz (không hỗ trợ).
- Tín hiệu WiFi yếu.
- Mạng device chặn outbound tới `171.226.10.121:8443`.

**Hành động**:
1. Cấu hình lại với password chính xác.
2. Đảm bảo dùng SSID 2.4 GHz.
3. Đặt thiết bị gần router.
4. Liên hệ admin mạng nếu nghi firewall.

### 6.5 Volume slider disabled khi đang online

**Nguyên nhân**: Status `connectivityState != "ONLINE"`.

**Hành động**:
- Đợi MQTT connect (~30s sau khi mở screen).
- Pull-to-refresh nếu có.
- Verify thiết bị đã online thật (qua dashboard hoặc serial log).

### 6.6 Đăng nhập rồi mà mở lại app vẫn vào Login

**Nguyên nhân**: Bug đã biết — `TokenManager` (legacy) và `OIDCSessionManager` chưa sync trong codebase.

**Hành động**: Đăng nhập lại, không ảnh hưởng đến chức năng khác.

### 6.7 Lỗi "Command failed: ${e.message}" hiển thị literal trong log

**Nguyên nhân**: Bug đã biết — string interpolation bị escape sai trong codebase.

**Hành động**: Bỏ qua, không ảnh hưởng logic.

---

## 7. Khuyến nghị môi trường

- **Khoảng cách điện thoại ↔ thiết bị**:
  - Setup BLE: < 1m.
  - Sau khi online qua MQTT: tùy phạm vi WiFi của router.

- **Mạng WiFi cấu hình cho thiết bị**:
  - 2.4 GHz, WPA2 hoặc WPA2/WPA3 mixed.
  - Tín hiệu RSSI ở vị trí thiết bị nên ≥ -70 dBm.

- **Thử lần đầu trong mạng PTIT**: tránh các vấn đề firewall ngoài campus.

- **Nếu test ngoài campus**:
  - VPN vào mạng nội bộ PTIT, hoặc
  - Nhờ admin mở firewall cho client IP của bạn tới `171.226.10.121:8443` (TCP outbound).

- **Test BLE riêng**: dùng app **nRF Connect** (Play Store) trước khi vào PAssistant để verify firmware advertise đúng service UUID.

- **Test MQTT broker**: dùng `mosquitto_sub` từ máy tính:
  ```sh
  mosquitto_sub -h 171.226.10.121 -p 8443 \
    -u ptalk -P ptalk123 \
    -t 'devices/+/status' -V mqttv5
  ```
  Nếu thiết bị đã online, sẽ thấy payload status xuất hiện định kỳ.

---

## Tham chiếu code

| Hạng mục | File |
|---|---|
| BLE service/characteristic UUID | `app/src/main/java/com/avis/app/ptalk/core/config/BleUuid.java` |
| BLE scan/connect | `app/src/main/java/com/avis/app/ptalk/core/ble/impl/PTalkBleClient.kt` |
| MQTT client | `app/src/main/java/com/avis/app/ptalk/core/mqtt/MqttClient.kt` |
| Device control protocol | `app/src/main/java/com/avis/app/ptalk/domain/service/DeviceControlService.kt` |
| Build config (URL, broker, OIDC) | `app/build.gradle.kts:51-58` |
| Permissions | `app/src/main/AndroidManifest.xml:5-12` |
| Network security (cleartext allow) | `app/src/main/res/xml/network_security_config.xml` |

---

## Thông tin liên hệ

**Học viện Công nghệ Bưu chính Viễn thông (PTIT)**
- Website: https://ptit.edu.vn
- Địa chỉ: Km10, Đường Nguyễn Trãi, Q. Hà Đông, TP. Hà Nội

---

*Tài liệu này được tạo dựa trên phân tích codebase PAssistant. Các giá trị URL, broker, credentials có thể thay đổi theo thời gian — kiểm tra lại trong `app/build.gradle.kts` nếu nghi vấn.*
