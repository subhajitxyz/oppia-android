package org.oppia.android.app.databinding;

import android.text.TextWatcher;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import org.oppia.android.R;

/** Holds all custom binding adapters that bind to [EditText]. */
public final class EditTextBindingAdapters {

  /**
   * Binding adapter for setting a [TextWatcher] as a change listener for an [EditText].
   */
//  @BindingAdapter("textChangedListener")
//  public static void bindTextWatcher(@NonNull EditText editText, TextWatcher textWatcher) {
//    //editText.removeTextChangedListener(textWatcher);
//    editText.addTextChangedListener(textWatcher);
//  }

  //subha use text_watcher
  @BindingAdapter("textChangedListener")
  public static void bindTextWatcher(@NonNull EditText editText, TextWatcher textWatcher) {
    // Remove any existing TextWatcher to prevent multiple instances
    TextWatcher existingWatcher = (TextWatcher) editText.getTag(R.id.textWatcher);
    if (existingWatcher != null) {
      editText.removeTextChangedListener(existingWatcher);
    }

    // Add the new TextWatcher and store it as a tag
    editText.addTextChangedListener(textWatcher);
    editText.setTag(R.id.textWatcher, textWatcher);
  }
}


