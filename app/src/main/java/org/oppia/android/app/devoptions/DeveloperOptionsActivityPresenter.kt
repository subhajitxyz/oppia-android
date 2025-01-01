package org.oppia.android.app.devoptions

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import org.oppia.android.R
import org.oppia.android.app.activity.ActivityScope
import org.oppia.android.app.drawer.NavigationDrawerFragment
import org.oppia.android.databinding.DeveloperOptionsActivityBinding
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.yield
import org.oppia.android.app.administratorcontrols.AdministratorControlsActivity
import org.oppia.android.app.model.AddProfileActivityParams
import org.oppia.android.app.model.Profile
import org.oppia.android.app.model.ProfileId
import org.oppia.android.app.profile.AddProfileActivity
import org.oppia.android.app.profile.ProfileChooserActivity
import org.oppia.android.app.profile.ProfileChooserViewModel
import org.oppia.android.app.settings.profile.ProfileListActivity
import org.oppia.android.app.translation.AppLanguageResourceHandler
import org.oppia.android.domain.oppialogger.OppiaLogger
import org.oppia.android.domain.profile.ProfileManagementController
import org.oppia.android.util.data.AsyncResult
import org.oppia.android.util.data.DataProviders.Companion.toLiveData
import org.oppia.android.util.extensions.getProtoExtra

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

/** The presenter for [DeveloperOptionsActivity]. */
@ActivityScope
class DeveloperOptionsActivityPresenter @Inject constructor(
  private val activity: AppCompatActivity,
  private val profileManagementController: ProfileManagementController,
  private val resourceHandler: AppLanguageResourceHandler,
  private val oppiaLogger: OppiaLogger
) {
  private lateinit var navigationDrawerFragment: NavigationDrawerFragment
  private lateinit var binding: DeveloperOptionsActivityBinding

  fun handleOnCreate() {
    binding = DataBindingUtil.setContentView(
      activity,
      R.layout.developer_options_activity
    )
    setUpNavigationDrawer()
    val previousFragment = getDeveloperOptionsFragment()
    if (previousFragment == null) {
      activity.supportFragmentManager.beginTransaction().add(
        R.id.developer_options_fragment_placeholder,
        DeveloperOptionsFragment.newInstance()
      ).commitNow()
    }
  }

  private fun setUpNavigationDrawer() {
    val toolbar = binding.developerOptionsActivityToolbar
    activity.setSupportActionBar(toolbar)
    activity.supportActionBar!!.setDisplayShowHomeEnabled(true)
    navigationDrawerFragment = activity
      .supportFragmentManager
      .findFragmentById(
        R.id.developer_options_activity_fragment_navigation_drawer
      ) as NavigationDrawerFragment
    navigationDrawerFragment.setUpDrawer(
      binding.developerOptionsActivityDrawerLayout,
      toolbar, menuItemId = -1
    )
  }

  private fun getDeveloperOptionsFragment(): DeveloperOptionsFragment? {
    return activity
      .supportFragmentManager
      .findFragmentById(
        R.id.developer_options_fragment_placeholder
      ) as DeveloperOptionsFragment?
  }

  /** Called when the 'force crash' button is clicked by the user. This function crashes the app and will not return. */
  fun forceCrash(): Nothing {
    throw RuntimeException("Force crash occurred")
  }

  //test
  fun showToast() {
    getDeveloperOptionsFragment()?.showToast()
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

  fun createMyProfileFromDeveloperOption_withoutSuspend(count: Int) {
    getDeveloperOptionsFragment()?.createMyProfileFromDeveloperOption_withoutSuspend(count)
//    val nameList = listOf("Ben", "subha", "Nikita", "adhiambo", "sean", "saptak", "vishwajit")
//    val existingProfileList = mutableListOf<String>()
//
//    // Observe the existingProfiles LiveData
//    existingProfiles.observeOnce(activity) { profileList ->
//      // Clear and populate the existingProfileList
//      existingProfileList.clear()
//      profileList.forEach {
//        existingProfileList.add(it.name)
//        Log.d("logjob", "Observed profile: ${it.name}")
//      }
//
//      // Filter names to add
//      val newNames = nameList.filter { !existingProfileList.contains(it) }.take(count)
//
//      // Add new profiles
//      newNames.forEach { newName ->
//        Log.d("logjob", "Added profile: $newName")
//        val rgbColor = selectRandomColor()
//        profileManagementController.addProfile(
//          name = newName,
//          pin = "",
//          avatarImagePath = null,
//          allowDownloadAccess = true,
//          colorRgb = rgbColor,
//          isAdmin = false
//        )
//      }
//
//      // Process profiles
//      Log.d("logjob", "Changed activity")
//      Toast.makeText(activity.applicationContext, "All Profiles created", Toast.LENGTH_SHORT).show()
//      val intent = Intent(activity, ProfileChooserActivity::class.java).apply {
//        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//      }
//      activity.startActivity(intent)
//      activity.finish()
//    }
  }

  fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) {
    observe(lifecycleOwner, object : Observer<T> {
      override fun onChanged(value: T) {
        observer(value)
        // Remove observer after the first update
        removeObserver(this)
      }
    })
  }



  suspend fun createMyProfileFromDeveloperOption(count: Int) {
    val nameList = listOf("Ben", "subha", "Nikita", "adhiambo", "sean", "saptak", "vishwajit")

    val existingProfileList = mutableListOf<String>()

    suspendCoroutine<Unit> { continuation ->
      val observer = Observer<List<Profile>> { profileList ->
        existingProfileList.clear()
        profileList.forEach {
          existingProfileList.add(it.name)
          Log.d("logjob", "Observed profile: ${it.name}")
        }
        // Resume the coroutine after data is observed
        continuation.resume(Unit)
      }
      existingProfiles.observeOnce(activity, observer)
    }
    Log.d("logjob", "after join: " + existingProfileList.toString())

    val newNames = nameList
      .filter { !existingProfileList.contains(it) }
      .take(count)

    newNames.forEach { newName ->
      Log.d("logjob", "added profilee $newName")
      val rgbColor = selectRandomColor()
      profileManagementController
        .addProfile(
          name = newName,
          pin = "",
          null,
          allowDownloadAccess = true,
          colorRgb = rgbColor,
          isAdmin = false
        )

    }
    // Process profiles
    Log.d("logjob", "changed activity")
    Toast.makeText(activity.applicationContext,"All Profiles created",Toast.LENGTH_SHORT).show()
    val intent = Intent(activity, ProfileChooserActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    activity.startActivity(intent)
    activity.finish()
  }

  private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
      override fun onChanged(value: T) {
        removeObserver(this)
        observer.onChanged(value)
      }
    })
  }

  fun deleteProfileFromDeveloperOption() {
    getDeveloperOptionsFragment()?.deleteProfileFromDeveloperOption()
//    profileManagementController.deleteAllProfilesExceptAdmin().toLiveData()
//      .observe(
//        activity,
//        Observer {
//          if (it is AsyncResult.Success) {
//            val intent = Intent(activity, ProfileChooserActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            activity.startActivity(intent)
//          }
//        }
//      )
  }

  //subha
  private fun handleAddProfileResultDeveloper(
    result: AsyncResult<Any?>
  ) {
    when (result) {
      is AsyncResult.Success -> {

      }
      is AsyncResult.Failure -> {
        when (result.error) {
          is ProfileManagementController.ProfileNameNotUniqueException ->
            Toast.makeText(
              activity.applicationContext, resourceHandler.getStringInLocale(
                R.string.add_profile_error_name_not_unique
              ), Toast.LENGTH_SHORT
            ).show()

          is ProfileManagementController.ProfileNameOnlyLettersException ->
            Toast.makeText(
              activity.applicationContext, resourceHandler.getStringInLocale(
                R.string.add_profile_error_name_only_letters
              ), Toast.LENGTH_SHORT
            ).show()

        }
      }
      is AsyncResult.Pending -> {} // Wait for an actual result.
    }
  }

  private fun processGetProfilesResult(profilesResult: AsyncResult<List<Profile>>): List<Profile> {
    val profileList = when (profilesResult) {
      is AsyncResult.Failure -> {
        oppiaLogger.e(
          "OnboardingFragment", "Failed to retrieve the list of profiles", profilesResult.error
        )
        emptyList()
      }
      is AsyncResult.Pending -> emptyList()
      is AsyncResult.Success -> profilesResult.value
    }
    return profileList
  }



  //try to add profile
  fun createMyProfileFromDeveloperOption_using_job() {
    val nameList = listOf("Ben", "subha", "Nikita", "adhiambo", "sean", "saptak", "vishwajit")

//    val job = CoroutineScope(Dispatchers.Main).async {
//      suspendCoroutine<Unit> { continuation ->
//        existingProfiles.observe(activity) { profileList ->
//          if(isActive) {
//            existingProfileList.clear()
//            profileList.forEach {
//              existingProfileList.add(it.name)
//              Log.d("logjob", "in join: " + it.name.toString())
//            }
//            // Resume the coroutine after data is observed
//            continuation.resume(Unit)
//          }
//
//        }
//      }
//    }
//
//    job.await()
//    job.cancel()

    val existingList = mutableListOf<String>()
    Log.d("testsubha",existingList.toString())
    val newNames = nameList
      .filter { !existingList.contains(it) } // Filter out existing names
      .take(3) // Limit to 3 names

        newNames.forEach { newName ->
          val rgbColor = selectRandomColor()

          profileManagementController
            .addProfile(
              name = newName,
              pin = "",
              null,
              allowDownloadAccess = true,
              colorRgb = rgbColor,
              isAdmin = false
            ).toLiveData()
            .observe(activity) { result ->
              when (result) {
                is AsyncResult.Success -> {

                  Log.d("testname", "Profile created: $newName")
                }
                is AsyncResult.Failure -> {
                  Log.d("testname", "Profile created: $newName")
                }
              }
            }
        }


    Toast.makeText(activity.applicationContext,"All Profiles created",Toast.LENGTH_SHORT).show()
    val intent = Intent(activity, ProfileChooserActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    activity.startActivity(intent)

  }

}
