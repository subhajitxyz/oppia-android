package org.oppia.android.app.topic.flashbackcard

import androidx.databinding.ObservableList
import javax.inject.Inject
import org.oppia.android.app.player.state.itemviewmodel.StateItemViewModel
import org.oppia.android.app.viewmodel.ObservableArrayList
import org.oppia.android.app.viewmodel.ObservableViewModel

class FlashbackCardViewModel @Inject constructor(): ObservableViewModel(){
  val itemList: ObservableList<StateItemViewModel> = ObservableArrayList()
}