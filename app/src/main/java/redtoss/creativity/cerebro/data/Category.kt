package redtoss.creativity.cerebro.data

import kotlinx.serialization.Serializable
import redtoss.creativity.cerebro.R

@Serializable
enum class Category(customTitle: String? = null) {
    Perspective,
    Experimentation,
    Clarity,
    Mindset,
    DecisionMaking("Decision Making"),
    Improvement;

    val title: String = customTitle ?: name

    val icon: Int?
        get() = when (this) {
            Mindset -> R.drawable.brain
            Perspective -> R.drawable.perspective
            Experimentation -> R.drawable.experimentation
            Clarity -> R.drawable.clarity
            DecisionMaking -> R.drawable.decision
            Improvement -> R.drawable.brain
            else -> null
        }
}
