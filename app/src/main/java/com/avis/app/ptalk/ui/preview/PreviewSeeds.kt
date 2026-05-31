package com.avis.app.ptalk.ui.preview

import com.avis.app.ptalk.core.ble.ScannedDevice
import com.avis.app.ptalk.core.network.ChatMessageResponse
import com.avis.app.ptalk.core.network.ChatSessionResponse
import com.avis.app.ptalk.core.websocket.DeviceStatusResponse
import com.avis.app.ptalk.domain.model.Device
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Realistic Vietnamese mock data for `@Preview` and the debug Gallery.
 *
 * Important: this file lives in `main/` so Android Studio's Compose
 * Preview can resolve it, but none of these helpers are referenced
 * by runtime code paths — only by @Preview-annotated functions
 * and the debug-only UIGalleryActivity.
 */
object PreviewSeeds {

    // ── Devices (Room entities) ────────────────────────────────────────
    fun previewDevices(count: Int = 3): List<Device> {
        val templates = listOf(
            Triple("Loa khách phòng họp", "AA:BB:CC:11:22:33", "1f7e9d10-2c3a-4b8e-9f12-aa11bb22cc33"),
            Triple("Loa phòng bé Bin", "AA:BB:CC:11:22:34", "2e8c0d11-3d4b-4cae-8a23-bb22cc33dd44"),
            Triple("PTalk - Sảnh chính", "AA:BB:CC:11:22:35", "3f9d1e22-4e5c-4dbf-7b34-cc33dd44ee55"),
            Triple("Loa phòng giáo viên", "AA:BB:CC:11:22:36", "4a0e2f33-5f6d-4ec0-6c45-dd44ee55ff66"),
            Triple("Loa hành lang tầng 2", "AA:BB:CC:11:22:37", null)
        )
        return templates.take(count.coerceIn(0, templates.size)).map { (name, mac, did) ->
            Device(name, mac).apply {
                deviceId = did
                appVersion = "1.4.2"
                buildInfo = "ptalk-build"
            }
        }
    }

    // ── Chat sessions (across all relative-time buckets) ───────────────
    fun previewChatSessions(): List<ChatSessionResponse> {
        val now = Instant.now()
        return listOf(
            session(
                id = "s-001",
                title = "Chuyện kể trước giờ ngủ",
                productSource = "ptalk",
                channel = "voice",
                messageCount = 24,
                avgSentiment = "positive",
                lastMessageAt = now.minus(15, ChronoUnit.MINUTES)
            ),
            session(
                id = "s-002",
                title = "Hỏi đáp về thời tiết hôm nay",
                productSource = "ptalk",
                channel = "voice",
                messageCount = 8,
                avgSentiment = "neutral",
                lastMessageAt = now.minus(3, ChronoUnit.HOURS)
            ),
            session(
                id = "s-003",
                title = "Học bảng cửu chương",
                productSource = "kid_mentor",
                channel = "voice",
                messageCount = 87,
                avgSentiment = "positive",
                lastMessageAt = now.minus(28, ChronoUnit.HOURS)
            ),
            session(
                id = "s-004",
                title = "Trò chuyện cùng Bin về trường lớp",
                productSource = "kid_mentor",
                channel = "voice",
                messageCount = 36,
                avgSentiment = "positive",
                lastMessageAt = now.minus(3, ChronoUnit.DAYS)
            ),
            session(
                id = "s-005",
                title = "Đặt câu hỏi về vũ trụ",
                productSource = "ptalk",
                channel = "voice",
                messageCount = 12,
                avgSentiment = "surprise",
                lastMessageAt = now.minus(5, ChronoUnit.DAYS)
            ),
            session(
                id = "s-006",
                title = null,
                productSource = "kid_mentor",
                channel = "voice",
                messageCount = 4,
                avgSentiment = "neutral",
                lastMessageAt = now.minus(12, ChronoUnit.DAYS)
            ),
            session(
                id = "s-007",
                title = "Kể chuyện cổ tích Tấm Cám",
                productSource = "ptalk",
                channel = "voice",
                messageCount = 56,
                avgSentiment = "positive",
                lastMessageAt = now.minus(20, ChronoUnit.DAYS)
            ),
            session(
                id = "s-008",
                title = "Phiên cũ ít sử dụng",
                productSource = "ptalk",
                channel = "voice",
                messageCount = 3,
                avgSentiment = "neutral",
                lastMessageAt = now.minus(45, ChronoUnit.DAYS)
            )
        )
    }

    // ── Chat messages (a 12-message Vietnamese conversation) ───────────
    fun previewChatMessages(): List<ChatMessageResponse> {
        val today = Instant.now()
        val yesterday = today.minus(28, ChronoUnit.HOURS)
        var t = yesterday
        fun next(seconds: Long): String {
            t = t.plus(seconds, ChronoUnit.SECONDS)
            return iso(t)
        }
        return listOf(
            message("m-01", "user", "Chào trợ lý, hôm nay thời tiết thế nào?", next(0), "neutral"),
            message(
                "m-02", "assistant",
                "Chào bạn! Hôm nay tại Hà Nội trời nhiều mây, nhiệt độ khoảng 24°C. Không có mưa nên bạn có thể ra ngoài thoải mái.",
                next(8), "positive"
            ),
            message("m-03", "user", "Thế tối nay có lạnh không?", next(20), "neutral"),
            message("m-04", "assistant", "Tối nay nhiệt độ giảm xuống 18°C, gió nhẹ. Bạn nên mặc thêm áo khoác mỏng nhé.", next(6), "neutral"),
            // jump to today
            message("m-05", "user", "Cảm ơn nhé! Bin học toán giỏi lắm phải không?", iso(today.minus(3, ChronoUnit.HOURS)), "positive"),
            message(
                "m-06", "assistant",
                "Bin rất chăm chỉ và tiến bộ mỗi ngày. Hôm qua Bin đã học xong bảng cửu chương 7 mà không cần nhắc.",
                iso(today.minus(3, ChronoUnit.HOURS).plusSeconds(10)), "positive"
            ),
            message(
                "m-07", "user",
                "Tuyệt vời! Lát nữa cho Bin nghe thêm chuyện cổ tích trước khi đi ngủ được không?",
                iso(today.minus(2, ChronoUnit.HOURS).minusSeconds(40)), "positive"
            ),
            message(
                "m-08", "assistant",
                "Được ngay. Mình sẽ chuẩn bị một câu chuyện ngắn về cô bé quàng khăn đỏ. Bin sẽ thích lắm.",
                iso(today.minus(2, ChronoUnit.HOURS)), "positive"
            ),
            message(
                "m-09", "assistant",
                "Mình cũng có thêm một vài câu hỏi vui sau câu chuyện để Bin được tương tác nhé.",
                iso(today.minus(2, ChronoUnit.HOURS).plusSeconds(15)), "neutral"
            ),
            message("m-10", "user", "Hay quá! Tối nay 8h nha.", iso(today.minus(50, ChronoUnit.MINUTES)), "positive"),
            message("m-11", "assistant", "Mình đã đặt nhắc. Hẹn Bin lúc 8 giờ tối nay nhé!", iso(today.minus(48, ChronoUnit.MINUTES)), "positive"),
            message("m-12", "user", "OK, cảm ơn rất nhiều!", iso(today.minus(15, ChronoUnit.MINUTES)), "positive")
        )
    }

    // ── Device status (control screen scenarios) ───────────────────────
    enum class StatusScenario { Online, LowBattery, Offline, Waiting, NoStatus }

    fun previewDeviceStatus(scenario: StatusScenario): DeviceStatusResponse? = when (scenario) {
        StatusScenario.Online -> DeviceStatusResponse(
            deviceId = "1F7E9D10",
            status = "ok",
            batteryLevel = 78,
            volume = 60,
            brightness = 80,
            deviceName = "Loa khách phòng họp",
            firmwareVersion = "1.4.2",
            wifiSsid = "PTIT-Office",
            wifiRssi = -52,
            connectivityState = "ONLINE",
            uptimeSec = 48 * 3600 + 17 * 60
        )
        StatusScenario.LowBattery -> DeviceStatusResponse(
            deviceId = "1F7E9D10",
            status = "ok",
            batteryLevel = 12,
            volume = 35,
            brightness = 70,
            deviceName = "Loa khách phòng họp",
            firmwareVersion = "1.4.2",
            wifiSsid = "PTIT-Office",
            wifiRssi = -68,
            connectivityState = "ONLINE",
            uptimeSec = 6 * 3600
        )
        StatusScenario.Offline -> DeviceStatusResponse(
            deviceId = "1F7E9D10",
            connectivityState = "OFFLINE"
        )
        StatusScenario.Waiting -> null   // VM has connected MQTT but device not responded yet
        StatusScenario.NoStatus -> null
    }

    // ── Scanned BLE devices (covers all 5 signal levels) ───────────────
    fun previewScannedDevices(count: Int = 5): List<ScannedDevice> {
        val all = listOf(
            ScannedDevice(address = "AA:BB:CC:11:22:01", name = "PTalk-A1B2", rssi = -45, hasConfigService = true),
            ScannedDevice(address = "AA:BB:CC:11:22:02", name = "PTalk-Speaker-01", rssi = -58, hasConfigService = true),
            ScannedDevice(address = "AA:BB:CC:11:22:03", name = "PTalk-Sảnh", rssi = -72, hasConfigService = true),
            ScannedDevice(address = "AA:BB:CC:11:22:04", name = null, rssi = -83, hasConfigService = true),
            ScannedDevice(address = "AA:BB:CC:11:22:05", name = "PTalk-Hallway", rssi = -91, hasConfigService = true)
        )
        return all.take(count.coerceIn(0, all.size))
    }

    // ── helpers ────────────────────────────────────────────────────────
    private fun session(
        id: String,
        title: String?,
        productSource: String,
        channel: String,
        messageCount: Int,
        avgSentiment: String?,
        lastMessageAt: Instant
    ): ChatSessionResponse = ChatSessionResponse(
        id = id,
        userId = "u-preview",
        deviceId = "1F7E9D10",
        productSource = productSource,
        channel = channel,
        title = title,
        messageCount = messageCount,
        avgSentiment = avgSentiment,
        startedAt = iso(lastMessageAt.minus(messageCount.toLong(), ChronoUnit.MINUTES)),
        lastMessageAt = iso(lastMessageAt),
        userName = "Bùi Vân",
        deviceLabel = "Loa khách phòng họp"
    )

    private fun message(
        id: String,
        sender: String,
        content: String,
        createdAt: String,
        sentiment: String?
    ): ChatMessageResponse = ChatMessageResponse(
        id = id,
        sender = sender,
        messageType = "text",
        content = content,
        audioUrl = null,
        audioDuration = null,
        sentiment = sentiment,
        emotionCode = null,
        createdAt = createdAt
    )

    private fun iso(instant: Instant): String =
        OffsetDateTime.ofInstant(instant, ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}
