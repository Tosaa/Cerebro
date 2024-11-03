package redtoss.creativity.cerebro.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import redtoss.creativity.cerebro.data.Category
import redtoss.creativity.cerebro.data.Strategy
import redtoss.creativity.cerebro.data.StrategyEditorState
import redtoss.creativity.cerebro.ui.layouts.cards.StrategyCard


@Composable
internal fun StrategyScreen(strategy: Strategy) {
    StrategyCard(strategy, Modifier.padding(8.dp))
}

@Composable
internal fun StrategyEditorScreen(onStrategyFinished: (Strategy) -> Unit) {
    val editorState = remember { mutableStateOf(StrategyEditorState()) }
    val selectedEditorStep = remember { mutableStateOf(1) }
    val finalStrategyPreview = remember { mutableStateOf<Strategy?>(null) }
    val finalStrategyPreviewValue = finalStrategyPreview.value
    if (finalStrategyPreviewValue != null) {
        Column(Modifier.padding(horizontal = 8.dp)) {
            StrategyCard(finalStrategyPreviewValue)
            Spacer(Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button({ finalStrategyPreview.value = null }) { Text("Edit") }
                Button({ onStrategyFinished(finalStrategyPreviewValue) }) { Text("Finished") }
            }
        }
    } else {
        Column(Modifier.padding(horizontal = 8.dp)) {
            TabRow(selectedEditorStep.value, indicator = {}, divider = {}) {
                (1..4).forEach { stepId ->
                    val isStepSelected = selectedEditorStep.value == stepId
                    val isStepCompleted = when (stepId) {
                        1 -> !editorState.value.newTitle.value.isNullOrBlank()
                        2 -> !editorState.value.newShortDescription.value.isNullOrBlank()
                        3 -> !editorState.value.newLongDescription.value.isNullOrBlank()
                        4 -> editorState.value.newCategory.value != null
                        else -> false
                    }
                    Tab(isStepSelected, { selectedEditorStep.value = stepId }) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isStepCompleted && !isStepSelected -> MaterialTheme.colorScheme.tertiaryContainer
                                    isStepSelected -> MaterialTheme.colorScheme.primaryContainer
                                    else -> Color.Transparent
                                },
                                contentColor = when {
                                    isStepCompleted && !isStepSelected -> MaterialTheme.colorScheme.onTertiaryContainer
                                    isStepSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                            )
                        ) {
                            Column {
                                Text("$stepId", modifier = Modifier.padding(horizontal = 8.dp), textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
            Column(Modifier.height(IntrinsicSize.Max)) {
                when (selectedEditorStep.value) {
                    1 -> TitleStep(1, editorState.value)
                    2 -> ShortDescriptionStep(2, editorState.value)
                    3 -> LongDescriptionStep(3, editorState.value)
                    4 -> CategoryStep(4, editorState.value)
                }
            }
            Spacer(Modifier.height(32.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { Button({ finalStrategyPreview.value = editorState.value.buildStrategy() }, enabled = editorState.value.buildStrategy() != null) { Text("Preview") } }
        }
    }
}

@Composable
fun TitleStep(index: Int, editorState: StrategyEditorState) {
    Text("Step $index: Title")
    TextField(editorState.newTitle.value ?: "", { editorState.newTitle.value = it }, modifier = Modifier.fillMaxWidth())
}

@Composable
fun ShortDescriptionStep(index: Int, editorState: StrategyEditorState) {
    Text("Step $index: ShortDescription")
    TextField(editorState.newShortDescription.value ?: "", { editorState.newShortDescription.value = it }, modifier = Modifier.fillMaxWidth())
}

@Composable
fun LongDescriptionStep(index: Int, editorState: StrategyEditorState) {
    Text("Step $index: LongDescription")
    TextField(editorState.newLongDescription.value ?: "", { editorState.newLongDescription.value = it }, modifier = Modifier.fillMaxWidth())
}

@Composable
fun CategoryStep(index: Int, editorState: StrategyEditorState) {
    Text("Step $index: Category")
    val expanded = remember { mutableStateOf(false) }
    Button({ expanded.value = !expanded.value }) {
        Icon(Icons.Default.ArrowDropDown, "Dropdown icon")
        Text(editorState.newCategory.value?.title ?: "Category")
    }
    DropdownMenu(expanded.value, { expanded.value = false }) {
        Category.entries.forEach {
            DropdownMenuItem({ Text(it.title) }, {
                editorState.newCategory.value = it
                expanded.value = false
            })
        }
    }
}

private val Int.stepName: String
    get() = when(this){
        1 -> "Title"
        2 -> "Short Description"
        3 -> "Long Description"
        4 -> "Category"
        else -> "Unknown"
    }