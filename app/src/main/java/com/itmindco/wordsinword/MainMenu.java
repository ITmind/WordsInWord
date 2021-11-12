package com.itmindco.wordsinword;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MainMenu extends Fragment {

    public MainMenu() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final MainActivity app = (MainActivity) getActivity();

        View rootView = inflater.inflate(R.layout.main_menu, container, false);
        rootView.findViewById(R.id.btnNewGameQuest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.StartGame(0);
            }
        });

        rootView.findViewById(R.id.btnNewGameTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.StartGame(1);
            }
        });

        rootView.findViewById(R.id.btnNewGameInfinity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.StartGame(2);
            }
        });
        return rootView;
    }


}
