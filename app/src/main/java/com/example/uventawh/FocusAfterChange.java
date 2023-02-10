package com.example.uventawh;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

public class FocusAfterChange implements TextWatcher {

    View next;
    String enabled;

    public FocusAfterChange(final View next, String enabled) {

        this.next = next;
        this.enabled = enabled;

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (!enabled.contains(s.toString().toLowerCase())){
            s.clear();
        }

        if (s.length() > 0)

            next.requestFocus();
    }
}
