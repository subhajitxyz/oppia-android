package org.oppia.android.app.topic.flashbackcard

import android.text.SpannableString
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import javax.inject.Inject
import org.oppia.android.app.player.state.itemviewmodel.StateItemViewModel
import org.oppia.android.app.viewmodel.ObservableArrayList
import org.oppia.android.app.viewmodel.ObservableViewModel

class FlashbackCardViewModel @Inject constructor(): ObservableViewModel(){
  //val contentSubtitledHtml = ObservableField<CharSequence>()
  //val htmlContent = ObservableField<CharSequence>()

//  fun updateFlashbackContent(newText: String) {
//    contentSubtitledHtml.set(newText)  // Updates UI automatically
//  }


  //subha mile 2.1
  val isSplitView = ObservableField(false)
  val centerGuidelinePercentage = ObservableField(0.5f)
  private val canSubmitAnswer = ObservableField(false)

  val itemList: ObservableList<StateItemViewModel> = ObservableArrayList()
  val rightItemList: ObservableList<StateItemViewModel> = ObservableArrayList()

  fun getCanSubmitAnswer(): ObservableField<Boolean> = canSubmitAnswer
}