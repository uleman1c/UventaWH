package com.example.uventawh;

import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class OnKeyListenerBefore implements View.OnKeyListener {

    EditText et1;

    public OnKeyListenerBefore(EditText et1) {
        this.et1 = et1;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (event.getAction() == android.view.KeyEvent.ACTION_DOWN
            && keyCode == android.view.KeyEvent.KEYCODE_DEL
            && ((EditText) v).getText().toString().isEmpty()){

                et1.setText("");
                et1.requestFocus();

        }

        return false;
    }
}
