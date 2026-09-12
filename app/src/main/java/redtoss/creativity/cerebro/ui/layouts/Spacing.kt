package redtoss.creativity.cerebro.ui.layouts

import androidx.compose.ui.unit.dp

/**
 * The spacing steps the UI actually uses, named once instead of at every call site.
 *
 * Deliberately small: four steps on a 4dp grid cover every gap in the app. Add a step
 * only when a layout genuinely needs one, not to give an existing value a second name.
 */
object Spacing {
    /** Hairline gaps inside a component, e.g. between an icon and its label. */
    val Tiny = 4.dp

    /** The default gap between sibling elements, and between cards in a list. */
    val Small = 8.dp

    /** Padding inside a card, and the screen's horizontal margin. */
    val Medium = 16.dp

    /** Separation between distinct sections of a screen. */
    val Large = 32.dp
}
