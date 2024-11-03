package redtoss.creativity.cerebro.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class StrategyEditorState(
    val newTitle: MutableState<String?> = mutableStateOf(null),
    val newShortDescription: MutableState<String?> = mutableStateOf(null),
    val newLongDescription: MutableState<String?> = mutableStateOf(null),
    val newCategory: MutableState<Category?> = mutableStateOf(null)
) {
    fun buildStrategy(): Strategy? {
        val newTitle = newTitle.value ?: return null
        val newShortDescription = newShortDescription.value ?: return null
        val newLongDescription = newLongDescription.value ?: return null
        val newCategory = newCategory.value ?: return null
        return Strategy(category = newCategory, title = newTitle, shortDescription = newShortDescription, longDescription = newLongDescription)
    }
}
