package org.oppia.android.app.topic.flashbackcard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import javax.inject.Inject
import nl.dionsegijn.konfetti.KonfettiView
import org.oppia.android.R
import org.oppia.android.app.fragment.FragmentScope
import org.oppia.android.app.model.EphemeralQuestion
import org.oppia.android.app.model.EphemeralState
import org.oppia.android.app.model.ProfileId
import org.oppia.android.app.model.ProfileType
import org.oppia.android.app.model.UserAnswerState
import org.oppia.android.app.model.WrittenTranslationContext
import org.oppia.android.app.player.state.ConfettiConfig
import org.oppia.android.app.player.state.StatePlayerRecyclerViewAssembler
import org.oppia.android.app.topic.conceptcard.ConceptCardFragment
import org.oppia.android.app.topic.conceptcard.ConceptCardListener
import org.oppia.android.app.translation.AppLanguageResourceHandler
import org.oppia.android.app.utility.SplitScreenManager
import org.oppia.android.databinding.FlashbackCardFragmentBinding
import org.oppia.android.domain.exploration.ExplorationProgressController
import org.oppia.android.domain.oppialogger.OppiaLogger
import org.oppia.android.domain.translation.TranslationController
import org.oppia.android.util.data.AsyncResult
import org.oppia.android.util.data.DataProviders.Companion.toLiveData
import org.oppia.android.util.gcsresource.DefaultResourceBucketName
import org.oppia.android.util.parser.html.ExplorationHtmlParserEntityType
import org.oppia.android.util.parser.html.HtmlParser

/** Presenter for [FlashbackCardFragment], sets up bindings from ViewModel. */
@FragmentScope
class FlashbackCardFragmentPresenter @Inject constructor(
  private val activity: AppCompatActivity,
  private val fragment: Fragment,
  private val oppiaLogger: OppiaLogger,
  private val translationController: TranslationController,
//  private val analyticsController: AnalyticsController,

  //subha mile 2.1
  private val explorationProgressController: ExplorationProgressController,
  private val splitScreenManager: SplitScreenManager,
  private val htmlParserFactory: HtmlParser.Factory,
  private val assemblerBuilderFactory: StatePlayerRecyclerViewAssembler.Builder.Factory,
  @ExplorationHtmlParserEntityType private val entityType: String,
  @DefaultResourceBucketName private val resourceBucketName: String,
  private val flashbackCardViewModel: FlashbackCardViewModel,
//  private val translationController: TranslationController,
  private val appLanguageResourceHandler: AppLanguageResourceHandler
) : HtmlParser.CustomOppiaTagActionListener {
  private lateinit var profileId: ProfileId

  //subha mile 2.1
  private lateinit var recyclerViewAssembler: StatePlayerRecyclerViewAssembler
  private val ephemeralStateLiveData: LiveData<AsyncResult<EphemeralState>> by lazy {
    explorationProgressController.getCurrentState().toLiveData()
  }

  private val hasConversationView = false
  private lateinit var explorationId: String

  /**
   * Sets up data binding and toolbar.
   * Host activity must inherit ConceptCardListener to dismiss this fragment.
   */
  fun handleCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    id: String,
    writtenTranslationContext: WrittenTranslationContext,
    profileId: ProfileId,
    explorationId: String,
    ephemeralState: EphemeralState
  ): View? {
    this.profileId = profileId
    this.explorationId = explorationId
    val binding = FlashbackCardFragmentBinding.inflate(
      inflater,
      container,
      /* attachToRoot= */ false
    )
    //subha mile 2.1

    Log.d("testmile2.1"," ${id},${writtenTranslationContext},${ephemeralState}")

    recyclerViewAssembler = createRecyclerViewAssembler(
      assemblerBuilderFactory.create(resourceBucketName, entityType, profileId, UserAnswerState.getDefaultInstance())
    )



    binding.apply {
      lifecycleOwner = fragment
      viewModel = flashbackCardViewModel
    }
    binding.stateRecyclerView.apply {
      adapter = recyclerViewAssembler.adapter
    }
    binding.extraInteractionRecyclerView.apply {
      adapter = recyclerViewAssembler.rhsAdapter
    }

    subscribeToCurrentQuestion()
    return binding.root










    ///correct previuos code

    //val view = binding.conceptCardExplanationText

    //flashbackCardViewModel.initialize(skillId, profileId)
    //logConceptCardEvent(skillId)

//    Log.d("test",id)
//    Log.d("test",writtenTranslationContext.toString())
//
//    binding.flashbackCardToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
//    binding.flashbackCardToolbar.setNavigationContentDescription(
//      R.string.navigate_up
//    )
//
//    //need to learn dismiss concept
////    binding.flashbackCardToolbar.setNavigationOnClickListener {
////      (fragment.requireActivity() as? ConceptCardListener)?.dismissConceptCard()
////    }
////
//    binding.flashbackCardToolbar.setNavigationOnClickListener {
//      Log.d("testflashback","click on closebutton")
//      (fragment.requireActivity() as? FlashbackCardListener)?.dismissFlashbackCard()
//    }
//    binding.okUnderstandButton.setOnClickListener {
//      Log.d("testflashback","click on ok_understand")
//      (fragment.requireActivity() as? FlashbackCardListener)?.dismissFlashbackCard()
//    }
//
//
//    //binding.flashbackCardExplanationText.text = contentSubtitledHtml
//
//    binding.let { it ->
//      it.viewModel = flashbackCardViewModel
//      it.lifecycleOwner = fragment
//    }
//
//
//    val view = binding.flashbackCardExplanationText
//    val contentSubtitledHtml =
//      translationController.extractString(
//        ephemeralState.state.content, ephemeralState.writtenTranslationContext
//      )
//
//    view.text =
//      htmlParserFactory.create(
//        resourceBucketName,
//        entityType,
//        id,
//        customOppiaTagActionListener = this,
//        imageCenterAlign = true,
//        displayLocale = appLanguageResourceHandler.getDisplayLocale()
//      ).parseOppiaHtml(
//        contentSubtitledHtml,
//        view,
//        supportsLinks = true,
//        supportsConceptCards = true
//      )
//    //setFlashbackContent(contentSubtitledHtml)
//
//    //subha mile 2.1
//    subscribeToCurrentQuestion()
//
//    return binding.root
  }

  //subha mile 2.1
  private fun subscribeToCurrentQuestion() {
    ephemeralStateLiveData.observe(
      fragment,
      Observer {
        processEphemeralFlashbackResult(it)
      }
    )
  }

  private fun processEphemeralFlashbackResult(result: AsyncResult<EphemeralState>) {
    when (result) {
      is AsyncResult.Failure -> {
        oppiaLogger.e(
          "QuestionPlayerFragment", "Failed to retrieve ephemeral question", result.error
        )
      }
      is AsyncResult.Pending -> {} // Display nothing until a valid result is available.
      is AsyncResult.Success -> processEphemeralFlashback(result.value)
    }
  }

  private fun processEphemeralFlashback(ephemeralState: EphemeralState) {
    val shouldSplit = splitScreenManager.shouldSplitScreen(ephemeralState.state.interaction.id)
    if (shouldSplit) {
      flashbackCardViewModel.isSplitView.set(true)
      flashbackCardViewModel.centerGuidelinePercentage.set(0.5f)
    } else {
      flashbackCardViewModel.isSplitView.set(false)
      flashbackCardViewModel.centerGuidelinePercentage.set(1f)
    }

//    val isInNewState =
//      ::currentStateName.isInitialized && currentStateName != ephemeralState.state.name

//    currentState = ephemeralState.state
//    currentStateName = ephemeralState.state.name
//
//    showOrHideAudioByState(ephemeralState.state)

    val dataPair = recyclerViewAssembler.compute(
      ephemeralState,
      explorationId,
      shouldSplit
    )

    flashbackCardViewModel.itemList.clear()
    flashbackCardViewModel.itemList += dataPair.first
    flashbackCardViewModel.rightItemList.clear()
    flashbackCardViewModel.rightItemList += dataPair.second

  }

  private fun createRecyclerViewAssembler(
    builder: StatePlayerRecyclerViewAssembler.Builder,
//    congratulationsTextView: TextView,
//    congratulationsTextConfettiView: KonfettiView
  ): StatePlayerRecyclerViewAssembler {
    // TODO(#502): Add support for surfacing skills that need to be reviewed by the learner.
    return builder
      .hasConversationView(hasConversationView)
      .addContentSupport()
      .addFeedbackSupport()
      .addInteractionSupport(flashbackCardViewModel.getCanSubmitAnswer())
      //.addPastAnswersSupport()
      //.addWrongAnswerCollapsingSupport()
      //.addForwardNavigationSupport()
      //.addReplayButtonSupport()
      //.addReturnToTopicSupport()
      //.addHintsAndSolutionsSupport()
//      .addCelebrationForCorrectAnswers(
//        congratulationsTextView,
//        congratulationsTextConfettiView,
//        ConfettiConfig.MINI_CONFETTI_BURST
//      )
//      .addConceptCardSupport()
      .build()
  }

  //subha
//  private fun setFlashbackContent(content: String) {
//    flashbackCardViewModel.updateFlashbackContent(content)
//  }



//  private fun logConceptCardEvent(skillId: String) {
//    analyticsController.logImportantEvent(
//      oppiaLogger.createOpenConceptCardContext(skillId), profileId
//    )
//  }

  override fun onConceptCardLinkClicked(view: View, skillId: String) {
    ConceptCardFragment.bringToFrontOrCreateIfNew(skillId, profileId, fragment.childFragmentManager)
  }

  /** Removes all [ConceptCardFragment] in the given FragmentManager. */
//  fun dismissFlashbackCard() {
//    FlashbackCardFragmen.d
//  }

}
