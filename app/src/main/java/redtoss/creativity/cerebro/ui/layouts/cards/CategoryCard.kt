package redtoss.creativity.cerebro.ui.layouts.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import redtoss.creativity.cerebro.data.Category
import redtoss.creativity.cerebro.ui.theme.CosyAppTheme
import redtoss.creativity.cerebro.ui.theme.Spacing

private val CategoryIconSize = 36.dp

@Composable
fun CategoryCard(
    category: Category,
    strategyCount: Int?,
    modifier: Modifier = Modifier,
    onClicked: () -> Unit,
) {
    Card(onClick = onClicked, modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.Medium, horizontal = Spacing.Small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Small),
        ) {
            Icon(
                painter = category.icon
                    ?.let { painterResource(it) }
                    ?: rememberVectorPainter(Icons.Default.AccountCircle),
                contentDescription = null,
                modifier = Modifier.size(CategoryIconSize),
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = category.title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
            if (strategyCount != null) {
                Text(
                    text = if (strategyCount == 1) "1 strategy" else "$strategyCount strategies",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CategoryCardPreview() = CosyAppTheme {
    CategoryCard(category = Category.DecisionMaking, strategyCount = 11) {}
}
