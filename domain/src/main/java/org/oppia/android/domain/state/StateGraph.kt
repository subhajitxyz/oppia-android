package org.oppia.android.domain.state

import org.oppia.android.app.model.AnswerOutcome
import org.oppia.android.app.model.Outcome
import org.oppia.android.app.model.State

/**
 * Graph that provides lookup access for [State]s and functionality for processing the outcome of a submitted learner
 * answer.
 */
class StateGraph constructor(
  private var stateGraph: Map<String, State>
) {
  /** Resets this graph to the new graph represented by the specified [Map]. */
  fun reset(stateGraph: Map<String, State>) {
    this.stateGraph = stateGraph
  }

  /** Returns the [State] corresponding to the specified name. */
  fun getState(stateName: String): State {
    return stateGraph.getValue(stateName)
  }

  /** Returns an [AnswerOutcome] based on the current state and resulting [Outcome] from the learner's answer. */
  fun computeAnswerOutcomeForResult(currentState: State, outcome: Outcome): AnswerOutcome {
    val answerOutcomeBuilder = AnswerOutcome.newBuilder()
      .setFeedback(outcome.feedback)
      .setLabelledAsCorrectAnswer(outcome.labelledAsCorrect)
      .setState(currentState)
    when {
      outcome.refresherExplorationId.isNotEmpty() ->
        answerOutcomeBuilder.refresherExplorationId = outcome.refresherExplorationId
      outcome.missingPrerequisiteSkillId.isNotEmpty() ->
        answerOutcomeBuilder.missingPrerequisiteSkillId = outcome.missingPrerequisiteSkillId
      outcome.destStateName == currentState.name -> answerOutcomeBuilder.sameState = true
      //subha
      //we will compute previous_state_name for flashcard with proper condition
      //how can we optimize this condition -> if we can able to check the [outcome.destStateName present in statedeck earlier]
      //then we do not need to check [outcome.feedback.contentId contains feedback] because i have a doubt on this condition
      !outcome.labelledAsCorrect &&
        outcome.feedback.contentId.contains("feedback", true) -> answerOutcomeBuilder.flashbackStateName = outcome.destStateName
      else -> answerOutcomeBuilder.stateName = outcome.destStateName
    }
    return answerOutcomeBuilder.build()
  }
}
