package com.example.uventawh;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

public class BoldStringBuilder extends SpannableStringBuilder {

    public SpannableStringBuilder spannableStringBuilder;

    public BoldStringBuilder() {
        this.spannableStringBuilder = new SpannableStringBuilder();
    }

    public void addString(String s){

        spannableStringBuilder.append(new SpannableString(s));

    }

    public void erase(){
        this.spannableStringBuilder = new SpannableStringBuilder();
    }

    public void addBoldString(String s){

        SpannableString spannableString = new SpannableString(s);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), 0);

        spannableStringBuilder.append(spannableString);

    }
}
