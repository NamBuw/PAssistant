# UI Preview & Debug Gallery

Hai cách xem giao diện PAssistant mà **không cần thiết bị PTalk thực**, không cần backend MQTT/Authentik, không cần BLE permission thực tế.

---

## 1) Compose `@Preview` (xem trong Android Studio, không cần emulator)

### Cách dùng
1. Mở bất kỳ file nào trong `ui/screen/...Preview*.kt` trong Android Studio.
2. Bật Preview pane (góc phải trên file Kotlin: icon split right hoặc `View → Tool Windows → Preview`).
3. Mỗi screen có 4–10 preview functions cover:
   - Light + Dark mode.
   - Empty / Loading / Error / Success states.
   - Edge cases (long Vietnamese name, low battery, offline, BT off, GPS off…).

### Files preview
| File | Số preview |
|---|---|
| `ui/screen/auth/SplashScreenPreviews.kt` | 2 |
| `ui/screen/auth/LoginScreenPreviews.kt` | 2 |
| `ui/screen/auth/SignupScreenPreviews.kt` | 5 |
| `ui/screen/config/HomeScreenPreviews.kt` | 6 |
| `ui/screen/config/ControlScreenPreviews.kt` | 6 |
| `ui/screen/config/DeviceDetailScreenPreviews.kt` | 9 |
| `ui/screen/config/ScanDeviceScreenPreviews.kt` | 7 |

### Cấu trúc kỹ thuật
- Mỗi screen có một wrapper `Screen(...)` (gọi VM qua Hilt) + một body `Content(...)` stateless.
- `@Preview` gọi thẳng `*Content(...)` với mock state — không touch Hilt, an toàn.
- Mock data lấy từ `ui/preview/PreviewSeeds.kt` (tiếng Việt thực tế).
- Theme wrapper `PreviewTheme(dark = true/false)` đặt sẵn `MaterialTheme` + `LocalAppColors`.

---

## 2) Debug Gallery (chạy trên emulator/máy thật, debug build)

Một Activity chỉ tồn tại trong **debug build** cho phép xem mọi scenario UI tương tác như app thật (animation, scroll, gesture).

### Cách mở
- **Cách 1 — Long-press**: trên màn Splash, bấm-giữ logo PTIT khoảng 1 giây → Gallery mở.
- **Cách 2 — adb**: 
  ```
  adb shell am start -n com.avis.app.ptalk/.debug.UIGalleryActivity
  ```

### Tính năng Gallery
- Danh sách 24 scenario (auth, home, control, chat history, scan, foundation components).
- Tap row → mở screen thật với mock state.
- Toggle Light ↔ Dark theme bằng icon góc phải header.
- Back button quay về Gallery list.

### Source set debug-only
Tất cả code Gallery nằm trong `app/src/debug/` — Gradle chỉ compile vào debug variant:

```
app/src/debug/
├── AndroidManifest.xml          ← khai báo UIGalleryActivity (manifest merger)
└── java/com/avis/app/ptalk/debug/
    ├── UIGalleryActivity.kt
    ├── UIGalleryNavigation.kt
    ├── UIGalleryScenarios.kt
    └── FoundationsGalleryContent.kt
```

### Verify Gallery KHÔNG có trong release APK
```sh
./gradlew :app:assembleRelease
unzip -p app/build/outputs/apk/release/app-release-unsigned.apk classes*.dex \
  | strings | grep "UIGalleryActivity" || echo "OK: not in release"
```

---

## 3) Mock data — `PreviewSeeds`

File: `app/src/main/java/com/avis/app/ptalk/ui/preview/PreviewSeeds.kt`

Cung cấp 5 nhóm seed data tiếng Việt thực tế:

| Hàm | Trả về |
|---|---|
| `previewDevices(count)` | `List<Device>` — "Loa khách phòng họp", "Loa phòng bé Bin"… |
| `previewChatSessions()` | 8 phiên trải đều bucket Hôm nay/Hôm qua/Tuần này/Tháng này/Cũ hơn |
| `previewChatMessages()` | Hội thoại 12 message thực tế, mix sender, sentiment đa dạng |
| `previewDeviceStatus(scenario)` | `DeviceStatusResponse` cho Online / LowBattery / Offline / Waiting / NoStatus |
| `previewScannedDevices(count)` | 5 BLE device với RSSI -45/-58/-72/-83/-91 (5 mức signal) |

Lưu ý: file này nằm ở `main/` (không phải `debug/`) để Android Studio Preview pane resolve được. Tuy nhiên **không có call site runtime** nào tham chiếu — chỉ `@Preview` annotated functions và `app/src/debug/` Gallery. Verify:
```sh
rg "PreviewSeeds\." app/src/main --glob '!**/preview/**'
```
Kết quả phải rỗng.

---

## 4) Thêm scenario mới

**Bước 1**: thêm seed mới vào `PreviewSeeds.kt`.

**Bước 2**: thêm `@Preview` function trong file `*Preview.kt` tương ứng:
```kotlin
@Preview(name = "Home · case mới", showBackground = true, heightDp = 900)
@Composable
private fun PreviewHomeNewCase() {
    PreviewTheme(dark = false) {
        HomeContent(
            uiState = VMHome.UiState(devices = PreviewSeeds.previewDevices(1)),
            // ...
        )
    }
}
```

**Bước 3** (nếu muốn xem trên emulator): thêm enum vào `GalleryScenario` + entry vào `GalleryItems` + branch vào `renderScenario()`.

---

## 5) Tác động lên production

| Loại | Số lượng | Vào release? |
|---|---|---|
| `app/src/main/.../preview/*` | 3 file (PreviewSeeds, PreviewTheme, DebugGalleryHook) | Có (~5–10KB) — Hook reflection no-op trong release |
| `app/src/main/.../*Previews.kt` | 7 file `@Preview` annotated | **Không** — annotations bị strip ở release |
| `app/src/debug/...` | 4 file (Gallery + manifest) | **Không** — chỉ compile vào debug variant |
| Visual / behavior change ở runtime | 0 | — |
| Dependency mới | 0 | — |

Đã verify: classpath `release-unsigned.apk` không chứa `UIGalleryActivity` ⇒ không bị reverse-engineer được.
