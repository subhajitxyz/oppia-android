package org.oppia.android.app.topic.flashbackcard

import org.oppia.android.app.model.EphemeralState

interface FlashbackCardListener {
  fun routeToFlashBackCard(id: String, ephemeralState: EphemeralState)
}