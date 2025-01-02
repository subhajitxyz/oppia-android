package org.oppia.android.app.devoptions.devoptionsitemviewmodel

import org.oppia.android.app.devoptions.AddOneProfileButtonClickListener
import org.oppia.android.app.devoptions.AddThreeProfilesButtonClickListener
import org.oppia.android.app.devoptions.DeleteAllNonAdminProfilesButtonClickListener

/**
 * [DeveloperOptionsItemViewModel] to provide features to to add and delete profiles such as
 * add one profile, add three profiles, delete all non admin profiles.
 */
class DeveloperOptionsAddAndDeleteProfilesViewModel(
  private val addOneProfileButtonClickListener: AddOneProfileButtonClickListener,
  private val addThreeProfilesButtonClickListener: AddThreeProfilesButtonClickListener,
  private val deleteAllNonAdminProfilesButtonClickListener:
    DeleteAllNonAdminProfilesButtonClickListener
) : DeveloperOptionsItemViewModel() {

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
