package org.oppia.android.app.databinding;

import android.text.TextWatcher;
import android.util.Log;
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

  @BindingAdapter("textChangedListener")
  public static void bindTextWatcher(@NonNull EditText editText, @Nullable TextWatcher textWatcher) {
    TextWatcher existingWatcher = (TextWatcher) editText.getTag(R.id.textWatcher);
    if (existingWatcher != null && existingWatcher != textWatcher) {
      editText.removeTextChangedListener(existingWatcher);
      Log.d("testtextinput", "Removed old TextWatcher: " + existingWatcher);
    }

    if (textWatcher != null) {
      editText.addTextChangedListener(textWatcher);
      editText.setTag(R.id.textWatcher, textWatcher);
      Log.d("testtextinput", "Added new TextWatcher: " + textWatcher);
    }
  }

}
