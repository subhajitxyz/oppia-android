package org.oppia.android.app.player.state.itemviewmodel

import java.util.*
import org.oppia.android.app.player.state.listener.ContinueNavigationButtonListener
import org.oppia.android.app.player.state.listener.PreviousNavigationButtonListener
import org.oppia.android.app.recyclerview.BindableItemViewModel
import org.oppia.android.app.recyclerview.StateItemId

/**
 * [StateItemViewModel] for navigating to previous states and continuing to a new state. This differs from
 * [NextButtonViewModel] in that the latter is for navigating to existing states rather than a new state. This differs
 * from [ContinueNavigationButtonViewModel] in that the latter is for the continue interaction whereas this is for
 * navigating past a recently completed state.
 */
class ContinueNavigationButtonViewModel(
  val hasPreviousButton: Boolean,
  val hasConversationView: Boolean,
  val previousNavigationButtonListener: PreviousNavigationButtonListener,
  val continueNavigationButtonListener: ContinueNavigationButtonListener,
  val isSplitView: Boolean,
  val shouldAnimateContinueButton: Boolean,
  val continueButtonAnimationTimestampMs: Long
) : StateItemViewModel(ViewType.CONTINUE_NAVIGATION_BUTTON), BindableItemViewModel {

  private val uniqueId: String = UUID.randomUUID().toString()
  override val contentId: StateItemId
    get() = StateItemId.ContinueNavigationButton(uniqueId)

  override fun hasChanges(other: BindableItemViewModel): Boolean {
    if (other !is ContinueNavigationButtonViewModel) return true
    if(this !== other) return true

    // Compare the fields to check if there are changes
    return this.hasPreviousButton != other.hasPreviousButton ||
      this.hasConversationView != other.hasConversationView ||
      this.shouldAnimateContinueButton != other.shouldAnimateContinueButton ||
      this.isSplitView != other.isSplitView ||
      this.continueButtonAnimationTimestampMs != other.continueButtonAnimationTimestampMs
  }
}
