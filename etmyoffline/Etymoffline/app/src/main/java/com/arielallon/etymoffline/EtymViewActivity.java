package com.arielallon.etymoffline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arielallon.etymoffline.model.Etym;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;

public class EtymViewActivity extends AppCompatActivity {

    private Realm realm;

    @Bind(R.id.word)
    TextView word;

    @Bind(R.id.etymology)
    TextView etymology;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etym_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Etym etym = (Etym) Parcels.unwrap(intent.getParcelableExtra(MainActivity.EXTRA_ETYM));

        ButterKnife.bind(this);
        bind(etym);

        getSupportActionBar().setSubtitle(etym.getWord());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    public void bind(Etym etym) {
        word.setText(etym.getWord());
        etymology.setText(etym.getEtymology());
    }

}
