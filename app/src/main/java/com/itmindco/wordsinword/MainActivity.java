package com.itmindco.wordsinword;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;


public class MainActivity extends ActionBarActivity {

    public MainActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainMenu(), "MainMenu")
                    .commit();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreencall();
    }

    public void FullScreencall() {
        View decorView = getWindow().getDecorView();

        if (Build.VERSION.SDK_INT < 19 && Build.VERSION.SDK_INT > 11) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            decorView.setSystemUiVisibility(View.GONE);
        } else {

//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void StartGame(int mode) {
        Bundle args = new Bundle();
        args.putInt("mode", mode);
        GameFragment gf = new GameFragment();
        gf.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, gf, "Game")
                .commit();
    }

    public void showEndGameFragment(Bundle params) {
        EndGameFragment endGameFragment = new EndGameFragment();
        endGameFragment.setArguments(params);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, endGameFragment, "EndGame")
                .commit();
    }

    public void showMainMenu() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new MainMenu(), "MainMenu")
                .commit();
    }

    public DictionaryDb GetDB() {
        return ((WordsInWordApplication) getApplication()).db;
    }

    @Override
    public void onBackPressed() {

        Fragment mm = getSupportFragmentManager().findFragmentByTag("MainMenu");
        if (mm != null) {
            super.onBackPressed();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new MainMenu(), "MainMenu")
                .commit();

    }


}
