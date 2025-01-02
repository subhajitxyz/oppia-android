package org.oppia.android.app.devoptions

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.LinearLayoutManager
import org.oppia.android.app.devoptions.devoptionsitemviewmodel.DeveloperOptionsItemViewModel
import org.oppia.android.app.devoptions.devoptionsitemviewmodel.DeveloperOptionsModifyLessonProgressViewModel
import org.oppia.android.app.devoptions.devoptionsitemviewmodel.DeveloperOptionsOverrideAppBehaviorsViewModel
import org.oppia.android.app.devoptions.devoptionsitemviewmodel.DeveloperOptionsTestParsersViewModel
import org.oppia.android.app.devoptions.devoptionsitemviewmodel.DeveloperOptionsViewLogsViewModel
import org.oppia.android.app.fragment.FragmentScope
import org.oppia.android.app.recyclerview.BindableAdapter
import org.oppia.android.databinding.DeveloperOptionsFragmentBinding
import org.oppia.android.databinding.DeveloperOptionsModifyLessonProgressViewBinding
import org.oppia.android.databinding.DeveloperOptionsOverrideAppBehaviorsViewBinding
import org.oppia.android.databinding.DeveloperOptionsTestParsersViewBinding
import org.oppia.android.databinding.DeveloperOptionsViewLogsViewBinding
import javax.inject.Inject
import org.oppia.android.R
import org.oppia.android.app.devoptions.devoptionsitemviewmodel.DeveloperOptionsAddProfileViewModel
import org.oppia.android.app.model.Profile
import org.oppia.android.app.model.ProfileDatabase
import org.oppia.android.app.profile.ProfileChooserActivity
import org.oppia.android.data.persistence.PersistentCacheStore
import org.oppia.android.databinding.DeveloperOptionsAddProfileBinding
import org.oppia.android.domain.oppialogger.LoggingIdentifierController
import org.oppia.android.domain.oppialogger.OppiaLogger
import org.oppia.android.domain.profile.ProfileManagementController
import org.oppia.android.util.data.AsyncResult
import org.oppia.android.util.data.DataProvider
import org.oppia.android.util.data.DataProviders.Companion.toLiveData
import org.oppia.android.util.profile.DirectoryManagementUtil

private val COLORS_LIST = listOf(
  R.color.component_color_avatar_background_1_color,
  R.color.component_color_avatar_background_2_color,
  R.color.component_color_avatar_background_3_color,
  R.color.component_color_avatar_background_4_color,
  R.color.component_color_avatar_background_5_color,
  R.color.component_color_avatar_background_6_color,
  R.color.component_color_avatar_background_7_color,
  R.color.component_color_avatar_background_8_color,
  R.color.component_color_avatar_background_9_color,
  R.color.component_color_avatar_background_10_color,
  R.color.component_color_avatar_background_11_color,
  R.color.component_color_avatar_background_12_color,
  R.color.component_color_avatar_background_13_color,
  R.color.component_color_avatar_background_14_color,
  R.color.component_color_avatar_background_15_color,
  R.color.component_color_avatar_background_16_color,
  R.color.component_color_avatar_background_17_color,
  R.color.component_color_avatar_background_18_color,
  R.color.component_color_avatar_background_19_color,
  R.color.component_color_avatar_background_20_color,
  R.color.component_color_avatar_background_21_color,
  R.color.component_color_avatar_background_22_color,
  R.color.component_color_avatar_background_23_color,
  R.color.component_color_avatar_background_24_color
)

private val PRE_DEFINED_NAMES_LIST = listOf(
  "Ben", "Nikita", "Adhiambo", "Sean", "Saptak", "Vishwajit", "Subhajit", "Aarav", "Emily", "Fatima"
)

/** The presenter for [DeveloperOptionsFragment]. */
@FragmentScope
class DeveloperOptionsFragmentPresenter @Inject constructor(
  private val activity: AppCompatActivity,
  private val fragment: Fragment,
  private val multiTypeBuilderFactory: BindableAdapter.MultiTypeBuilder.Factory,
  private val profileManagementController: ProfileManagementController,
  private val oppiaLogger: OppiaLogger,
  ) {

  private lateinit var binding: DeveloperOptionsFragmentBinding
  private lateinit var linearLayoutManager: LinearLayoutManager

  @Inject
  lateinit var developerOptionsViewModel: DeveloperOptionsViewModel

  fun handleCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?
  ): View? {
    binding = DeveloperOptionsFragmentBinding.inflate(
      inflater,
      container,
      /* attachToRoot= */ false
    )

    linearLayoutManager = LinearLayoutManager(activity.applicationContext)

    binding.developerOptionsList.apply {
      layoutManager = linearLayoutManager
      adapter = createRecyclerViewAdapter()
    }

    binding.apply {
      this.viewModel = developerOptionsViewModel
      this.lifecycleOwner = fragment
    }

    return binding.root
  }

  private fun createRecyclerViewAdapter(): BindableAdapter<DeveloperOptionsItemViewModel> {
    return multiTypeBuilderFactory.create<DeveloperOptionsItemViewModel, ViewType> { viewModel ->
      when (viewModel) {
        is DeveloperOptionsModifyLessonProgressViewModel -> {
          viewModel.itemIndex.set(0)
          ViewType.VIEW_TYPE_MODIFY_LESSON_PROGRESS
        }
        is DeveloperOptionsViewLogsViewModel -> {
          viewModel.itemIndex.set(1)
          ViewType.VIEW_TYPE_VIEW_LOGS
        }
        is DeveloperOptionsOverrideAppBehaviorsViewModel -> {
          viewModel.itemIndex.set(2)
          ViewType.VIEW_TYPE_OVERRIDE_APP_BEHAVIORS
        }
        is DeveloperOptionsTestParsersViewModel -> {
          viewModel.itemIndex.set(3)
          ViewType.VIEW_TYPE_TEST_PARSERS
        }
        //subha
        is DeveloperOptionsAddProfileViewModel -> {
          viewModel.itemIndex.set(4)
          ViewType.VIEW_TYPE_ADD_AND_DELETE_PROFILES
        }
        else -> throw IllegalArgumentException("Encountered unexpected view model: $viewModel")
      }
    }
      .registerViewDataBinder(
        viewType = ViewType.VIEW_TYPE_MODIFY_LESSON_PROGRESS,
        inflateDataBinding = DeveloperOptionsModifyLessonProgressViewBinding::inflate,
        setViewModel = DeveloperOptionsModifyLessonProgressViewBinding::setViewModel,
        transformViewModel = { it as DeveloperOptionsModifyLessonProgressViewModel }
      )
      .registerViewDataBinder(
        viewType = ViewType.VIEW_TYPE_VIEW_LOGS,
        inflateDataBinding = DeveloperOptionsViewLogsViewBinding::inflate,
        setViewModel = DeveloperOptionsViewLogsViewBinding::setViewModel,
        transformViewModel = { it as DeveloperOptionsViewLogsViewModel }
      )
      .registerViewDataBinder(
        viewType = ViewType.VIEW_TYPE_OVERRIDE_APP_BEHAVIORS,
        inflateDataBinding = DeveloperOptionsOverrideAppBehaviorsViewBinding::inflate,
        setViewModel = DeveloperOptionsOverrideAppBehaviorsViewBinding::setViewModel,
        transformViewModel = { it as DeveloperOptionsOverrideAppBehaviorsViewModel }
      )
      .registerViewDataBinder(
        viewType = ViewType.VIEW_TYPE_TEST_PARSERS,
        inflateDataBinding = DeveloperOptionsTestParsersViewBinding::inflate,
        setViewModel = DeveloperOptionsTestParsersViewBinding::setViewModel,
        transformViewModel = { it as DeveloperOptionsTestParsersViewModel }
      )
      .registerViewDataBinder(
        viewType = ViewType.VIEW_TYPE_ADD_AND_DELETE_PROFILES,
        inflateDataBinding = DeveloperOptionsAddProfileBinding::inflate,
        setViewModel = DeveloperOptionsAddProfileBinding::setViewModel,
        transformViewModel = { it as DeveloperOptionsAddProfileViewModel }
      )
      //subha
      .build()
  }

  private enum class ViewType {
    VIEW_TYPE_MODIFY_LESSON_PROGRESS,
    VIEW_TYPE_VIEW_LOGS,
    VIEW_TYPE_OVERRIDE_APP_BEHAVIORS,
    VIEW_TYPE_TEST_PARSERS,
    VIEW_TYPE_ADD_AND_DELETE_PROFILES //subha
  }

  fun deleteAllNonAdminProfiles() {
    profileManagementController.deleteAllNonAdminProfiles().toLiveData()
      .observe(
        fragment,
        Observer {
          if (it is AsyncResult.Success) {
            val intent = Intent(fragment.requireContext(), ProfileChooserActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            fragment.startActivity(intent)
          }
        }
      )
  }

  fun createProfile(count: Int) {
    //val nameList = listOf("Ben", "Nikita", "Adhiambo", "Sean", "Saptak", "Vishwajit", "Subhajit")
    val existingProfileNameList = mutableListOf<String>()

    // Observe the existingProfiles LiveData
    existingProfiles.observeOnce(activity) { profileList ->
      existingProfileNameList.clear()
      profileList.forEach {
        existingProfileNameList.add(it.name)
      }

      val newNames = PRE_DEFINED_NAMES_LIST.filter { !existingProfileNameList.contains(it) }.take(count)
      newNames.forEach { newName ->
        val rgbColor = selectRandomColor()
        profileManagementController.addProfile(
          name = newName,
          pin = "",
          avatarImagePath = null,
          allowDownloadAccess = true,
          colorRgb = rgbColor,
          isAdmin = false
        )
      }

      val intent = Intent(fragment.requireContext(), ProfileChooserActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      }
      fragment.startActivity(intent)
    }
  }

  private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) {
    observe(lifecycleOwner, object : Observer<T> {
      override fun onChanged(value: T) {
        observer(value)
        // Remove observer after the first update
        removeObserver(this)
      }
    })
  }

  /** Randomly selects a color for the new profile. */
  private fun selectRandomColor(): Int {
    return COLORS_LIST.map {
      ContextCompat.getColor(activity, it)
    }.random()
  }

  private val existingProfiles: LiveData<List<Profile>> by lazy {
    Transformations.map(
      profileManagementController.getProfiles().toLiveData(),
      ::processGetProfilesResult
    )
  }

  private fun processGetProfilesResult(profilesResult: AsyncResult<List<Profile>>): List<Profile> {
    val profileList = when (profilesResult) {
      is AsyncResult.Failure -> {
        oppiaLogger.e(
          "DeveloperOptionsFragment",
          "Failed to retrieve the list of profiles", profilesResult.error
        )
        emptyList()
      }
      is AsyncResult.Pending -> emptyList()
      is AsyncResult.Success -> profilesResult.value
    }
    return profileList
  }

}
