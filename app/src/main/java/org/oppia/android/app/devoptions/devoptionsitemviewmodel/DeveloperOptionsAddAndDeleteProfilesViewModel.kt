package org.oppia.android.app.devoptions.devoptionsitemviewmodel

import org.oppia.android.app.devoptions.AddOneProfileButtonClickListener
import org.oppia.android.app.devoptions.AddThreeProfilesButtonClickListener
import org.oppia.android.app.devoptions.DeleteAllNonAdminProfilesButtonClickListener

/**
 * [DeveloperOptionsItemViewModel] to provide features to modify lesson progress such as
 * marking chapters completed, marking stories completed and marking topics completed.
 */
class DeveloperOptionsAddAndDeleteProfilesViewModel(
  private val addOneProfileButtonClickListener: AddOneProfileButtonClickListener,
  private val addThreeProfilesButtonClickListener: AddThreeProfilesButtonClickListener,
  private val deleteAllNonAdminProfilesButtonClickListener:
  DeleteAllNonAdminProfilesButtonClickListener
) : DeveloperOptionsItemViewModel() {

  fun addOneProfile() {
    addOneProfileButtonClickListener.createOneProfile()
  }

  fun addThreeProfiles() {
    addThreeProfilesButtonClickListener.createThreeProfiles()
  }

  fun deleteAllNonAdminProfiles() {
    deleteAllNonAdminProfilesButtonClickListener.deleteAllNonAdminProfiles()
  }
}