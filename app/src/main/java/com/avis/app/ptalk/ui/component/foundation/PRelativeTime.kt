package com.avis.app.ptalk.ui.component.foundation

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Vietnamese-friendly relative-time formatting utilities.
 *
 * Examples (assuming "now"):
 *   - 0–59s    → "Vừa xong"
 *   - <60m     → "5 phút trước"
 *   - <24h     → "3 giờ trước"
 *   - same day → "Hôm nay 14:30"
 *   - yesterday→ "Hôm qua 14:30"
 *   - <7 days  → "Thứ Ba 14:30"
 *   - older    → "12/03 14:30" or "12/03/2024" if other year
 *
 * The parser accepts a flexible set of inputs: ISO-8601 with offset,
 * ISO-8601 local date-time, "yyyy-MM-dd HH:mm:ss", or epoch millis as
 * a numeric string. Returns the original input when parsing fails so
 * the UI never shows blank.
 */
object PRelativeTime {

    private val ISO_LOCAL_FALLBACKS = listOf(
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    )

    private val DAY_OF_WEEK_VI = mapOf(
        java.time.DayOfWeek.MONDAY to "Thứ Hai",
        java.time.DayOfWeek.TUESDAY to "Thứ Ba",
        java.time.DayOfWeek.WEDNESDAY to "Thứ Tư",
        java.time.DayOfWeek.THURSDAY to "Thứ Năm",
        java.time.DayOfWeek.FRIDAY to "Thứ Sáu",
        java.time.DayOfWeek.SATURDAY to "Thứ Bảy",
        java.time.DayOfWeek.SUNDAY to "Chủ Nhật"
    )

    fun format(input: String?, now: Instant = Instant.now()): String {
        if (input.isNullOrBlank()) return ""
        val instant = parse(input) ?: return input

        val zone = ZoneId.systemDefault()
        val whenLdt = instant.atZone(zone).toLocalDateTime()
        val nowLdt = now.atZone(zone).toLocalDateTime()
        val seconds = Duration.between(instant, now).seconds

        return when {
            seconds < 0 -> formatTime(whenLdt)
            seconds < 60 -> "Vừa xong"
            seconds < 3600 -> "${seconds / 60} phút trước"
            seconds < 24 * 3600 && whenLdt.toLocalDate() == nowLdt.toLocalDate() -> {
                if (seconds < 6 * 3600) "${seconds / 3600} giờ trước"
                else "Hôm nay ${formatTime(whenLdt)}"
            }
            isYesterday(whenLdt.toLocalDate(), nowLdt.toLocalDate()) ->
                "Hôm qua ${formatTime(whenLdt)}"
            seconds < 7 * 24 * 3600 ->
                "${DAY_OF_WEEK_VI[whenLdt.dayOfWeek] ?: ""} ${formatTime(whenLdt)}".trim()
            whenLdt.year == nowLdt.year ->
                whenLdt.format(DateTimeFormatter.ofPattern("dd/MM HH:mm", Locale.forLanguageTag("vi")))
            else ->
                whenLdt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("vi")))
        }
    }

    /** Section bucket key used to group items by recency. */
    fun bucket(input: String?, now: Instant = Instant.now()): Bucket {
        if (input.isNullOrBlank()) return Bucket.Older
        val instant = parse(input) ?: return Bucket.Older
        val zone = ZoneId.systemDefault()
        val whenDate = instant.atZone(zone).toLocalDate()
        val today = now.atZone(zone).toLocalDate()
        val daysApart = java.time.temporal.ChronoUnit.DAYS.between(whenDate, today)

        return when {
            daysApart <= 0L -> Bucket.Today
            daysApart == 1L -> Bucket.Yesterday
            daysApart < 7L -> Bucket.ThisWeek
            daysApart < 30L -> Bucket.ThisMonth
            else -> Bucket.Older
        }
    }

    enum class Bucket(val label: String) {
        Today("Hôm nay"),
        Yesterday("Hôm qua"),
        ThisWeek("Tuần này"),
        ThisMonth("Tháng này"),
        Older("Cũ hơn")
    }

    private fun parse(raw: String): Instant? {
        // Numeric epoch millis
        raw.toLongOrNull()?.let { return Instant.ofEpochMilli(it) }

        // Try ISO offset first (preserves timezone)
        runCatching {
            return OffsetDateTime.parse(raw, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()
        }

        // ISO with trailing 'Z'
        if (raw.endsWith("Z")) {
            runCatching { return Instant.parse(raw) }
        }

        // Other patterns assumed to be in system zone
        for (fmt in ISO_LOCAL_FALLBACKS) {
            runCatching {
                return LocalDateTime.parse(raw, fmt)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            }
        }
        return null
    }

    private fun formatTime(ldt: LocalDateTime): String =
        ldt.format(DateTimeFormatter.ofPattern("HH:mm"))

    private fun isYesterday(target: LocalDate, today: LocalDate): Boolean =
        java.time.temporal.ChronoUnit.DAYS.between(target, today) == 1L
}
