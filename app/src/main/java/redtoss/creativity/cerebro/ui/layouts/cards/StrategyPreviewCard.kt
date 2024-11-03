package redtoss.creativity.cerebro.ui.layouts.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import redtoss.creativity.cerebro.data.Strategy


@Composable
fun StrategyPreviewCard(strategy: Strategy, modifier: Modifier = Modifier, onClicked: () -> Unit) {
    Card(
        modifier
            .fillMaxWidth()
            .clickable { onClicked() },

        ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(IntrinsicSize.Max)) {
                Icon(
                    painter = strategy.category.icon?.let { painterResource(it) } ?: rememberVectorPainter(Icons.Default.AccountCircle),
                    contentDescription = "Strategy preview card icon, ${strategy.title}",
                    modifier = Modifier.height(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = strategy.title,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    maxLines = 2
                )
            }
            Text(text = strategy.shortDescription, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxSize())
        }
    }
}
