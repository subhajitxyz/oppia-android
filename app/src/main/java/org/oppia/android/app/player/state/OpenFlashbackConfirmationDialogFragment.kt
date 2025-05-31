package org.oppia.android.app.player.state

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.oppia.android.app.fragment.FragmentComponentImpl
import org.oppia.android.app.fragment.InjectableDialogFragment
import org.oppia.android.app.model.ProfileId
import org.oppia.android.app.ui.R
import org.oppia.android.util.profile.CurrentUserProfileIdIntentDecorator.decorateWithUserProfileId
import javax.inject.Inject

/** Fragment that displays a dialog for survey exit confirmation. */
class OpenFlashbackConfirmationDialogFragment : InjectableDialogFragment() {
  @Inject
  lateinit var openFlashbackConfirmationDialogFragmentPresenter:
    OpenFlashbackConfirmationDialogFragmentPresenter

  companion object {
    /**
     * Creates a new instance of a DialogFragment to display an exit confirmation in a survey.
     *
     * @param profileId the ID of the profile viewing the survey
     * @return [OpenFlashbackConfirmationDialogFragment]: DialogFragment
     */
    fun newInstance(
      profileId: ProfileId
    ): OpenFlashbackConfirmationDialogFragment {
      return OpenFlashbackConfirmationDialogFragment().apply {
        arguments = Bundle().apply {
          decorateWithUserProfileId(profileId)
        }
      }
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    (fragmentComponent as FragmentComponentImpl).inject(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.ExitSurveyConfirmationDialogStyle)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    dialog?.setCanceledOnTouchOutside(false)
    dialog?.setCancelable(false)
    return OpenFlashbackConfirmationDialogFragmentPresenter.handleCreateView(inflater, container)
  }

  override fun onStart() {
    super.onStart()
    dialog?.window?.setWindowAnimations(R.style.ExitSurveyConfirmationDialogStyle)
  }
}
