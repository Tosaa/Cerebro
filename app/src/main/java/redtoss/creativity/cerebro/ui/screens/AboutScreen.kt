package redtoss.creativity.cerebro.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import redtoss.creativity.cerebro.R

@Composable
fun AboutScreen() {
    Column(Modifier.padding(8.dp)) {
        Text(text = "About", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth())
        Text(text = stringResource(R.string.about_screen_app_description), modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.bodyLarge)
    }
}
