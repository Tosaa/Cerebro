package redtoss.creativity.cerebro.ui.layouts.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import redtoss.creativity.cerebro.data.Category

@Composable
fun CategoryCard(category: Category, modifier: Modifier = Modifier, onClicked: () -> Unit) = PreviewCard(category.title, modifier, onClicked)

@Composable
fun PreviewCard(title: String, modifier: Modifier = Modifier, onClicked: () -> Unit) {
    Card(
        modifier
            .fillMaxWidth()
            .clickable { onClicked() },
    ) {
        Column(Modifier.padding(vertical = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, maxLines = 1)
        }
    }
}