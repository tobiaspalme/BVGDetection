package de.htwberlin.f4.ai.ma.prototype_temp;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Override the standart Edit Text Element. It is now possible to see the value of the child element as summary.
 */
public class EditTextPreference extends android.preference.EditTextPreference {
    public EditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        return this.getText();
    }
}
