package org.oppia.android.app.player.state.listener

import org.oppia.android.app.model.HelpIndex
import org.oppia.android.app.model.ProfileId

/** Listener for when an [ExplorationActivity] should route to a [HintsAndSolution]. */
interface RouteToHintsAndSolutionListener {
  fun routeToHintsAndSolution(id: String, helpIndex: HelpIndex)

  //subha just test -> if works i will shift this method in new interface
}
