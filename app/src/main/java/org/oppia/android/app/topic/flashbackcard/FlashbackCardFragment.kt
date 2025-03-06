package org.oppia.android.app.topic.flashbackcard

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import javax.inject.Inject
import org.oppia.android.R
import org.oppia.android.app.fragment.FragmentComponentImpl
import org.oppia.android.app.fragment.InjectableDialogFragment
import org.oppia.android.app.hintsandsolution.HintsAndSolutionDialogFragment
import org.oppia.android.app.model.ConceptCardFragmentArguments
import org.oppia.android.app.model.EphemeralState
import org.oppia.android.app.model.FlashbackFragmentArguments
import org.oppia.android.app.model.HelpIndex
import org.oppia.android.app.model.HintsAndSolutionDialogFragmentArguments
import org.oppia.android.app.model.ProfileId
import org.oppia.android.app.model.State
import org.oppia.android.app.model.WrittenTranslationContext
import org.oppia.android.app.topic.conceptcard.ConceptCardFragment
import org.oppia.android.app.topic.conceptcard.ConceptCardFragmentPresenter
import org.oppia.android.util.extensions.getProto
import org.oppia.android.util.extensions.putProto
import org.oppia.android.util.profile.CurrentUserProfileIdIntentDecorator.decorateWithUserProfileId
import org.oppia.android.util.profile.CurrentUserProfileIdIntentDecorator.extractCurrentUserProfileId

//private const val SKILL_ID_ARGUMENT_KEY = "ConceptCardFragment.skill_id"
private const val PROFILE_ID_ARGUMENT_KEY = "FlashbackFragment.profile_id"

/* Fragment that displays a fullscreen dialog for concept cards */
class FlashbackCardFragment : InjectableDialogFragment() {

  companion object {

    const val FLASHBACK_CARD_FRAGMENT_ARGUMENTS_KEY = "FlashbackCardFragment.arguments"

    /** The fragment tag corresponding to the concept card dialog fragment. */
    private const val FLASHBACK_CARD_DIALOG_FRAGMENT_TAG = "FLASHBACK_CARD_FRAGMENT"

    fun newInstance(
      id: String,
      writtenTranslationContext: WrittenTranslationContext,
      ephemeralState: EphemeralState
    ): FlashbackCardFragment {
      val args = FlashbackFragmentArguments.newBuilder().apply {
        this.idArgument = id
        this.writtenTranslationContext = writtenTranslationContext
        this.ephemeralState = ephemeralState
      }.build()
      return FlashbackCardFragment().apply {
        arguments = Bundle().apply {
          putProto(FlashbackCardFragment.FLASHBACK_CARD_FRAGMENT_ARGUMENTS_KEY, args)
        }
      }
    }
  }

  @Inject
  lateinit var flashbackCardFragmentPresenter: FlashbackCardFragmentPresenter

  override fun onAttach(context: Context) {
    super.onAttach(context)
    (fragmentComponent as FragmentComponentImpl).inject(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    super.onCreateView(inflater, container, savedInstanceState)
    val arguments =
      checkNotNull(
        arguments
      ) { "Expected arguments to be passed to FlashbackCardFragment" }
    val args = arguments.getProto(
      FLASHBACK_CARD_FRAGMENT_ARGUMENTS_KEY,
      FlashbackFragmentArguments.getDefaultInstance()
    )

    val id =
      checkNotNull(
        args.idArgument
      ) { "Expected id to be passed to HintsAndSolutionDialogFragment" }

    val writtenTranslationContext =
      args.writtenTranslationContext ?: WrittenTranslationContext.getDefaultInstance()
    val profileId = arguments.getProto(HintsAndSolutionDialogFragment.PROFILE_ID_KEY, ProfileId.getDefaultInstance())

    val ephemeralState = args.ephemeralState ?: EphemeralState.getDefaultInstance()

    return flashbackCardFragmentPresenter.handleCreateView(inflater, container, id, writtenTranslationContext, profileId, ephemeralState)
  }

  override fun onStart() {
    super.onStart()
    dialog?.window?.setWindowAnimations(R.style.FullScreenDialogStyle)
  }

  //need to learn and then implement
  fun dismissFlashbackCard() {
    dismissAll(fragmentManager = parentFragmentManager)
  }

  fun dismissAll(fragmentManager: FragmentManager) {
    val toDismiss = fragmentManager.fragments.filterIsInstance<FlashbackCardFragment>()
    if (toDismiss.isNotEmpty()) {
      val transaction = fragmentManager.beginTransaction()
      for (fragment in toDismiss) {
        transaction.remove(fragment)
      }
      transaction.commitNow()
    }
  }
}
