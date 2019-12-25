package com.itmindco.wordsinword;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EndGameFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.endgame_fragment, container, false);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity app = (MainActivity) getActivity();
                if (app != null) {
                    app.showMainMenu();
                }
            }
        });

        Bundle params = getArguments();

        TextView tw = (TextView) rootView.findViewById(R.id.endGame_word);
        tw.setText(params.getString("word"));

        tw = (TextView) rootView.findViewById(R.id.endGame_numSolved);
        tw.setText("Отгадано слов: " + params.getInt("solved"));

        tw = (TextView) rootView.findViewById(R.id.endGame_score);
        tw.setText("Заработано: " + params.getInt("score") + " очков");

        String status = "Отлично";
        int diff = params.getInt("playerWin");
        if (diff < -10) {
            status = "Плохо";
        } else if (diff < -3) {
            status = "Средне";
        } else if (diff < -1) {
            status = "Хорощо";
        }

        tw = (TextView) rootView.findViewById(R.id.endGame_rating);
        tw.setText(status);

        return rootView;
    }
}
