package org.oppia.android.app.player.state.itemviewmodel

import androidx.databinding.ObservableField
import java.util.*
import org.oppia.android.app.player.state.listener.PreviousNavigationButtonListener
import org.oppia.android.app.player.state.listener.SubmitNavigationButtonListener
import org.oppia.android.app.recyclerview.BindableItemViewModel
import org.oppia.android.app.recyclerview.StateItemId

/** [StateItemViewModel] for navigation to previous states and submitting new answers. */
class SubmitButtonViewModel(
  val canSubmitAnswer: ObservableField<Boolean>,
  val hasConversationView: Boolean,
  val hasPreviousButton: Boolean,
  val previousNavigationButtonListener: PreviousNavigationButtonListener,
  val submitNavigationButtonListener: SubmitNavigationButtonListener,
  val isSplitView: Boolean
) : StateItemViewModel(ViewType.SUBMIT_ANSWER_BUTTON), BindableItemViewModel {

  //private val uniqueId: String = UUID.randomUUID().toString()
  override val contentId: StateItemId
    get() = StateItemId.SubmitButton(canSubmitAnswer.toString())

  override fun hasChanges(other: BindableItemViewModel): Boolean {
    if (other !is SubmitButtonViewModel) return true

    // Compare the fields to check if there are changes
    return this.canSubmitAnswer != other.canSubmitAnswer ||
      this.hasConversationView != other.hasConversationView ||
      this.hasPreviousButton != other.hasPreviousButton ||
      this.isSplitView != other.isSplitView
  }
}

