package org.oppia.android.app.player.state.itemviewmodel

import android.text.Spannable
import android.text.SpannableStringBuilder
import org.oppia.android.app.recyclerview.BindableItemViewModel
import org.oppia.android.app.recyclerview.StateItemId

/** [StateItemViewModel] for content-card state. */
class ContentViewModel(
  val htmlContent: CharSequence,
  val gcsEntityId: String,
  val hasConversationView: Boolean,
  val isSplitView: Boolean,
  val supportsConceptCards: Boolean
) : StateItemViewModel(ViewType.CONTENT), BindableItemViewModel {
  override val contentId: StateItemId
    get() = StateItemId.Content(htmlContent)

  override fun hasChanges(other: BindableItemViewModel): Boolean {
    if (other !is ContentViewModel) return true
    if(this !== other) return true

    // Compare the fields to check if there are changes
    return this.htmlContent != other.htmlContent ||
      this.gcsEntityId != other.gcsEntityId ||
      this.hasConversationView != other.hasConversationView ||
      this.isSplitView != other.isSplitView ||
      this.supportsConceptCards != other.supportsConceptCards
  }

  private val underscoreRegex = Regex("(?<=\\s|[,.;?!])_{3,}(?=\\s|[,.;?!])")
  private val replacementText = "Blank"

  /**
   * Replaces "2+ underscores, with space/punctuation on both sides" in the input text with a
   * replacement string "blank", returning a Spannable.
   * Adjusts offsets to handle text length changes during replacements.
   */
  fun replaceRegexWithBlank(inputText: CharSequence): Spannable {
    val spannableStringBuilder = SpannableStringBuilder(inputText)
    val matches = underscoreRegex.findAll(inputText)
    var lengthOffset = 0

    for (match in matches) {
      val matchStart = match.range.first + lengthOffset
      val matchEnd = match.range.last + 1 + lengthOffset
      spannableStringBuilder.replace(matchStart, matchEnd, replacementText)

      // Adjust offset due to change in length (difference between old and new text length)
      lengthOffset += replacementText.length - (matchEnd - matchStart)
    }
    return spannableStringBuilder
  }
}
