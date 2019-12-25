package com.itmindco.wordsinword;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.Random;

public class DictionaryDb extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "dictionary";
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase db;

    public DictionaryDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
        db = getReadableDatabase();
    }

    public Word getRandomWord() {
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT d1.word,d2.word AS solveword FROM solve \n" +
                    "INNER JOIN DictionaryItem AS d1 ON d1.id = solve.wordID \n" +
                    "INNER JOIN DictionaryItem AS d2 ON d2.id = solve.solveID\n" +
                    "WHERE solve.wordID IN (\n" +
                    "SELECT wordID FROM [wordCount] ORDER BY random() LIMIT 1)", new String[]{});
            cursor.moveToFirst();
            Word temp = new Word(cursor.getString(0));
            temp.addSolveWord(cursor.getString(1));
            while (cursor.moveToNext()){
                temp.addSolveWord(cursor.getString(1));
            }
            temp.Srink();

            return temp;
        } else {
            return new Word("ошибка БД");
        }
    }

    public Word getLargestWord() {
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT d1.word,d2.word AS solveword FROM solve \n" +
                    "INNER JOIN DictionaryItem AS d1 ON d1.id = solve.wordID \n" +
                    "INNER JOIN DictionaryItem AS d2 ON d2.id = solve.solveID\n" +
                    "WHERE solve.wordID IN (\n" +
                    "SELECT wordID FROM [wordCount] ORDER BY countSolve DESC LIMIT 1)", new String[]{});
            cursor.moveToFirst();
            Word temp = new Word(cursor.getString(0));
            temp.addSolveWord(cursor.getString(1));
            while (cursor.moveToNext()){
                temp.addSolveWord(cursor.getString(1));
            }

            return temp;
        } else {
            return new Word("ошибка БД");
        }
    }

    public class Word{
        public String word;
        ArrayList<String> solve;
        Random random;

        public Word(String mainWord){
            word = mainWord;
            solve = new ArrayList<>(10);
            random = new Random();
        }

        public void addSolveWord(String solveWord){
            if(!solve.contains(solveWord)) {
                solve.add(solveWord);
                Log.d("Words",solveWord);
            }
        }

        public void removeSolveWord(String solveWord){
            if(solve.contains(solveWord)) {
                solve.remove(solveWord);
            }
        }

        //делаем количество нечетным, иначе противник всегда убдет выигрывать
        public void Srink(){
            if(solve.size()%2==0){
                solve.remove(solve.size()-1);
            }
        }

        public String getRandomSolve(){
            if (solve.size()==0)
                return "";

            int min = 0;
            int max = solve.size()-1;
            int index = random.nextInt(max - min + 1) + min;
            return solve.get(index);
        }

        //количество слов
        public int numSolveWord(){
            return solve.size();
        }

        public int allAviableScore() {
            int num = 0;
            for (String s : solve) {
                num += s.length();
            }
            return num;
        }
        //количество слов начинающихся на заданное слово
        public int numSolve(String solveWord){
            int num = 0;
            for(String s:solve){
                if(s.startsWith(solveWord)){
                    num++;
                }
            }
            return num;
        }

        //слово уже отгдадано
        public boolean IsSolve(String solveWord){
            return solve.contains(solveWord);

        }

    }

}
