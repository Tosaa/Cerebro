package redtoss.creativity.cerebro.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver

class StrategyEditorState(
    val newTitle: MutableState<String?> = mutableStateOf(null),
    val newShortDescription: MutableState<String?> = mutableStateOf(null),
    val newLongDescription: MutableState<String?> = mutableStateOf(null),
    val newCategory: MutableState<Category?> = mutableStateOf(null),
) {
    /**
     * The strategy under construction, or null while any field is still missing.
     *
     * Blank counts as missing: typing into a field and then clearing it leaves an empty
     * string, which would otherwise build a strategy with an empty title. This matches
     * the step indicator, which has always used `isNullOrBlank`.
     */
    fun buildStrategy(): Strategy? {
        val newTitle = newTitle.value?.takeUnless { it.isBlank() } ?: return null
        val newShortDescription = newShortDescription.value?.takeUnless { it.isBlank() } ?: return null
        val newLongDescription = newLongDescription.value?.takeUnless { it.isBlank() } ?: return null
        val newCategory = newCategory.value ?: return null
        return Strategy(
            category = newCategory,
            title = newTitle,
            shortDescription = newShortDescription,
            longDescription = newLongDescription,
        )
    }

    companion object {
        /**
         * Lets a half-written strategy survive a rotation or process death.
         *
         * `listSaver` requires non-null entries, so an unset field is stored as an empty
         * string. Nothing is lost: blank and unset already mean the same thing here.
         * The category is stored by [Category.name] because the enum is not saveable.
         */
        val Saver: Saver<StrategyEditorState, Any> = listSaver(
            save = {
                listOf(
                    it.newTitle.value.orEmpty(),
                    it.newShortDescription.value.orEmpty(),
                    it.newLongDescription.value.orEmpty(),
                    it.newCategory.value?.name.orEmpty(),
                )
            },
            restore = { saved ->
                StrategyEditorState(
                    newTitle = mutableStateOf(saved[TITLE_INDEX].ifEmpty { null }),
                    newShortDescription = mutableStateOf(saved[SHORT_DESCRIPTION_INDEX].ifEmpty { null }),
                    newLongDescription = mutableStateOf(saved[LONG_DESCRIPTION_INDEX].ifEmpty { null }),
                    // A category name that no longer exists (an enum entry dropped
                    // between app versions) restores as "not chosen yet", not a crash.
                    newCategory = mutableStateOf(
                        Category.entries.firstOrNull { it.name == saved[CATEGORY_INDEX] },
                    ),
                )
            },
        )

        private const val TITLE_INDEX = 0
        private const val SHORT_DESCRIPTION_INDEX = 1
        private const val LONG_DESCRIPTION_INDEX = 2
        private const val CATEGORY_INDEX = 3
    }
}
