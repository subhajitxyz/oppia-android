package org.oppia.android.app.story

interface StoryFragmentScroller {
  /**
   * Scrolls smoothly (with animation) to the specified vertical pixel position in
   * [StoryFragment].
   */
  fun smoothScrollToPosition(position: Int)
}
