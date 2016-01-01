package com.arielallon.etymoffline.model;

//import android.os.Parcel;

import org.parceler.Parcel;

import java.io.Serializable;

import io.realm.EtymRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = { EtymRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { Etym.class })
public class Etym extends RealmObject {
//    @todo add a pk, since multiple words may match

    @Index
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