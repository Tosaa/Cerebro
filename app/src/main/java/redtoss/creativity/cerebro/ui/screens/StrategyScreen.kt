package redtoss.creativity.cerebro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import redtoss.creativity.cerebro.data.Category
import redtoss.creativity.cerebro.data.Strategy
import redtoss.creativity.cerebro.data.StrategyEditorState
import redtoss.creativity.cerebro.ui.layouts.Spacing
import redtoss.creativity.cerebro.ui.layouts.cards.StrategyCard
import redtoss.creativity.cerebro.ui.sampleStrategy
import redtoss.creativity.cerebro.ui.theme2.CosyAppTheme

private const val EDITOR_STEP_COUNT = 4
private const val LONG_DESCRIPTION_MIN_LINES = 5

@Composable
internal fun StrategyScreen(strategy: Strategy) {
    // Scrollable: long descriptions run past the bottom of the screen otherwise, with
    // no way to reach the rest of the text.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.Small),
    ) {
        StrategyCard(strategy)
    }
}

@Suppress("MagicNumber")
@Composable
internal fun StrategyEditorScreen(onStrategyFinished: (Strategy) -> Unit) {
    // rememberSaveable: a rotation used to wipe a half-written strategy outright.
    val editorState = rememberSaveable(saver = StrategyEditorState.Saver) { StrategyEditorState() }
    val selectedEditorStep = rememberSaveable { mutableIntStateOf(1) }
    val finalStrategyPreview = remember { mutableStateOf<Strategy?>(null) }
    val finalStrategyPreviewValue = finalStrategyPreview.value

    if (finalStrategyPreviewValue != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.Small),
            verticalArrangement = Arrangement.spacedBy(Spacing.Large),
        ) {
            StrategyCard(finalStrategyPreviewValue)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button({ finalStrategyPreview.value = null }) { Text("Edit") }
                Button({ onStrategyFinished(finalStrategyPreviewValue) }) { Text("Finished") }
            }
        }
    } else {
        val draftStrategy = editorState.buildStrategy()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.Small),
        ) {
            // selectedTabIndex is 0-based while the steps are numbered from 1.
            SecondaryTabRow(selectedTabIndex = selectedEditorStep.intValue - 1, indicator = {}, divider = {}) {
                for (stepId in 1..EDITOR_STEP_COUNT) {
                    val isStepSelected = selectedEditorStep.intValue == stepId
                    val isStepCompleted = editorState.isStepCompleted(stepId)
                    Tab(isStepSelected, { selectedEditorStep.intValue = stepId }) {
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
                            ),
                        ) {
                            Text(
                                text = "$stepId",
                                modifier = Modifier.padding(horizontal = Spacing.Small),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(Spacing.Large))
            when (selectedEditorStep.intValue) {
                1 -> TitleStep(1, editorState)
                2 -> ShortDescriptionStep(2, editorState)
                3 -> LongDescriptionStep(3, editorState)
                4 -> CategoryStep(4, editorState)
            }
            Spacer(Modifier.height(Spacing.Large))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { finalStrategyPreview.value = draftStrategy },
                    enabled = draftStrategy != null,
                ) { Text("Preview") }
            }
        }
    }
}

@Composable
private fun StepHeading(index: Int, title: String) {
    Text(
        text = "Step $index of $EDITOR_STEP_COUNT: $title",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = Spacing.Small),
    )
}

@Composable
fun TitleStep(index: Int, editorState: StrategyEditorState) {
    StepHeading(index, "Title")
    OutlinedTextField(
        value = editorState.newTitle.value.orEmpty(),
        onValueChange = { editorState.newTitle.value = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Title") },
        supportingText = { Text("A short name for the strategy.") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    )
}

@Composable
fun ShortDescriptionStep(index: Int, editorState: StrategyEditorState) {
    StepHeading(index, "Short description")
    OutlinedTextField(
        value = editorState.newShortDescription.value.orEmpty(),
        onValueChange = { editorState.newShortDescription.value = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Short description") },
        supportingText = { Text("One line, shown under the title in lists.") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    )
}

@Composable
fun LongDescriptionStep(index: Int, editorState: StrategyEditorState) {
    StepHeading(index, "Long description")
    OutlinedTextField(
        value = editorState.newLongDescription.value.orEmpty(),
        onValueChange = { editorState.newLongDescription.value = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Long description") },
        supportingText = { Text("The full explanation, shown on the strategy's own screen.") },
        minLines = LONG_DESCRIPTION_MIN_LINES,
    )
}

@Composable
fun CategoryStep(index: Int, editorState: StrategyEditorState) {
    StepHeading(index, "Category")
    val expanded = remember { mutableStateOf(false) }
    Button({ expanded.value = !expanded.value }) {
        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        Text(editorState.newCategory.value?.title ?: "Choose a category")
    }
    DropdownMenu(expanded.value, { expanded.value = false }) {
        Category.entries.forEach { category ->
            DropdownMenuItem({ Text(category.title) }, {
                editorState.newCategory.value = category
                expanded.value = false
            })
        }
    }
}

@Suppress("MagicNumber")
private fun StrategyEditorState.isStepCompleted(stepId: Int): Boolean = when (stepId) {
    1 -> !newTitle.value.isNullOrBlank()
    2 -> !newShortDescription.value.isNullOrBlank()
    3 -> !newLongDescription.value.isNullOrBlank()
    4 -> newCategory.value != null
    else -> false
}

@PreviewLightDark
@Composable
private fun StrategyScreenPreview() = CosyAppTheme {
    StrategyScreen(sampleStrategy)
}

@PreviewLightDark
@Composable
private fun StrategyEditorScreenPreview() = CosyAppTheme {
    StrategyEditorScreen {}
}
