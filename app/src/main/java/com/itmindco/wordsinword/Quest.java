package com.itmindco.wordsinword;


import java.util.Random;

public class Quest {

    //0 - отдгадать колво слов
    //1 - набрать колво очков
    //2 - отгдадать 3 самых длинных слова
    //3 - отгдать 5 слов на букву К
    int type = 0;
    DictionaryDb.Word word;

    int questParam = 0;

    public Quest(DictionaryDb.Word word) {
        this.word = word;
    }

    //возвращает текст квеста
    public String start() {


        int min = 0;
        int max = 1;

        Random r = new Random();
        type = r.nextInt(max - min + 1) + min;
        String questText = "";

        switch (type) {
            case 0:
                int numSolve = word.numSolveWord();
                min = 4;
                max = numSolve / 2;
                int num = max;
                if (max > min) {
                    num = r.nextInt(max - min + 1) + min;
                }
                questText = "Отгадайте " + num + " слов";
                questParam = num;
                break;
            case 1:

                int numScore = word.allAviableScore();
                min = 4;
                max = numScore / 2;
                int numS = max;
                if (max > min) {
                    numS = r.nextInt(max - min + 1) + min;
                }
                questText = "Наберите " + numS + " очков";
                questParam = numS;
                break;
            default:
                break;
        }

        return questText;
    }

    //0 - еще не выполнен
    //1 - выполнен
    //2 - проиграл
    public int check(int playerScore) {
        int result = 0;
        int last = 0;

        if (type == 0) {
            questParam--;
            last = word.numSolveWord();
        } else if (type == 1) {
            questParam -= playerScore;
            last = word.allAviableScore();
        }

        if (questParam <= 0) {
            result = 1;
        } else if (last < questParam) {
            //проверим достижимость
            result = -1;

        }
        return result;
    }

    public boolean aviable() {
        boolean result = true;
        int last = 0;

        if (type == 0) {
            last = word.numSolveWord();
        } else if (type == 1) {
            last = word.allAviableScore();
        }

        if (last < questParam) {
            //проверим достижимость
            result = false;
        }

        return result;
    }

    public String getQuestParamText() {
        String text = "осталось слов: " + questParam;
        if (type == 1) {
            text = "осталось очков: " + questParam;
        }

        return text;
    }
}
