package org.oppia.android.app.player.state.itemviewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import org.oppia.android.app.model.Interaction
import org.oppia.android.app.model.SubtitledHtml
import org.oppia.android.app.model.UserAnswer
import org.oppia.android.app.model.WrittenTranslationContext
import org.oppia.android.app.translation.AppLanguageResourceHandler
import org.oppia.android.app.view.models.R
import org.oppia.android.app.viewmodel.ObservableArrayList
import org.oppia.android.domain.translation.TranslationController
import org.oppia.android.util.logging.ConsoleLogger
import org.oppia.android.util.parser.html.CUSTOM_IMG_TAG
import org.oppia.android.util.parser.html.CustomHtmlContentHandler
import org.oppia.android.util.parser.html.ImageTagHandler

/** [StateItemViewModel] for previously submitted answers. */
class SubmittedAnswerViewModel(
  val submittedUserAnswer: UserAnswer,
  val gcsEntityId: String,
  val hasConversationView: Boolean,
  val isSplitView: Boolean,
  val supportsConceptCards: Boolean,
  private val resourceHandler: AppLanguageResourceHandler,
  val interaction: Interaction, //demo subha
  val writtenTranslationContext: WrittenTranslationContext,
  private val translationController: TranslationController,
  consoleLogger: ConsoleLogger
) : StateItemViewModel(ViewType.SUBMITTED_ANSWER) {
  val isCorrectAnswer = ObservableField(DEFAULT_IS_CORRECT_ANSWER)
  val submittedAnswer: ObservableField<CharSequence> = ObservableField(DEFAULT_SUBMITTED_ANSWER)
  val isExtraInteractionAnswerCorrect = ObservableField(DEFAULT_IS_CORRECT_ANSWER)
  val submittedAnswerContentDescription: ObservableField<String> =
    ObservableField(
      computeSubmittedAnswerContentDescription(
        DEFAULT_IS_CORRECT_ANSWER, DEFAULT_SUBMITTED_ANSWER, DEFAULT_ACCESSIBLE_ANSWER
      )
    )
  private var accessibleAnswer: String? = DEFAULT_ACCESSIBLE_ANSWER

  fun setSubmittedAnswer(submittedAnswer: CharSequence, accessibleAnswer: String?) {
    this.submittedAnswer.set(submittedAnswer)
    this.accessibleAnswer = accessibleAnswer
    updateSubmittedAnswerContentDescription()
  }

  fun setIsCorrectAnswer(isCorrectAnswer: Boolean) {
    this.isCorrectAnswer.set(isCorrectAnswer)
    updateSubmittedAnswerContentDescription()
  }

  private fun updateSubmittedAnswerContentDescription() {
    submittedAnswerContentDescription.set(
      computeSubmittedAnswerContentDescription(
        isCorrectAnswer.get() ?: DEFAULT_IS_CORRECT_ANSWER,
        submittedAnswer.get() ?: DEFAULT_SUBMITTED_ANSWER,
        accessibleAnswer
      )
    )
  }

  private fun computeSubmittedAnswerContentDescription(
    isCorrectAnswer: Boolean,
    submittedAnswer: CharSequence,
    accessibleAnswer: String?
  ): String {
    val answer = if (accessibleAnswer.isNullOrBlank()) submittedAnswer else accessibleAnswer
    return if (isCorrectAnswer) {
      resourceHandler.getStringInLocaleWithWrapping(
        R.string.correct_submitted_answer_with_append, answer
      )
    } else {
      resourceHandler.getStringInLocaleWithWrapping(
        R.string.incorrect_submitted_answer_with_append, answer
      )
    }
  }

  // subha idea 1
  // pass interacton.id in this class
  //
  // Step 2 :::::::::
  // Now we have interaction.id
  // Compute  List<SelectionSubmittedItemViewModel> based on interaction.id
  // this is list will be rendered in final submitted answer in submitted_answer_item.xml

  //
  // how binding will be happened in stateplayerRecyclerviewAssembler ?

  // Step 3::::
  // introduce these files
  // multiple_choice_submitted_item.xml,
  // item_selection_submitted_item.xml, and
  // SelectionSubmittedItemViewModel.

  // Step 4 :::::::
  //---> in StatePlayerRecyclerViewAssembler's addPastAnswersSupport()
  // if interaction.id == item_selection -> bind item_selection_submitted_item and SelectionSubmittedItemViewModel
  // else if interaction.id == item_selection -> bind multiple_choice_submitted_item and SelectionSubmittedItemViewModel


  // Conclusion --> Idea 1 is working.
  // Now look how we set actual interaction list. then try to find a final way.



  // subha idea 2

  // Step 1
  // try to find a way to pass or store list<user submitted answer idx> -> for showing submitted answer selected.
  // --->
  // 1. we can store list of correct answers (list<index>)  in UserAnswer proto        /// result -> It is working
  // 2. Use list of correct answers (list<index>) in SubmittedAnswerViewModel          /// result -> It is working

  // 3. try to finalize adapter set up in StatePlayerRecyclerViewAssembler (copy from SelectionInteractionView's createAdapter())  /// result -> It is working
  // 4. Try to make xml design like figma (multiple_choice_submitted_item.xml, item_selection_submitted_item.xml, submitted_answer_item.xml)
  // ---> Approach 1 -> Use radio button (non clickable, disabled talkback), Use textview for each correct answer ("This is a correct Answer")
  // ---> Approach 2 -> Design a circular/Squared box,






  /// FINAL ANSWER IDEA
  //  Submitted answer design should be like this---->
  /// 1. place circular/ square icon in place of radio buttons (with correct color in light/dark mode).
  /// 2. Use text("This is correct answer") below each correct options. (talk back should correctly pronounce).
  /// 3. Try to match figma view.



  // demo subha
//  fun computeItemOrMultiSubmittedAnswerList(): List<SelectionSubmittedItemViewModel> {
//    return when (interactionId) {
//      "ItemSelectionInput" -> listOf(
//        // First item in the list
//        SelectionSubmittedItemViewModel(
//          id = "hi",
//        ),
//        // Second item in the list
//        SelectionSubmittedItemViewModel(
//          id = "hi",
//        )
//      )
//
//      "MultipleChoiceInput" -> listOf(
//        // First item in the list
//        SelectionSubmittedItemViewModel(
//          id = "Hello",
//        ),
//        // Second item in the list
//        SelectionSubmittedItemViewModel(
//          id = "hello",
//        )
//      )
//
//      else -> emptyList() // Return an empty list if no condition matches
//    }
//  }

  // subha idea 2



  private val minAllowableSelectionCount: Int by lazy {
    interaction.customizationArgsMap["minAllowableSelectionCount"]?.signedInt ?: 1
  }

  private val maxAllowableSelectionCount: Int by lazy {
    // Assume that at least 1 answer always needs to be submitted, and that the max can't be less than the min for cases
    // when either of the counts are not specified.
    interaction.customizationArgsMap["maxAllowableSelectionCount"]?.signedInt
      ?: minAllowableSelectionCount
  }
  private fun areCheckboxesBound(): Boolean {
    return interaction.id == "ItemSelectionInput" && maxAllowableSelectionCount > 1
  }

  fun getSelectionItemInputType(): SelectionItemInputType {
    return if (areCheckboxesBound()) {
      SelectionItemInputType.CHECKBOXES
    } else {
      SelectionItemInputType.RADIO_BUTTONS
    }
  }


  private val choiceSubtitledHtmls: List<SubtitledHtml> by lazy {
    interaction.customizationArgsMap["choices"]
      ?.schemaObjectList
      ?.schemaObjectList
      ?.map { schemaObject -> schemaObject.customSchemaValue.subtitledHtml }
      ?: listOf()
  }

  private val customTagHandlers = mapOf<String, CustomHtmlContentHandler.CustomTagHandler>(
    CUSTOM_IMG_TAG to ImageTagHandler(consoleLogger)
  )

  val choiceItems: List<SelectionSubmittedItemViewModel> =
    computeChoiceItems(
      choiceSubtitledHtmls,
      hasConversationView,
      submittedUserAnswer.itemSelection.selectedIndexesList,
      writtenTranslationContext,
      translationController,
      customTagHandlers
    )
  private fun computeChoiceItems(
    choiceSubtitledHtmls: List<SubtitledHtml>,
    hasConversationView: Boolean,
    enabledItemsList: List<Int>,
    writtenTranslationContext: WrittenTranslationContext,
    translationController: TranslationController,
    customTagHandlers: Map<String, CustomHtmlContentHandler.CustomTagHandler>
  ): List<SelectionSubmittedItemViewModel> {
    return choiceSubtitledHtmls.mapIndexed { index, subtitledHtml ->
      SelectionSubmittedItemViewModel(
        htmlContent = subtitledHtml,
        hasConversationView = hasConversationView,
        itemIndex = index,
        isEnabled = enabledItemsList.contains(index),
        customTagHandlers = customTagHandlers,
        writtenTranslationContext = writtenTranslationContext,
        translationController = translationController,
        gcsEntityId,
        resourceHandler
      )
    }
  }


  private companion object {
    private const val DEFAULT_IS_CORRECT_ANSWER = false
    private const val DEFAULT_SUBMITTED_ANSWER = ""
    private val DEFAULT_ACCESSIBLE_ANSWER: String? = null
  }
}
