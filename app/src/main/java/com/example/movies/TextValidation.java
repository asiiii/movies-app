package com.example.movies;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

//Source: https://stackoverflow.com/questions/11698857/how-to-use-textwatcher-in-android?noredirect=1&lq=1
public abstract class TextValidation implements TextWatcher {
    //this class implements TetWatcher interface
    //and is used to check the inputs of the Register and Edit functions as the text entered changes realtime
    public final EditText textView;

    public TextValidation(EditText textView) {
        this.textView = textView;
    }

    public abstract void validate(EditText textView, String text);

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //validate method passes the EditText and its contents
        //validation is carried out at the respective class by a addTextChangedListener
        validate(textView, textView.getText().toString());
    }

}
