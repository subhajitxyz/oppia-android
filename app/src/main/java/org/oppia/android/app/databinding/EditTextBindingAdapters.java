package org.oppia.android.app.databinding;

import android.text.TextWatcher;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import org.oppia.android.R;

/** Holds all custom binding adapters that bind to [EditText]. */
public final class EditTextBindingAdapters {

  /** Binding adapter for setting a [TextWatcher] as a change listener for an [EditText]. */
//  @BindingAdapter("textChangedListener")
//  public static void bindTextWatcher(@NonNull EditText editText, TextWatcher textWatcher) {
//    editText.addTextChangedListener(textWatcher);
//  }

  //subha use text_watcher
  @BindingAdapter("textChangedListener")
  public static void bindTextWatcher(
      @NonNull EditText editText,
      @Nullable TextWatcher textWatcher
  ) {
    // 1. Remove existing TextWatcher if present
    Object existingTag = editText.getTag(R.id.text_watcher);
    if (existingTag instanceof TextWatcher) {
      editText.removeTextChangedListener((TextWatcher) existingTag);
    }

    // 2. Add new TextWatcher and store it in the tag
    if (textWatcher != null) {
      editText.addTextChangedListener(textWatcher);
      editText.setTag(R.id.text_watcher, textWatcher);
    } else {
      editText.setTag(R.id.text_watcher, null);
    }
  }
}

