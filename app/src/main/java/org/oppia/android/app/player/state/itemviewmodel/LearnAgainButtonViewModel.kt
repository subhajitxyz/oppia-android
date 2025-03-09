package org.oppia.android.app.player.state.itemviewmodel

import androidx.databinding.ObservableField
import org.oppia.android.app.player.state.listener.LearnAgainButtonListener

//subha two
class LearnAgainButtonViewModel(
  val hasConversationView: Boolean,
  val learnAgainButtonListener: LearnAgainButtonListener,
  val isSplitView: Boolean
) : StateItemViewModel(ViewType.SUBMIT_ANSWER_BUTTON)