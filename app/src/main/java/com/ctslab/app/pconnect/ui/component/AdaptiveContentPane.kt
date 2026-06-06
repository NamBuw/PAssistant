package com.ctslab.app.pconnect.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Pure layout helper. On wide screens (tablets) it caps the content width and
 * centers it; on phones it stays full-width with horizontal padding. No logic.
 */
@Composable
fun AdaptiveContentPane(
    modifier: Modifier = Modifier,
    maxContentWidth: Dp = 600.dp,
    horizontalPadding: Dp = 24.dp,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}
