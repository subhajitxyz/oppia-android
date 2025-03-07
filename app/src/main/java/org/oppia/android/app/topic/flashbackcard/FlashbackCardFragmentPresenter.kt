package org.oppia.android.app.topic.flashbackcard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import javax.inject.Inject
import org.oppia.android.R
import org.oppia.android.app.fragment.FragmentScope
import org.oppia.android.app.model.EphemeralState
import org.oppia.android.app.model.ProfileId
import org.oppia.android.app.model.ProfileType
import org.oppia.android.app.model.WrittenTranslationContext
import org.oppia.android.app.topic.conceptcard.ConceptCardFragment
import org.oppia.android.app.topic.conceptcard.ConceptCardListener
import org.oppia.android.app.translation.AppLanguageResourceHandler
import org.oppia.android.databinding.FlashbackCardFragmentBinding
import org.oppia.android.domain.oppialogger.OppiaLogger
import org.oppia.android.domain.translation.TranslationController
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
  private val htmlParserFactory: HtmlParser.Factory,
  @ExplorationHtmlParserEntityType private val entityType: String,
  @DefaultResourceBucketName private val resourceBucketName: String,
  private val flashbackCardViewModel: FlashbackCardViewModel,
//  private val translationController: TranslationController,
  private val appLanguageResourceHandler: AppLanguageResourceHandler
) : HtmlParser.CustomOppiaTagActionListener {
  private lateinit var profileId: ProfileId

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
    ephemeralState: EphemeralState
  ): View? {
    this.profileId = profileId
    val binding = FlashbackCardFragmentBinding.inflate(
      inflater,
      container,
      /* attachToRoot= */ false
    )
    //val view = binding.conceptCardExplanationText

    //flashbackCardViewModel.initialize(skillId, profileId)
    //logConceptCardEvent(skillId)

    Log.d("test",id)
    Log.d("test",writtenTranslationContext.toString())

    binding.flashbackCardToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
    binding.flashbackCardToolbar.setNavigationContentDescription(
      R.string.navigate_up
    )

    //need to learn dismiss concept
//    binding.flashbackCardToolbar.setNavigationOnClickListener {
//      (fragment.requireActivity() as? ConceptCardListener)?.dismissConceptCard()
//    }
//
    binding.flashbackCardToolbar.setNavigationOnClickListener {
      Log.d("testflashback","click on closebutton")
      (fragment.requireActivity() as? FlashbackCardListener)?.dismissFlashbackCard()
    }
    binding.okUnderstandButton.setOnClickListener {
      Log.d("testflashback","click on ok_understand")
      (fragment.requireActivity() as? FlashbackCardListener)?.dismissFlashbackCard()
    }


    //binding.flashbackCardExplanationText.text = contentSubtitledHtml

    binding.let { it ->
      it.viewModel = flashbackCardViewModel
      it.lifecycleOwner = fragment
    }

    val view = binding.flashbackCardExplanationText
    val contentSubtitledHtml =
      translationController.extractString(
        ephemeralState.state.content, ephemeralState.writtenTranslationContext
      )

    view.text =
      htmlParserFactory.create(
        resourceBucketName,
        entityType,
        id,
        customOppiaTagActionListener = this,
        imageCenterAlign = true,
        displayLocale = appLanguageResourceHandler.getDisplayLocale()
      ).parseOppiaHtml(
        contentSubtitledHtml,
        view,
        supportsLinks = true,
        supportsConceptCards = true
      )
    //setFlashbackContent(contentSubtitledHtml)

    return binding.root
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
