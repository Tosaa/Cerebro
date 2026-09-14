package redtoss.creativity.cerebro.ui.screens

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.unit.IntOffset

private const val SLIDE_DURATION_MILLIS = 300
private const val FADE_IN_DURATION_MILLIS = 210
private const val FADE_IN_DELAY_MILLIS = 90
private const val FADE_OUT_DURATION_MILLIS = 90

// A fraction of the width rather than all of it. A full-width slide makes both screens
// visibly travel at once; a short offset reads as one surface replacing another.
private const val SLIDE_FRACTION = 5

private val slideSpec = tween<IntOffset>(SLIDE_DURATION_MILLIS, easing = FastOutSlowInEasing)

// The outgoing screen is gone within 90ms and the incoming one only starts appearing
// then, so the two are never both half-visible on top of each other.
private val fadeInSpec = tween<Float>(FADE_IN_DURATION_MILLIS, FADE_IN_DELAY_MILLIS, LinearEasing)
private val fadeOutSpec = tween<Float>(FADE_OUT_DURATION_MILLIS, easing = LinearEasing)

private fun enter(fromRight: Boolean): EnterTransition {
    val sign = if (fromRight) 1 else -1
    return slideInHorizontally(slideSpec) { width -> sign * width / SLIDE_FRACTION } + fadeIn(fadeInSpec)
}

private fun exit(toRight: Boolean): ExitTransition {
    val sign = if (toRight) 1 else -1
    return slideOutHorizontally(slideSpec) { width -> sign * width / SLIDE_FRACTION } + fadeOut(fadeOutSpec)
}

/** Going deeper: the new screen arrives from the right, the old one leaves to the left. */
internal val ForwardEnter = enter(fromRight = true)
internal val ForwardExit = exit(toRight = false)

/** Coming back: the reverse, so a screen retraces the path it took getting here. */
internal val BackEnter = enter(fromRight = false)
internal val BackExit = exit(toRight = true)
