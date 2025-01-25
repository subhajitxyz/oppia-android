package org.oppia.android.app.player.state.itemviewmodel

import androidx.databinding.ObservableBoolean
import org.oppia.android.R
import org.oppia.android.app.player.state.listener.PreviousResponsesHeaderClickListener
import org.oppia.android.app.recyclerview.BindableItemViewModel
import org.oppia.android.app.recyclerview.StateItemId
import org.oppia.android.app.translation.AppLanguageResourceHandler

/** [StateItemViewModel] for the header of the section of previously submitted answers. */
class PreviousResponsesHeaderViewModel(
  private val previousAnswerCount: Int,
  val hasConversationView: Boolean,
  var isExpanded: ObservableBoolean,
  private val previousResponsesHeaderClickListener: PreviousResponsesHeaderClickListener,
  val isSplitView: Boolean,
  private val resourceHandler: AppLanguageResourceHandler
) : StateItemViewModel(ViewType.PREVIOUS_RESPONSES_HEADER), BindableItemViewModel {

  override val contentId: StateItemId
    get() = StateItemId.Feedback("x")

  override fun hasChanges(other: BindableItemViewModel): Boolean {
    return true
  }
//  override val contentId: StateItemId
//    get() = StateItemId.PreviousAnswerCount(previousAnswerCount.toString())
//
//  override fun hasChanges(other: BindableItemViewModel): Boolean {
//    return true
//    if (other !is PreviousResponsesHeaderViewModel) return true
//
//    // Compare the fields to check if there are changes
//    return this.previousAnswerCount != other.previousAnswerCount ||
//      this.hasConversationView != other.hasConversationView ||
//      this.isExpanded != other.isExpanded ||
//      this.isSplitView != other.isSplitView
  //}

  /** Called when the user clicks on the previous response header. */
  fun onResponsesHeaderClicked() = previousResponsesHeaderClickListener.onResponsesHeaderClicked()

  /** Returns the user-readable header text for previous responses. */
  fun computePreviousResponsesHeaderText(): String {
    return resourceHandler.getStringInLocaleWithWrapping(
      R.string.previous_responses_header, previousAnswerCount.toString()
    )
  }
}
