package com.itmindco.wordsinword;

import android.app.Application;


public class WordsInWordApplication extends Application {
    public DictionaryDb db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DictionaryDb(this);
    }
}
