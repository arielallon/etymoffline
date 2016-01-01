package com.arielallon.etymoffline;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.util.Log;

//import com.fasterxml.jackson.core.JsonFactory;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;

import com.arielallon.etymoffline.model.Etym;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import co.moonmonkeylabs.realmsearchview.RealmSearchAdapter;
import co.moonmonkeylabs.realmsearchview.RealmSearchView;
import co.moonmonkeylabs.realmsearchview.RealmSearchViewHolder;
import io.realm.RealmViewHolder;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    private RealmSearchView realmSearchView;
    private EtymRecyclerViewAdapter adapter;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resetRealm();
        loadEtymData();

        RealmSearchView realmSearchView = (RealmSearchView) findViewById(R.id.search_view);

        realm = Realm.getInstance(this);
        EtymRecyclerViewAdapter adapter = new EtymRecyclerViewAdapter(this, realm, "word", false, Case.INSENSITIVE, Sort.ASCENDING, "word", "");
        realmSearchView.setAdapter(adapter);

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

    private void loadEtymData() {
        AssetManager assetManager = getAssets();

        try {
            String[] files = assetManager.list("");
            for (String filename : files) {
                Log.i("LOADINGJSON", filename);
            }
            InputStream is = assetManager.open("etymologies.json");
            Log.i("LOADINGJSON", "successfully opened json file");


            Realm realm = Realm.getInstance(this);
            realm.beginTransaction();
            try {
                realm.createAllFromJson(Etym.class, is);
                realm.commitTransaction();
            } catch (IOException e) {
                realm.cancelTransaction();
            }
        } catch (FileNotFoundException e) {
            Log.e("LOADINGJSON", "Couldn't find JSON file");
        } catch (IOException e) {
            Log.e("LOADINGJSON", "Couldn't find JSON file");
        }
    }

    private void resetRealm() {
        RealmConfiguration realmConfig = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.deleteRealm(realmConfig);
    }

    public class EtymRecyclerViewAdapter
            extends RealmSearchAdapter<Etym, EtymRecyclerViewAdapter.ViewHolder> {

        public EtymRecyclerViewAdapter(
                Context context,
                Realm realm,
                String filterColumnName) {
            super(context, realm, filterColumnName);
        }

        public EtymRecyclerViewAdapter(
                Context context,
                Realm realm,
                String filterColumnName,
                boolean useContains,
                Case casing,
                Sort sortOrder,
                String sortKey,
                String basePredicate
        ) {
            super(context, realm, filterColumnName, useContains, casing, sortOrder, sortKey, basePredicate);
        }

        public class ViewHolder extends RealmSearchViewHolder {

            private final EtymItemView etymItemView;

            public ViewHolder(EtymItemView etymItemView) {
                super(etymItemView);
                this.etymItemView = etymItemView;
            }
        }

        @Override
        public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
            ViewHolder vh = new ViewHolder(new EtymItemView(viewGroup.getContext()));
            return vh;
        }

        @Override
        public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
            final Etym etym = realmResults.get(position);
            viewHolder.etymItemView.bind(etym);
        }

    }
}
