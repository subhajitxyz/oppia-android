package org.oppia.android.app.player.state.itemviewmodel

import android.util.Log
import org.oppia.android.app.player.state.listener.RevisitButtonListener

/** [StateItemViewModel] for navigation to old states for revision. */
class FlashbackButtonViewModel(
  val hasConversationView: Boolean,
  val hasPreviousButton: Boolean,
  val isSplitView: Boolean,
  val revisitButtonListener: RevisitButtonListener,
  val flashbackStateName: String
) : StateItemViewModel(ViewType.FLASHBACK_BUTTON) {

  //subha 1.4
  fun onRevisitButtonClicked() {
    Log.d("subharevisit","button clicked in flashabckviewmodel")
    revisitButtonListener.onRevisitButtonClicked(flashbackStateName)
  }
}