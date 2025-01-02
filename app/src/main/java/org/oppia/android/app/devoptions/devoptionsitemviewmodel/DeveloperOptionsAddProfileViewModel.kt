package org.oppia.android.app.devoptions.devoptionsitemviewmodel

import org.oppia.android.app.devoptions.AddOneProfileButtonClickListener
import org.oppia.android.app.devoptions.AddThreeProfilesButtonClickListener
import org.oppia.android.app.devoptions.DeleteAllNonAdminProfilesButtonClickListener

/**
 * [DeveloperOptionsItemViewModel] to provide features to test and debug math expressions and
 * equations.
 */
class DeveloperOptionsAddProfileViewModel(
  private val addOneProfileButtonClickListener: AddOneProfileButtonClickListener,
  private val addThreeProfilesButtonClickListener: AddThreeProfilesButtonClickListener,
  private val deleteAllNonAdminProfilesButtonClickListener: DeleteAllNonAdminProfilesButtonClickListener
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
