package org.oppia.android.app.player.state.itemviewmodel

import java.util.*
import org.oppia.android.app.player.state.listener.PreviousNavigationButtonListener
import org.oppia.android.app.recyclerview.BindableItemViewModel
import org.oppia.android.app.recyclerview.StateItemId

/**
 * [StateItemViewModel] for navigating to a previous state. Unlike other navigation buttons, this model only represents
 * backward navigation.
 */
class PreviousButtonViewModel(
  val hasConversationView: Boolean,
  val previousNavigationButtonListener: PreviousNavigationButtonListener,
  val isSplitView: Boolean
) : StateItemViewModel(ViewType.PREVIOUS_NAVIGATION_BUTTON), BindableItemViewModel {
  private val uniqueId: String = UUID.randomUUID().toString()
  override val contentId: StateItemId
    get() = StateItemId.PreviousNavigationButton

  override fun hasChanges(other: BindableItemViewModel): Boolean {
    if (other !is PreviousButtonViewModel) return true

    // Compare the fields to check if there are changes
    return this.previousNavigationButtonListener != other.previousNavigationButtonListener ||
      this.hasConversationView != other.hasConversationView ||
      this.isSplitView != other.isSplitView
  }
//  override val contentId: StateItemId = StateItemId.PreviousNavigationButton
//  override fun hasChanges(other: BindableItemViewModel): Boolean {
//    return true
//  }
}
