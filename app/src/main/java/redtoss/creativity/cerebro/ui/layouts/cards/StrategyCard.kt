package redtoss.creativity.cerebro.ui.layouts.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import redtoss.creativity.cerebro.data.Category
import redtoss.creativity.cerebro.data.Strategy
import redtoss.creativity.cerebro.ui.sampleStrategy
import redtoss.creativity.cerebro.ui.theme2.CosyAppTheme
import redtoss.creativity.cerebro.ui.theme2.Spacing

private val TitleIconSize = 30.dp
private val BadgeIconSize = 16.dp

@Composable
fun EditableStrategyCard(
    strategy: Strategy,
    modifier: Modifier = Modifier,
    onEditStrategy: (Strategy) -> Unit,
) = StrategyCard(strategy, modifier) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        IconButton({ onEditStrategy(strategy) }) {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = "Edit strategy",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun StrategyCard(strategy: Strategy, modifier: Modifier = Modifier, cardFooter: @Composable () -> Unit = {}) {
    Card(modifier.fillMaxWidth()) {
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
                    modifier = Modifier.size(TitleIconSize),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = strategy.title,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            CategoryBadge(strategy.category)
            if (strategy.shortDescription != strategy.longDescription) {
                Text(
                    text = strategy.shortDescription,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = strategy.longDescription,
                style = MaterialTheme.typography.bodyLarge,
            )
            cardFooter()
        }
    }
}

@Composable
private fun CategoryBadge(category: Category) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.Small, vertical = Spacing.Tiny),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Tiny),
        ) {
            category.icon?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = null,
                    modifier = Modifier.size(BadgeIconSize),
                )
            }
            Text(text = category.title, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@PreviewLightDark
@Composable
private fun StrategyCardPreview() = CosyAppTheme {
    StrategyCard(sampleStrategy, Modifier.padding(Spacing.Small))
}
