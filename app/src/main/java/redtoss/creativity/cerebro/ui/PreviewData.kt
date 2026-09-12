package redtoss.creativity.cerebro.ui

import redtoss.creativity.cerebro.data.Category
import redtoss.creativity.cerebro.data.Strategy

/**
 * Sample data for `@Preview` composables only. Mirrors the shape of the bundled
 * strategies in `assets/strategies/` so previews show realistic text lengths.
 */
internal val sampleStrategy = Strategy(
    category = Category.Perspective,
    title = "Reframe the Question",
    shortDescription = "Rethink the problem's context",
    longDescription = "Challenge your assumptions and rephrase the question to uncover new " +
        "possibilities. The way a problem is stated often smuggles in a solution; stating it " +
        "differently can make a different answer look obvious.",
)

internal val sampleStrategies = listOf(
    sampleStrategy,
    Strategy(
        category = Category.Experimentation,
        title = "Run a Cheap Test",
        shortDescription = "Find the smallest experiment that would change your mind",
        longDescription = "Instead of arguing about which option is better, look for the " +
            "quickest thing you could try that would settle it.",
    ),
    Strategy(
        category = Category.DecisionMaking,
        title = "Consider the Opposite",
        shortDescription = "Argue the case you rejected",
        longDescription = "Take the option you dismissed first and build the strongest case " +
            "for it you can. If the case is weak, you have confirmed your choice.",
    ),
)
