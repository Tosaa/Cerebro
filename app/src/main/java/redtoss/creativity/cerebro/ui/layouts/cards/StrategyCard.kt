package redtoss.creativity.cerebro.ui.layouts.cards

import android.text.Editable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun EditableStrategyCard(strategy: Strategy, modifier: Modifier = Modifier, onEditStrategy: (Strategy) -> Unit) = StrategyCard(strategy, modifier) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        IconButton({ onEditStrategy(strategy) }) {
            Icon(Icons.Default.Create, "Edit button", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun StrategyCard(strategy: Strategy, modifier: Modifier = Modifier, cardFooter: @Composable () -> Unit = {}) {
    Card(
        modifier
            .fillMaxWidth(),
    ) {
        Column(Modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(strategy.category.icon?.let { painterResource(it) } ?: rememberVectorPainter(Icons.Default.AccountCircle), "Strategy preview card icon, ${strategy.title}", Modifier.height(30.dp), tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = strategy.title,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1
                )
            }
            FilterChip(true, {}, label = { Text(strategy.category.title, style = MaterialTheme.typography.labelMedium) }, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
            Text(strategy.longDescription, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 8.dp))
            cardFooter()
        }
    }
}