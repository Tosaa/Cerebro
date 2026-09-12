package redtoss.creativity.cerebro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import redtoss.creativity.cerebro.ui.layouts.Spacing
import redtoss.creativity.cerebro.ui.theme2.CosyAppTheme

/**
 * Placeholder. Nothing here is implemented yet, and unlike Settings it is not clear this
 * screen should exist at all — the menu item shipped before the idea behind it did.
 *
 * Rough ideas, recorded rather than built:
 *
 *  - Decide what "all" even means. Today every one of the 60 bundled strategies is
 *    already free and offline, so there is currently nothing locked to unlock. Some
 *    candidate meanings:
 *      * Extra strategy packs beyond the six bundled categories, sold per pack.
 *      * A cap on custom strategies for free users, lifted by purchase. Cheapest to
 *        build, but it charges for the user's own content, which reads badly.
 *      * Cosmetic unlocks — extra themes, alternative type — which fits the app's
 *        character better than gating the strategies themselves.
 *  - One-time purchase vs. subscription. A reference app with no running costs has a
 *    weak case for a subscription; a single unlock fits better.
 *  - Restore purchases, which is mandatory for store review, not optional.
 *  - Play Billing as the delivery mechanism, plus entitlement caching so a paid user is
 *    not locked out offline.
 *
 * Worth being blunt about the cost: Cerebro today has no INTERNET permission, no
 * accounts, no analytics and no backend. Any of the above is an architectural change
 * and a privacy-posture change, not a feature toggle. That is the decision to make
 * before writing a line of it.
 */
@Composable
fun UnlockAllScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.Medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.Small),
    ) {
        Text(
            text = "Unlock all",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "Every strategy is already unlocked. There is nothing to buy.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@PreviewLightDark
@Composable
private fun UnlockAllScreenPreview() = CosyAppTheme { UnlockAllScreen() }
