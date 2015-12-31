package com.arielallon.etymoffline.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Etym extends RealmObject
{
    @PrimaryKey
    private String word;
    private String etymology;

    public Etym()
    {
    }

    public String getWord()
    {
        return word;
    }

    public void setWord(String word)
    {
        this.word = word;
    }

    public String getEtymology()
    {
        return etymology;
    }

    public void setEtymology(String etymology)
    {
        this.etymology = etymology;
    }
}