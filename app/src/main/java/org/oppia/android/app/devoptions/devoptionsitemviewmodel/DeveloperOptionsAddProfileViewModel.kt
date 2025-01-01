package org.oppia.android.app.devoptions.devoptionsitemviewmodel

import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import javax.inject.Inject
import org.oppia.android.R
import org.oppia.android.app.devoptions.AddProfilesClickListener
import org.oppia.android.app.devoptions.ForceCrashButtonClickListener
import org.oppia.android.app.devoptions.RouteToMathExpressionParserTestListener
import org.oppia.android.app.model.AddProfileActivityParams
import org.oppia.android.app.profile.AddProfileActivity
import org.oppia.android.app.profile.AddProfileActivityPresenter
import org.oppia.android.app.profile.ProfileChooserActivity
import org.oppia.android.databinding.AddProfileActivityBinding
import org.oppia.android.domain.devoptions.ShowAllHintsAndSolutionController
import org.oppia.android.domain.profile.ProfileManagementController
import org.oppia.android.util.data.AsyncResult
import org.oppia.android.util.data.DataProviders.Companion.toLiveData
import org.oppia.android.util.extensions.getProtoExtra

/**
 * [DeveloperOptionsItemViewModel] to provide features to test and debug math expressions and
 * equations.
 */
class DeveloperOptionsAddProfileViewModel(
  private val addProfilesClickListener: AddProfilesClickListener
) : DeveloperOptionsItemViewModel() {
  @Inject
  lateinit var addProfileFragmentPresenter: AddProfileActivityPresenter

   fun addThreeProfiles() {
    addProfilesClickListener.createThreeProfiles()
  }
  fun addTenProfiles() {
    addProfilesClickListener.createTenProfiles()
  }
  fun deleteProfiles() {
    addProfilesClickListener.deleteProfiles()
  }


}
