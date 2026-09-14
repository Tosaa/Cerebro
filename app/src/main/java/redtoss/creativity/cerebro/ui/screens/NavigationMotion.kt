package redtoss.creativity.cerebro.ui.screens

import androidx.activity.BackEventCompat
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

// While a back gesture is being dragged the fade is scrubbed by finger position rather
// than played, so the staggered 90ms timing above would empty the screen a third of the
// way through the swipe. These span the whole gesture instead. The geometry is identical
// to the committed transitions, so releasing mid-swipe continues rather than jumps.
private val predictiveFadeSpec = tween<Float>(SLIDE_DURATION_MILLIS, easing = LinearEasing)

private fun predictiveEnter(fromRight: Boolean): EnterTransition {
    val sign = if (fromRight) 1 else -1
    return slideInHorizontally(slideSpec) { width -> sign * width / SLIDE_FRACTION } +
        fadeIn(predictiveFadeSpec)
}

private fun predictiveExit(toRight: Boolean): ExitTransition {
    val sign = if (toRight) 1 else -1
    return slideOutHorizontally(slideSpec) { width -> sign * width / SLIDE_FRACTION } +
        fadeOut(predictiveFadeSpec)
}

/**
 * Navigation Compose keeps the in-progress back gesture on its own pair of transitions,
 * separate from popEnter/popExit, and defaults them to a scaleOut. Left unset, the app
 * shrinks into the middle of the screen while the finger is down no matter what the pop
 * transitions say.
 *
 * The swipe edge decides the direction so the content tracks the finger: dragging from
 * the left edge pushes the screen right, dragging from the right edge pushes it left.
 */
internal fun predictivePopEnter(swipeEdge: Int): EnterTransition =
    predictiveEnter(fromRight = swipeEdge != BackEventCompat.EDGE_LEFT)

internal fun predictivePopExit(swipeEdge: Int): ExitTransition =
    predictiveExit(toRight = swipeEdge == BackEventCompat.EDGE_LEFT)
