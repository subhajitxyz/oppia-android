package org.oppia.android.app.devoptions.devoptionsitemviewmodel


import javax.inject.Inject
import org.oppia.android.app.devoptions.AddProfilesClickListener
import org.oppia.android.app.profile.AddProfileActivityPresenter

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
