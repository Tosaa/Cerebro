package redtoss.creativity.cerebro.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
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
import redtoss.creativity.cerebro.ui.layouts.cards.StrategyCard
import redtoss.creativity.cerebro.ui.theme.CosyAppTheme
import redtoss.creativity.cerebro.ui.theme.Spacing

private const val EDITOR_STEP_COUNT = 4
private const val LONG_DESCRIPTION_MIN_LINES = 5
private const val STEP_COLOR_DURATION_MILLIS = 200

@Suppress("MagicNumber")
@Composable
internal fun StrategyEditorScreen(onStrategyFinished: (Strategy) -> Unit) {
    val editorState = rememberSaveable(saver = StrategyEditorState.Saver) { StrategyEditorState() }
    val selectedEditorStep = rememberSaveable { mutableIntStateOf(1) }
    val finalStrategyPreview = remember { mutableStateOf<Strategy?>(null) }

    AnimatedContent(
        targetState = finalStrategyPreview.value,
        transitionSpec = {
            val forwards = targetState != null
            if (forwards) {
                ForwardEnter togetherWith ForwardExit
            } else {
                BackEnter togetherWith BackExit
            } using SizeTransform(clip = false)
        },
        label = "Strategy editor preview",
    ) { previewedStrategy ->
        if (previewedStrategy == null) {
            StrategyEditorSteps(
                editorState = editorState,
                selectedEditorStep = selectedEditorStep,
                onPreview = { finalStrategyPreview.value = it },
            )
        } else {
            StrategyEditorPreview(
                strategy = previewedStrategy,
                onEdit = { finalStrategyPreview.value = null },
                onFinished = { onStrategyFinished(previewedStrategy) },
            )
        }
    }
}

@Composable
private fun StrategyEditorPreview(strategy: Strategy, onEdit: () -> Unit, onFinished: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.Small),
        verticalArrangement = Arrangement.spacedBy(Spacing.Large),
    ) {
        StrategyCard(strategy)
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onEdit) { Text("Edit") }
            Button(onFinished) { Text("Finished") }
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun StrategyEditorSteps(
    editorState: StrategyEditorState,
    selectedEditorStep: MutableIntState,
    onPreview: (Strategy) -> Unit,
) {
    val draftStrategy = editorState.buildStrategy()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.Small),
        verticalArrangement = Arrangement.spacedBy(Spacing.Large),
    ) {
        SecondaryTabRow(selectedTabIndex = selectedEditorStep.intValue - 1, indicator = {}, divider = {}) {
            for (stepId in 1..EDITOR_STEP_COUNT) {
                val isStepSelected = selectedEditorStep.intValue == stepId
                val isStepCompleted = editorState.isStepCompleted(stepId)
                // Animated so a step lights up as it is completed instead of snapping.
                val containerColor by animateColorAsState(
                    targetValue = when {
                        isStepSelected -> MaterialTheme.colorScheme.primaryContainer
                        isStepCompleted -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> Color.Transparent
                    },
                    animationSpec = tween(STEP_COLOR_DURATION_MILLIS),
                    label = "Editor step container",
                )
                val contentColor by animateColorAsState(
                    targetValue = when {
                        isStepSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                        isStepCompleted -> MaterialTheme.colorScheme.onTertiaryContainer
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    animationSpec = tween(STEP_COLOR_DURATION_MILLIS),
                    label = "Editor step content",
                )
                Tab(isStepSelected, { selectedEditorStep.intValue = stepId }) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = containerColor,
                            contentColor = contentColor,
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
        AnimatedContent(
            targetState = selectedEditorStep.intValue,
            transitionSpec = {
                if (targetState > initialState) {
                    ForwardEnter togetherWith ForwardExit
                } else {
                    BackEnter togetherWith BackExit
                } using SizeTransform(clip = false)
            },
            label = "Strategy editor step",
        ) { step ->
            Column {
                when (step) {
                    1 -> TitleStep(1, editorState)
                    2 -> ShortDescriptionStep(2, editorState)
                    3 -> LongDescriptionStep(3, editorState)
                    4 -> CategoryStep(4, editorState)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(
                onClick = { draftStrategy?.let(onPreview) },
                enabled = draftStrategy != null,
            ) { Text("Preview") }
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
private fun StrategyEditorScreenPreview() = CosyAppTheme {
    StrategyEditorScreen {}
}
