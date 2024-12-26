package org.oppia.android.domain.state

import android.util.Log
import org.oppia.android.app.model.AnswerAndResponse
import org.oppia.android.app.model.CompletedState
import org.oppia.android.app.model.CompletedStateInCheckpoint
import org.oppia.android.app.model.EphemeralState
import org.oppia.android.app.model.ExplorationCheckpoint
import org.oppia.android.app.model.HelpIndex
import org.oppia.android.app.model.PendingState
import org.oppia.android.app.model.State
import org.oppia.android.app.model.SubtitledHtml
import org.oppia.android.app.model.UserAnswer

// TODO(#59): Hide the visibility of this class to domain implementations.
/**
 * Tracks the progress of a dynamic playing session through a graph of state cards. This class
 * treats the learner's progress like a deck of cards to simplify forward/backward navigation.
 */
class StateDeck constructor(
  initialState: State,
  private val isTopOfDeckTerminalChecker: (State) -> Boolean
) {
  private var pendingTopState: State = initialState
  private val previousStates: MutableList<EphemeralState> = ArrayList()
  private val currentDialogInteractions: MutableList<AnswerAndResponse> = ArrayList()
  private var stateIndex: Int = 0


  //private var previousRevisionStateIndex: Int = -1
  private var actualCallLearnAgain: Boolean = false


  /** Resets this deck to a new, specified initial [State]. */
  fun resetDeck(initialState: State) {
    pendingTopState = initialState
    previousStates.clear()
    currentDialogInteractions.clear()
    stateIndex = 0
  }

  /** Resumes this deck to continue the exploration from the last marked checkpoint. */
  fun resumeDeck(
    pendingTopState: State,
    previousStates: List<EphemeralState>,
    currentDialogInteractions: List<AnswerAndResponse>,
    stateIndex: Int
  ) {
    this.pendingTopState = pendingTopState
    this.previousStates.clear()
    this.currentDialogInteractions.clear()
    this.previousStates.addAll(previousStates)
    this.currentDialogInteractions.addAll(currentDialogInteractions)
    this.stateIndex = stateIndex
  }

  /** Navigates to the previous state in the deck, or fails if this isn't possible. */
  fun navigateToPreviousState() {
    check(!isCurrentStateInitial()) { "Cannot navigate to previous state; at initial state." }
    stateIndex--
  }


  /** Navigates to the next state in the deck, or fails if this isn't possible. */
  fun navigateToNextState() {
    check(!isCurrentStateTopOfDeck()) { "Cannot navigate to next state; at most recent state." }

    val revisionIdx = calculateWrongAnswerPreviousState(pendingTopState.name)

    if(revisionIdx != null && stateIndex == previousStates.size-1 && actualCallLearnAgain) {
      handleLearnAgainCondition(revisionIdx)
    } else {
      val previousState = previousStates[stateIndex]
      stateIndex++
      if (!previousState.hasNextState) {
        // Update the previous state to indicate that it has a next state now that its next state has
        // actually been created' by navigating to it.
        previousStates[stateIndex - 1] = previousState.toBuilder().setHasNextState(true).build()
      }
   }

  }

  /**
   * Returns the [State] corresponding to the latest card in the deck, regardless of whichever state
   * the learner is currently viewing.
   */
  fun getPendingTopState(): State = pendingTopState

  /** Returns the index of the current selected card of the deck. */
  fun getTopStateIndex(): Int = stateIndex

  /**
   * Returns the number of unique states that have been viewed so far in the deck (i.e. the size of
   * the deck).
   */
  fun getViewedStateCount(): Int = previousStates.size

  /** Returns the current [State] being viewed by the learner. */
  fun getCurrentState(): State {
    return when {
      isCurrentStateTopOfDeck() -> pendingTopState
      else -> previousStates[stateIndex].state
    }
  }

  /** Returns the current [EphemeralState] the learner is viewing. */
  fun getCurrentEphemeralState(
    helpIndex: HelpIndex,
    timestamp: Long,
    isContinueButtonAnimationSeen: Boolean
  ): EphemeralState {
    // Note that the terminal state is evaluated first since it can only return true if the current
    // state is the top of the deck, and that state is the terminal one. Otherwise the terminal
    // check would never be triggered since the second case assumes the top of the deck must be
    // pending.
    //subha
    Log.d("teststate","i am in getCurrentEphemeralState")
    if(isCurrentStateTerminal()) {
      Log.d("teststate","first")
    }
    if(isCurrentStateTopOfDeck()) {
      Log.d("teststate","2nd")
    }
    return when {
      isCurrentStateTerminal() -> getCurrentTerminalState()
      isCurrentStateTopOfDeck() -> getCurrentPendingState(
        helpIndex,
        timestamp,
        isContinueButtonAnimationSeen
      )
      else -> getPreviousState()
    }
  }

  /**
   * Pushes a new [State] onto the deck.
   *
   * This operation cannot happen if the learner isn't at the most recent state, if the current
   * state is not terminal, or if the learner hasn't submitted an answer to the most recent state.
   * This operation implies that the most recently submitted answer was the correct answer to the
   * previously current state. This does NOT change the user's position in the deck, it just marks
   * the current state as completed.
   *
   * @param prohibitSameStateName whether to enable a sanity check to ensure the same state isn't
   *     routed to twice
   */
  fun pushState(
    state: State,
    prohibitSameStateName: Boolean,
    timestamp: Long,
    isContinueButtonAnimationSeen: Boolean
  ) {

    Log.d("testidea2","i am into push state ")

    check(isCurrentStateTopOfDeck()) {
      Log.d("testidea2","i am here 1")
      "Cannot push a new state unless the learner is at the most recent state."
    }
    check(!isCurrentStateTerminal()) {
      Log.d("testidea2","i am here 2")
      "Cannot push another state after reaching a terminal state."
    }
    check(currentDialogInteractions.size != 0) {
      Log.d("testidea2","i am here 3")
      "Cannot push another state without an answer."
    }
    if (prohibitSameStateName) {
      check(state.name != pendingTopState.name) {
        Log.d("testidea2","i am here 4")
        "Cannot route from the same state to itself as a new card."
      }
    }

    // NB: This technically has a 'next' state, but it's not marked until it's first navigated away
    // since the new state doesn't become fully realized until navigated to.
    //subha


      previousStates += EphemeralState.newBuilder()
        .setState(pendingTopState)
        .setHasPreviousState(!isCurrentStateInitial())
        .setCompletedState(CompletedState.newBuilder().addAllAnswer(currentDialogInteractions))
        .setContinueButtonAnimationTimestampMs(timestamp)
        .setShowContinueButtonAnimation(!isContinueButtonAnimationSeen && isCurrentStateInitial())
        .build()

    //subha change in idea 1
//    if(calculateWrongAnswerPreviousState(state.name) == -1) {
//      Log.d("testdialoginter","i have cleared the diloginteractione")
//      currentDialogInteractions.clear()
//    }
    if(!actualCallLearnAgain) {
      Log.d("testdialoginter","i have cleared the diloginteractione")
      currentDialogInteractions.clear()
    }

      pendingTopState = state

  }
  //subha
  private fun calculateWrongAnswerPreviousState(stateName: String) : Int? {
    if(!actualCallLearnAgain) return null
    for (i in previousStates.size - 1 downTo 0) {
      val state = previousStates[i].state
      if (state.name == stateName) {
        Log.d("testidea1","found at $i")
        //previousRevisionStateIndex = i
        return i
      }
    }
    return null
  }
  //subha
  fun onActualCallLearnAgain(value: Boolean) {
    actualCallLearnAgain = value
  }

  private fun handleLearnAgainCondition(revisionIdx: Int) {
  //subha
    Log.d("testmyfun", "my condition applied")

    //update the previousStates list
    val timestamp = previousStates[previousStates.size - 1].continueButtonAnimationTimestampMs
    val showContinueButtonSeen =
      previousStates[previousStates.size - 1].showContinueButtonAnimation
    val currState = previousStates[previousStates.size - 1].state

    previousStates.removeAt(previousStates.size - 1)
    //mark previousstate 's last state as pending
    //we can only do this
    //pendingTopState = currState
    //you need to work on that
    pendingTopState = EphemeralState.newBuilder()
      .setState(currState)
      .setHasPreviousState(!isCurrentStateInitial())
      .setPendingState(PendingState.newBuilder().addAllWrongAnswer(currentDialogInteractions))
      .setContinueButtonAnimationTimestampMs(timestamp)
      .setShowContinueButtonAnimation(showContinueButtonSeen)
      .build().state

    // now move in revision idx
    stateIndex = revisionIdx
    onActualCallLearnAgain(false)

  }


  /**
   * Submits an answer & feedback dialog the learner experience in the current state.
   *
   * This operation fails if the user is not at the most recent state in the deck, or if the most
   * recent state is terminal (since no answer can be submitted to a terminal interaction).
   */
  fun submitAnswer(userAnswer: UserAnswer, feedback: SubtitledHtml, isCorrectAnswer: Boolean) {
    check(isCurrentStateTopOfDeck()) { "Cannot submit an answer except to the most recent state." }
    check(!isCurrentStateTerminal()) { "Cannot submit an answer to a terminal state." }
    currentDialogInteractions += AnswerAndResponse.newBuilder()
      .setUserAnswer(userAnswer)
      .setFeedback(feedback)
      .setIsCorrectAnswer(isCorrectAnswer)
      .build()
  }

  /**
   * Returns an [ExplorationCheckpoint] which contains all the latest values of variables of the
   * [StateDeck] that are used in light weight checkpointing.
   */
  fun createExplorationCheckpoint(
    explorationVersion: Int,
    explorationTitle: String,
    timestamp: Long,
    helpIndex: HelpIndex
  ): ExplorationCheckpoint {
    return ExplorationCheckpoint.newBuilder().apply {
      addAllCompletedStatesInCheckpoint(
        previousStates.map { state ->
          CompletedStateInCheckpoint.newBuilder().apply {
            completedState = state.completedState
            stateName = state.state.name
          }.build()
        }
      )
      pendingStateName = pendingTopState.name
      addAllPendingUserAnswers(currentDialogInteractions)
      this.stateIndex = this@StateDeck.stateIndex
      this.explorationVersion = explorationVersion
      this.explorationTitle = explorationTitle
      timestampOfFirstCheckpoint = timestamp
      this.helpIndex = helpIndex
    }.build()
  }

  private fun getCurrentPendingState(
    helpIndex: HelpIndex,
    timestamp: Long,
    isContinueButtonAnimationSeen: Boolean
  ): EphemeralState {
    return EphemeralState.newBuilder()
      .setState(pendingTopState)
      .setHasPreviousState(!isCurrentStateInitial())
      .setPendingState(
        PendingState.newBuilder()
          .addAllWrongAnswer(currentDialogInteractions)
          .setHelpIndex(helpIndex)
      )
      .setContinueButtonAnimationTimestampMs(timestamp)
      .setShowContinueButtonAnimation(!isContinueButtonAnimationSeen && isCurrentStateInitial())
      .build()
  }

  private fun getCurrentTerminalState(): EphemeralState {
    return EphemeralState.newBuilder()
      .setState(pendingTopState)
      .setHasPreviousState(!isCurrentStateInitial())
      .setTerminalState(true)
      .build()
  }


  private fun getPreviousState(): EphemeralState {
    return previousStates[stateIndex]
  }

  /** Returns whether the current scrolled state is the first state of the exploration. */
  private fun isCurrentStateInitial(): Boolean {
    return stateIndex == 0
  }

  /** Returns whether the current scrolled state is the most recent state played by the learner. */
  fun isCurrentStateTopOfDeck(): Boolean {
    return stateIndex == previousStates.size
  }

  /** Returns whether the current state is terminal. */
  private fun isCurrentStateTerminal(): Boolean {
    // Cards not on top of the deck cannot be terminal/the terminal card must be the last card in
    // the deck, if it's present.
    return isCurrentStateTopOfDeck() && isTopOfDeckTerminal()
  }

  /** Returns whether the most recent card on the deck is terminal. */
  private fun isTopOfDeckTerminal(): Boolean {
    return isTopOfDeckTerminalChecker(pendingTopState)
  }
}
