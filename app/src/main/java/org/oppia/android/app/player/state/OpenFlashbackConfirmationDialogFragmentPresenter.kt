package org.oppia.android.app.player.state

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.oppia.android.app.fragment.FragmentScope
import org.oppia.android.domain.oppialogger.OppiaLogger
import org.oppia.android.domain.survey.SurveyController
import org.oppia.android.util.data.AsyncResult
import org.oppia.android.util.data.DataProviders.Companion.toLiveData
import javax.inject.Inject
import org.oppia.android.databinding.FlashbackOpenConfirmationDialogBinding
import org.oppia.android.domain.exploration.ExplorationProgressController

//const val TAG_OPEN_FLASHBACK_CONFIRMATION_DIALOG = "OPEN_FLASHBACK_CONFIRMATION_DIALOG"

/** Presenter for [OpenFlashbackConfirmationDialogFragment], sets up bindings from ViewModel. */
@FragmentScope
class OpenFlashbackConfirmationDialogFragmentPresenter @Inject constructor(
  private val fragment: Fragment,
  private val activity: AppCompatActivity,
  private val surveyController: SurveyController,
  private val oppiaLogger: OppiaLogger,
  private val explorationProgressController: ExplorationProgressController, //subha dialog test
) {

  /** Sets up data binding. */
  fun handleCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?
  ): View {
    val binding =
      FlashbackOpenConfirmationDialogBinding.inflate(inflater, container, /* attachToRoot= */ false)

    binding.lifecycleOwner = fragment

    binding.continueConfirmationButton.setOnClickListener {
      fragment.parentFragmentManager.beginTransaction()
        .remove(fragment)
        .commitNow()
      explorationProgressController.moveToFlashback("Ratio shows relative relationship 2") //subha dialog test

    }

    binding.notNowButton.setOnClickListener {
      endSurveyWithCallback { closeSurveyDialogAndActivity() }
    }

    return binding.root
  }

  private fun closeSurveyDialogAndActivity() {
    activity.finish()
    fragment.parentFragmentManager.beginTransaction()
      .remove(fragment)
      .commitNow()
  }

  private fun endSurveyWithCallback(callback: () -> Unit) {
    surveyController.stopSurveySession(surveyCompleted = false).toLiveData().observe(
      activity,
      {
        when (it) {
          is AsyncResult.Pending -> oppiaLogger.d("SurveyActivity", "Stopping survey session")
          is AsyncResult.Failure -> {
            oppiaLogger.d("SurveyActivity", "Failed to stop the survey session")
            activity.finish() // Can't recover from the session failing to stop.
          }
          is AsyncResult.Success -> {
            oppiaLogger.d("SurveyActivity", "Stopped the survey session")
            callback()
          }
        }
      }
    )
  }
}
