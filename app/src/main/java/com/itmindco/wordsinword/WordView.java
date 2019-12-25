package com.itmindco.wordsinword;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;

public class WordView extends LinearLayout {
    private String tempWord = "";
    private ArrayList<WordViewListener> listeners;
    private float minTextSize = 10;
    private float maxTextSize = 15;

    public WordView(Context context) {
        super(context);
        init();
    }

    public WordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.word_view, this);

        if (isInEditMode()) return;

        listeners = new ArrayList<>(1);

        float fromValue = getResources().getDimension(R.dimen.ch_text_size_min);
        float toValue = getResources().getDimension(R.dimen.ch_text_size_max);

        minTextSize = fromValue;
        maxTextSize = toValue;

    }

    public void addWordViewListener(WordViewListener listener) {
        if (listener == null) return;
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeWordViewListener(WordViewListener listener) {
        if (listener == null) return;
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    private void onTextChanged() {
        for (WordViewListener listener : listeners) {
            listener.onTextChange(tempWord);
        }
    }

    public void setWord(String word) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.wordContainer);


        float k = 1f;

        if (word.length() <= 7) {
            k = 1.3f;
        }
        minTextSize *= k;
        maxTextSize *= k;

        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;

        for (char ch : word.toCharArray()) {
            final TextViewCheckable myText = new TextViewCheckable(getContext(), null);

            myText.setText(String.valueOf(ch));
            myText.setTextSize(minTextSize / scaledDensity);
            ll.addView(myText);
            myText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tapCh(v);
                }
            });

        }
    }

    public void tapCh(View v) {
        TextViewCheckable tw = (TextViewCheckable) v;
        String ch = (String) tw.getText();

        if (!tw.isChecked) {
            tw.isChecked = true;
            tw.setTextColor(getResources().getColor(R.color.accent));
            tempWord += ch;
            AnimCh(tw, minTextSize, maxTextSize);
        } else {
            if (tempWord.endsWith(ch)) {
                tempWord = tempWord.substring(0, tempWord.length() - 1);
                tw.setTextColor(getResources().getColor(R.color.text_secondary));
                tw.isChecked = false;
                AnimCh(tw, maxTextSize, minTextSize);
            }
        }


        updateView();
    }

    private void updateView() {
        TextView tmp = (TextView) findViewById(R.id.tempWord);
        tmp.setText(tempWord);
        if (!tempWord.isEmpty()) {
            onTextChanged();
        }
    }

    private void AnimCh(View v, float from, float to) {
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        float fromPX = from / scaledDensity;
        float toPX = to / scaledDensity;
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "textSize", fromPX, toPX);
        anim.setDuration(300);
        anim.start();
    }

    public void reset() {
        tempWord = "";
        updateView();

        LinearLayout ll = (LinearLayout) findViewById(R.id.wordContainer);
        for (int i = 0; i < ll.getChildCount(); i++) {
            TextViewCheckable tw = (TextViewCheckable) ll.getChildAt(i);
            if (tw.isChecked) {
                tw.isChecked = false;
                tw.setTextColor(getResources().getColor(R.color.text_secondary));
                AnimCh(tw, maxTextSize, minTextSize);
            }
        }
    }

    public void deleteLastChar() {
        if (tempWord.length() == 0) return;

        String lastCh = tempWord.substring(tempWord.length() - 1);
        tempWord = tempWord.substring(0, tempWord.length() - 1);
        updateView();

        LinearLayout ll = (LinearLayout) findViewById(R.id.wordContainer);
        for (int i = 0; i < ll.getChildCount(); i++) {
            TextViewCheckable tw = (TextViewCheckable) ll.getChildAt(i);
            String twCh = String.valueOf(tw.getText());
            if (twCh.equalsIgnoreCase(lastCh) && tw.isChecked) {
                tw.isChecked = false;
                tw.setTextColor(getResources().getColor(R.color.text_secondary));
                AnimCh(tw, maxTextSize, minTextSize);
                break;
            }
        }
    }

    public interface WordViewListener {
        public void onTextChange(String text);
    }
}
