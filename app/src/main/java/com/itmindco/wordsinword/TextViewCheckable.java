package com.itmindco.wordsinword;


import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class TextViewCheckable extends TextView {

    public boolean isChecked;

    public TextViewCheckable(Context context, AttributeSet attrs) {
        super(context, attrs);
        isChecked = false;
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.ch_text_size_min));
    }

}
