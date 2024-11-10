package org.oppia.android.app.player.state.itemviewmodel

import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.Log

/** [StateItemViewModel] for content-card state. */
class ContentViewModel(
  val htmlContent: CharSequence,
  val gcsEntityId: String,
  val hasConversationView: Boolean,
  val isSplitView: Boolean,
  val supportsConceptCards: Boolean
) : StateItemViewModel(ViewType.CONTENT) {

  private val underscoreRegex = Regex("(?<=\\s|[,.;?!])_{3,}(?=\\s|[,.;?!])")
  private val replacementText = "Blank"

  /**
   * Replaces "2+ underscores, with space/punctuation on both sides" in the input text with a
   * replacement string "blank", returning a Spannable.
   * Adjusts offsets to handle text length changes during replacements.
   */
  fun replaceRegexWithBlank(inputText: CharSequence): Spannable {
    Log.d("testRegex", "hi$inputText hi")

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
    Log.d("testRegex", spannableStringBuilder.toString())
    return spannableStringBuilder
  }
}