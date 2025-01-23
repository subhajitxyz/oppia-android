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
  override val contentId: StateItemId = StateItemId.PreviousNavigationButton
  override fun hasChanges(other: BindableItemViewModel): Boolean {
    TODO("Not yet implemented")
  }
}
