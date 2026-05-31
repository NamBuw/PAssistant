package com.avis.app.ptalk.ui.component.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avis.app.ptalk.LocalAppColors

/** Visual variants for `PChip`. */
enum class PChipVariant { Success, Warning, Error, Info, Neutral, Brand }

/**
 * Compact pill-shaped chip used for statuses, badges and tags.
 *
 * Tone is automatically derived from the variant; the pill uses a soft
 * tinted background plus a same-hue border for clarity in both light
 * and dark themes.
 */
@Composable
fun PChip(
    text: String,
    variant: PChipVariant = PChipVariant.Neutral,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val colors = LocalAppColors.current
    val (fg, bg) = chipColors(variant, colors.isDark, colors)

    Row(
        modifier = modifier
            .background(bg, RoundedCornerShape(999.dp))
            .border(1.dp, fg.copy(alpha = 0.18f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = text,
            color = fg,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

private fun chipColors(
    variant: PChipVariant,
    isDark: Boolean,
    colors: com.avis.app.ptalk.ui.theme.AppColors
): Pair<Color, Color> = when (variant) {
    PChipVariant.Success -> colors.success to colors.success.copy(alpha = if (isDark) 0.18f else 0.12f)
    PChipVariant.Warning -> colors.warning to colors.warning.copy(alpha = if (isDark) 0.18f else 0.12f)
    PChipVariant.Error -> colors.error to colors.error.copy(alpha = if (isDark) 0.18f else 0.12f)
    PChipVariant.Info -> colors.accent to colors.accent.copy(alpha = if (isDark) 0.18f else 0.12f)
    PChipVariant.Brand -> colors.primary to colors.primary.copy(alpha = if (isDark) 0.18f else 0.12f)
    PChipVariant.Neutral -> colors.textSecondary to colors.surfaceVariant
}
