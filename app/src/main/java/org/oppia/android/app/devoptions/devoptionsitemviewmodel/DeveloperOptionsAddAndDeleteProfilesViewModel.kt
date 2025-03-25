package org.oppia.android.app.devoptions.devoptionsitemviewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import javax.inject.Inject
import org.oppia.android.app.devoptions.AddOneProfileButtonClickListener
import org.oppia.android.app.devoptions.AddThreeProfilesButtonClickListener
import org.oppia.android.app.devoptions.DeleteAllNonAdminProfilesButtonClickListener
import org.oppia.android.domain.profile.ProfileManagementController
import org.oppia.android.util.data.AsyncResult
import org.oppia.android.util.data.DataProviders.Companion.toLiveData

/**
 * [DeveloperOptionsItemViewModel] to provide features to to add and delete profiles such as
 * add one profile, add three profiles, delete all non admin profiles.
 */
class DeveloperOptionsAddAndDeleteProfilesViewModel(
  private val addOneProfileButtonClickListener: AddOneProfileButtonClickListener,
  private val addThreeProfilesButtonClickListener: AddThreeProfilesButtonClickListener,
  private val deleteAllNonAdminProfilesButtonClickListener:
    DeleteAllNonAdminProfilesButtonClickListener,
  private val profileManagementController: ProfileManagementController
) : DeveloperOptionsItemViewModel() {

  //subha

  // Convert AsyncResult<Int> to LiveData<Int>
  val profileCount: LiveData<Int> = Transformations.map(
    profileManagementController.getProfileCount().toLiveData()
  ) { asyncResult ->
    when (asyncResult) {
      is AsyncResult.Success -> asyncResult.value
      is AsyncResult.Failure -> 0 // Default to 0 if there's an error
      is AsyncResult.Pending -> 0 // Default to 0 while loading
    }
  }


  /** Adds one profile by triggering the [AddOneProfileButtonClickListener]. */
  fun addOneProfile() {
    addOneProfileButtonClickListener.createOneProfile()
  }

  /** Adds three profiles by triggering the [AddThreeProfilesButtonClickListener]. */
  fun addThreeProfiles() {
    addThreeProfilesButtonClickListener.createThreeProfiles()
  }

  /** Deletes all non-admin profiles by triggering the [DeleteAllNonAdminProfilesButtonClickListener]. */
  fun deleteAllNonAdminProfiles() {
    deleteAllNonAdminProfilesButtonClickListener.deleteAllNonAdminProfiles()
  }
}
