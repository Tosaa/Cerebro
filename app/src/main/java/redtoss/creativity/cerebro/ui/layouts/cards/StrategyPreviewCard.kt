package redtoss.creativity.cerebro.ui.layouts.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import redtoss.creativity.cerebro.data.Strategy
import redtoss.creativity.cerebro.ui.layouts.Spacing
import redtoss.creativity.cerebro.ui.sampleStrategy
import redtoss.creativity.cerebro.ui.theme2.CosyAppTheme

private val PreviewIconSize = 20.dp

@Composable
fun StrategyPreviewCard(strategy: Strategy, modifier: Modifier = Modifier, onClicked: () -> Unit) {
    Card(onClick = onClicked, modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(Spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.Small),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
            ) {
                Icon(
                    painter = strategy.category.icon
                        ?.let { painterResource(it) }
                        ?: rememberVectorPainter(Icons.Default.AccountCircle),
                    contentDescription = null,
                    modifier = Modifier.size(PreviewIconSize),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = strategy.title,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                )
            }
            Text(
                text = strategy.shortDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun StrategyPreviewCardPreview() = CosyAppTheme {
    StrategyPreviewCard(sampleStrategy, Modifier.padding(Spacing.Small)) {}
}
