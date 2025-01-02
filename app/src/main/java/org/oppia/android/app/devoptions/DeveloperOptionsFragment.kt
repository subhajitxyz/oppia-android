package org.oppia.android.app.devoptions

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.oppia.android.app.fragment.FragmentComponentImpl
import org.oppia.android.app.fragment.InjectableFragment
import javax.inject.Inject

/** Fragment that contains Developer Options of the application. */
class DeveloperOptionsFragment : InjectableFragment() {
  @Inject
  lateinit var developerOptionsFragmentPresenter: DeveloperOptionsFragmentPresenter

  companion object {
    fun newInstance(): DeveloperOptionsFragment {
      return DeveloperOptionsFragment()
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    (fragmentComponent as FragmentComponentImpl).inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return developerOptionsFragmentPresenter.handleCreateView(inflater, container)
  }

  fun deleteAllNonAdminProfiles() {
    developerOptionsFragmentPresenter.deleteAllNonAdminProfiles()
  }

  fun createProfile(count: Int) {
    developerOptionsFragmentPresenter.createProfile(count)
  }
}
