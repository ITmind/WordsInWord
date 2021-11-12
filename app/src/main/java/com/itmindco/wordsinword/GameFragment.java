package com.itmindco.wordsinword;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;
import com.software.shell.fab.FloatingActionButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class GameFragment extends Fragment implements WordView.WordViewListener {

    DictionaryDb.Word word;
    ArrayList<String> playerSolve;
    ArrayList<String> compSolve;
    ArrayAdapter<String> playerAdapter;
    ArrayAdapter<String> compAdapter;
    int playerScore = 0;
    int compScore = 0;
    FloatingActionButton actionButton;
    String tempWord = "";
    CountDownTimer timer;
    long cuurentMillisUntilFinished = 30000;
    ProgressDialog mDialog;
    AsyncTask httpRequestTask;
    ListView lvMain;
    ListView lvComp;
    int mode = 0;
    Quest quest;
    int coins = 0;
    //private InterstitialAd interstitial;

    public GameFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerSolve = new ArrayList<>(10);
        compSolve = new ArrayList<>(10);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game, container, false);

        InitWord();

        WordView ww = (WordView) rootView.findViewById(R.id.wordview);
        ww.setWord(word.word);

        lvMain = (ListView) rootView.findViewById(R.id.playerlistView);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String w = ((TextView)view).getText().toString();
                getWordDescription(w);
            }
        });

        playerAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_element, playerSolve);
        lvMain.setAdapter(playerAdapter);

        lvComp = (ListView) rootView.findViewById(R.id.complistView);
        compAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_element, compSolve);
        lvComp.setAdapter(compAdapter);
        lvComp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String w = ((TextView)view).getText().toString();
                getWordDescription(w);
            }
        });

        actionButton =  (FloatingActionButton)rootView.findViewById(R.id.action_button);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionButtonClick();
            }
        });

        setBtnClicker(rootView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID))
                .build();

        //interstitial = new InterstitialAd(getActivity());
        //interstitial.setAdUnitId("ca-app-pub-4099519512268108/5614974492");

        // Запуск загрузки межстраничного объявления.
        //interstitial.loadAd(adRequest);

        mode = getArguments().getInt("mode");
        initMode(rootView);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sharedPref.contains("coins")) {
            coins = sharedPref.getInt("coins", 0);
        }


        TextView tw = (TextView) rootView.findViewById(R.id.playerScore);
        tw.setText("Вы (" + playerScore + ")");

        tw = (TextView) rootView.findViewById(R.id.compScore);
        tw.setText("АИ (" + compScore + ")");

        tw = (TextView) rootView.findViewById(R.id.numWord);

        if (quest != null) {
            tw.setText(quest.getQuestParamText());
        } else {
            tw.setText("осталось слов:" + word.numSolveWord());
        }

        tw = (TextView) rootView.findViewById(R.id.numCoins);
        tw.setText("" + coins);

        return rootView;
    }

    private void initMode(View view){
        if(mode==0){
            view.findViewById(R.id.timerIcon).setVisibility(View.GONE);
            view.findViewById(R.id.timerText).setVisibility(View.GONE);
            startQuest();
        }
        else if(mode==2){
            //infinity
            view.findViewById(R.id.timerIcon).setVisibility(View.GONE);
            view.findViewById(R.id.timerText).setVisibility(View.GONE);
        }

    }

    private void startQuest(){
        quest = new Quest(word);
        String questText = quest.start();

        //word.numSolveWord()
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(questText)
                .setCancelable(false)
                .setPositiveButton("закрыть", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();

    }

    private void InitWord(){
        MainActivity app = (MainActivity) getActivity();
        word = app.GetDB().getRandomWord();//.getLargestWord();
    }

    //region Buttons
    private void setBtnClicker(View mainView){
        mainView.findViewById(R.id.btnNextTurn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enemyTurn();

            }
        });

        mainView.findViewById(R.id.btnClearAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetWordWiew();
            }
        });

        mainView.findViewById(R.id.btnDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLastChWordWiew();
            }
        });

        mainView.findViewById(R.id.btnHelp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpBtnClick();
            }
        });

    }

    private void actionButtonClick() {
        addWord();
    }

    private void helpBtnClick() {
        if (coins < 5) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("У вас недостаточно монет. Требуется 5")
                    .setCancelable(true)
                    .setPositiveButton("Закрыть", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Подсказка стоит 5 монет. Подсказать?")
                    .setCancelable(false)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            coins -= 6;
                            tempWord = word.getRandomSolve();
                            addWord();

                            dialog.cancel();
                        }
                    }).setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }

            );
            builder.create().show();
        }
    }

    private void resetWordWiew() {
        View view = getView();
        if (view != null) {
            WordView ww = (WordView) view.findViewById(R.id.wordview);
            ww.reset();
            if (actionButton.isShown()) {
                actionButton.hide();
            }
        }

    }

    private void deleteLastChWordWiew() {
        View view = getView();
        if (view != null) {
            WordView ww = (WordView) view.findViewById(R.id.wordview);
            ww.deleteLastChar();
            if (actionButton.isShown()) {
                actionButton.hide();
            }
        }
    }

    //endregion

    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        if (view != null) {
            WordView ww = (WordView) view.findViewById(R.id.wordview);
            ww.addWordViewListener(this);
        }
        startTimer();

        actionButton.hide();
    }

    @Override
    public void onPause() {
        stopTimer();
        super.onPause();
        View view = getView();
        if (view != null) {
            WordView ww = (WordView) view.findViewById(R.id.wordview);
            ww.removeWordViewListener(this);
        }
    }

    @Override
    public void onTextChange(String text) {

        tempWord = text;
        if(word.numSolve(text)>1 && word.IsSolve(text)) {
            if(!actionButton.isShown()) {
                actionButton.show();
            }
        }
        else if(word.IsSolve(text)) {
            addWord();
        }
        else {
            if(actionButton.isShown()) {
                actionButton.hide();
            }
        }
    }


    public void displayInterstitial() {
//        if (interstitial.isLoaded()) {
//            interstitial.show();
//        }
    }

    private void addWord() {
        if(actionButton.isShown()){
            actionButton.hide();
        }
        playerSolve.add(tempWord);
        playerScore += tempWord.length();
        coins++;

        word.removeSolveWord(tempWord);

        resetWordWiew();
        playerAdapter.notifyDataSetChanged();
        //scrollMyListViewToBottom(lvMain,playerAdapter);
        //playerAdapter.

        //проверим на квест
        if (quest != null) {
            int questResult = quest.check(tempWord.length());
            if (questResult != 0) {
                endGame();
            }
        }

        updateView();
        enemyTurn();
    }

    private void enemyTurn(){
        String temp = word.getRandomSolve();
        if(temp.isEmpty()){
            endGame();
            return;
        }

        compSolve.add(temp);
        compScore+= temp.length();
        word.removeSolveWord(temp);
        compAdapter.notifyDataSetChanged();
        //scrollMyListViewToBottom(lvComp,compAdapter);
        updateView();
        cuurentMillisUntilFinished = 30000;
        startTimer();

        //проверим на квест
        if (quest != null) {
            if (!quest.aviable()) {
                endGame();
            }
        }
    }

    private void endGame() {
        displayInterstitial();

        coins += playerScore / 30;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("coins", coins);
        editor.apply();

        MainActivity app = (MainActivity)getActivity();
        if(app!=null){
            Bundle endGameParams = new Bundle();

            endGameParams.putInt("playerWin", playerSolve.size() - compSolve.size());
            endGameParams.putString("word",word.word);
            endGameParams.putInt("score",playerScore);
            endGameParams.putInt("solved",playerSolve.size());

            app.showEndGameFragment(endGameParams);
        }
    }

    private void updateView(){
        View view = getView();
        if (view != null) {
            TextView tw = (TextView) view.findViewById(R.id.playerScore);
            tw.setText("Вы (" + playerScore + ")");

            tw = (TextView) view.findViewById(R.id.compScore);
            tw.setText("АИ (" + compScore + ")");

            tw = (TextView) view.findViewById(R.id.numWord);

            if (quest != null) {
                tw.setText(quest.getQuestParamText());
            } else {
                tw.setText("осталось слов:" + word.numSolveWord());
            }

            tw = (TextView) view.findViewById(R.id.numCoins);
            tw.setText("" + coins);
        }
    }

    //region Timer
    private void startTimer(){
        if(mode!=1) return;
        if(timer!=null) timer.cancel();
        timer = new CountDownTimer(cuurentMillisUntilFinished, 1000) {

            public void onTick(long millisUntilFinished) {
                timerTick(millisUntilFinished);
            }

            public void onFinish() {
                timerFinsh();
            }
        };
        timer.start();
    }

    private void stopTimer(){
        if(mode!=1) return;
        if(timer!=null) timer.cancel();
    }

    private void timerTick(long millisUntilFinished){

        cuurentMillisUntilFinished = millisUntilFinished;
        View view = getView();
        if (view != null) {
            TextView tw = (TextView) view.findViewById(R.id.timerText);
            int ti = (int) (millisUntilFinished / 1000);
            String t = ti < 10 ? "0" + ti : "" + ti;
            tw.setText(t);
            if (ti < 10) {
                tw.setTextColor(getResources().getColor(R.color.accent));
            } else {
                tw.setTextColor(getResources().getColor(R.color.text_primary));
            }
        }
    }

    private void timerFinsh(){
        View view = getView();
        if (view != null) {
            TextView tw = (TextView) view.findViewById(R.id.timerText);
            tw.setText("00");
        }
        enemyTurn();
        cuurentMillisUntilFinished = 30000;
        startTimer();
    }

    //endregion

    //region WebRequest

    private void getWordDescription(String sourceWord){
        byte[] bytes = new byte[0];
        try {
            bytes = sourceWord.getBytes("windows-1251");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder(bytes.length * 3);

        for (byte b: bytes)
        {
            sb.append("%");
            sb.append(String.format("%02x", b & 0xff));
        }

        stopTimer();
        mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Получение значения слова...");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(httpRequestTask!=null) {
                    httpRequestTask.cancel(true);
                }
                startTimer();
            }
        });
        mDialog.show();

        Log.d("TAG","http://pda.gramota.ru/?action=dic&word="+sb.toString());
        //new WebRequest().execute("http://pda.gramota.ru/?action=dic&word="+sourceWord);
        httpRequestTask = new WebRequest().execute("http://pda.gramota.ru/?action=dic&word="+sb.toString());
    }

    private void showWordDescription(String description){
        mDialog.hide();
        Log.d("TAG",description);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(description)
                .setCancelable(false)
                .setPositiveButton("закрыть", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        startTimer();
                    }
                });
        builder.create().show();

    }

    private String GetDescriptionFromHTML(Document doc){
        StringBuilder sb = new StringBuilder("");
        Elements nodes = doc.select("div[style]");//.getElementsById("div");//.children();
        if(nodes.isEmpty()) return  sb.toString();

        for (Element node:nodes)
        {
            String nodeText = node.text();
            sb.append(nodeText);
            sb.append("\n");
        }

        return sb.toString();
    }

    public class WebRequest extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... uri) {

            String responseString;
            try {
                Document doc  = Jsoup.connect(uri[0]).get();
                responseString = GetDescriptionFromHTML(doc);

            } catch (IOException e) {
                responseString = e.toString();
                //TODO Handle problems..
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            showWordDescription(result);
        }
    }

    //endregion
}
