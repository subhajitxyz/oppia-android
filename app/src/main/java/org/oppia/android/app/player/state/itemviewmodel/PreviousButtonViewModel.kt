package org.oppia.android.app.player.state.itemviewmodel

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
  override val contentId: StateItemId
    get() = StateItemId.Feedback("x")

  override fun hasChanges(other: BindableItemViewModel): Boolean {
    return true
    TODO("Not yet implemented")
  }
//  override val contentId: StateItemId = StateItemId.PreviousNavigationButton
//  override fun hasChanges(other: BindableItemViewModel): Boolean {
//    return true
//  }
}
