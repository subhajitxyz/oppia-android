package org.oppia.android.app.topic.flashbackcard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import javax.inject.Inject
import org.oppia.android.R
import org.oppia.android.app.fragment.FragmentScope
import org.oppia.android.app.model.ProfileId
import org.oppia.android.app.model.WrittenTranslationContext
import org.oppia.android.app.topic.conceptcard.ConceptCardFragment
import org.oppia.android.app.topic.conceptcard.ConceptCardListener
import org.oppia.android.databinding.FlashbackCardFragmentBinding
import org.oppia.android.domain.oppialogger.OppiaLogger
import org.oppia.android.util.parser.html.HtmlParser

/** Presenter for [FlashbackCardFragment], sets up bindings from ViewModel. */
@FragmentScope
class FlashbackCardFragmentPresenter @Inject constructor(
  private val fragment: Fragment,
  private val oppiaLogger: OppiaLogger,
//  private val analyticsController: AnalyticsController,
//  private val htmlParserFactory: HtmlParser.Factory,
//  @ConceptCardHtmlParserEntityType private val entityType: String,
//  @DefaultResourceBucketName private val resourceBucketName: String,
  private val flashbackCardViewModel: FlashbackCardViewModel,
//  private val translationController: TranslationController,
//  private val appLanguageResourceHandler: AppLanguageResourceHandler
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
    profileId: ProfileId
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

    binding.flashbackCardToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
    binding.flashbackCardToolbar.setNavigationContentDescription(
      R.string.navigate_up
    )

    //need to learn dismiss concept
    binding.flashbackCardToolbar.setNavigationOnClickListener {
      (fragment.requireActivity() as? ConceptCardListener)?.dismissConceptCard()
    }

    binding.let { it ->
      it.viewModel = flashbackCardViewModel
      it.lifecycleOwner = fragment
    }

    return binding.root
  }

//  private fun logConceptCardEvent(skillId: String) {
//    analyticsController.logImportantEvent(
//      oppiaLogger.createOpenConceptCardContext(skillId), profileId
//    )
//  }

  override fun onConceptCardLinkClicked(view: View, skillId: String) {
    ConceptCardFragment.bringToFrontOrCreateIfNew(skillId, profileId, fragment.childFragmentManager)
  }

  /** Removes all [ConceptCardFragment] in the given FragmentManager. */
  fun dismissConceptCard() {
    ConceptCardFragment.dismissAll(fragment.childFragmentManager)
  }
}
