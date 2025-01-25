package org.oppia.android.app.player.state.itemviewmodel

import android.util.Log
import java.util.*
import org.oppia.android.app.recyclerview.BindableItemViewModel
import org.oppia.android.app.recyclerview.StateItemId

/** [StateItemViewModel] for feedback blurbs. */
class FeedbackViewModel(
  val htmlContent: CharSequence,
  val gcsEntityId: String,
  val hasConversationView: Boolean,
  val isSplitView: Boolean,
  val supportsConceptCards: Boolean
) : StateItemViewModel(ViewType.FEEDBACK), BindableItemViewModel {
  private val uniqueId: String = UUID.randomUUID().toString()
  override val contentId: StateItemId
    get() = StateItemId.Feedback(uniqueId)

  override fun hasChanges(other: BindableItemViewModel): Boolean {
    return true
  }

  //private val uniqueId: String = UUID.randomUUID().toString()
//  override val contentId: StateItemId
//    get() = StateItemId.Feedback(htmlContent)
//
//  override fun hasChanges(other: BindableItemViewModel): Boolean {
//    return true
//    Log.d("testdetails",gcsEntityId+ "   " + supportsConceptCards)
//    if (other !is FeedbackViewModel) return true
//
//    // Compare the fields to check if there are changes
//    return this.gcsEntityId != other.gcsEntityId ||
//      this.hasConversationView != other.hasConversationView ||
//      this.isSplitView != other.isSplitView ||
//      this.supportsConceptCards != other.supportsConceptCards
  //}
//  override val contentId: StateItemId
//    get() = StateItemId.Feedback(htmlContent)
//
//  override fun hasChanges(other: BindableItemViewModel): Boolean {
//    if (other !is FeedbackViewModel) return true
//
//    // Compare the fields to check if there are changes
//    return this.htmlContent != other.htmlContent ||
//      this.gcsEntityId != other.gcsEntityId ||
//      this.hasConversationView != other.hasConversationView ||
//      this.isSplitView != other.isSplitView ||
//      this.supportsConceptCards != other.supportsConceptCards
//  }
}
