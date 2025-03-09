package org.oppia.android.app.player.state.itemviewmodel

import org.oppia.android.app.player.state.listener.LearnAgainButtonListener

//subha two
class LearnAgainButtonViewModel(
  val hasConversationView: Boolean,
  val learnAgainButtonListener: LearnAgainButtonListener,
  val isSplitView: Boolean
) : StateItemViewModel(ViewType.LEARN_AGAIN_BUTTON)