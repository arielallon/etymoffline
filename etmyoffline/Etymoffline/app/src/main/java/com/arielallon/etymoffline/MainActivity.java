package com.arielallon.etymoffline;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Parcelable;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;

import com.arielallon.etymoffline.model.Etym;

import org.parceler.Parcels;

import io.realm.Case;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import co.moonmonkeylabs.realmsearchview.RealmSearchAdapter;
import co.moonmonkeylabs.realmsearchview.RealmSearchView;
import co.moonmonkeylabs.realmsearchview.RealmSearchViewHolder;
import io.realm.RealmMigration;
import io.realm.RealmViewHolder;
import io.realm.Sort;
import io.realm.exceptions.RealmMigrationNeededException;

public class MainActivity extends AppCompatActivity {

    private RealmSearchView realmSearchView;
    private EtymRecyclerViewAdapter adapter;
    private Realm realm;

    public final static String EXTRA_WORD = "com.arielallon.etymoffline.WORD";
    public final static String EXTRA_ETYM = "com.arielallon.etymoffline.ETYM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        resetRealm();
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

    private void loadEtymData()
    {
        String realmFileName = "default.realm";

        try {
            RealmConfiguration realmConfig = new RealmConfiguration
                    .Builder(this)
                    .build();
            String realmPath = realmConfig.getPath();
            File realmFile = new File(realmPath + realmFileName);
            if (!realmFile.exists() || realmFile.length() < 1024) {
                loadEtymDataFromRealm(realmFileName, realmPath);
            }

            Realm.getInstance(realmConfig);
        } catch (RealmMigrationNeededException exception) {
            loadEtymDataFromJson();
        }
    }

    private void loadEtymDataFromRealm(String realmFileName, String realmPath)
    {
        copyAsset(realmFileName, realmPath);
    }

    private void loadEtymDataFromJson() {
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

    public void openWord(View view)
    {
        Intent intent = new Intent(this, EtymViewActivity.class);
//        View word = findViewById(R.id.word_item);
        EtymItemView item = (EtymItemView) view.getParent();
        Etym etym = item.getEtym();
        Parcelable petym = Parcels.wrap(etym);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_ETYM, petym);
//        String w = word.toString();
//        intent.putExtra(EXTRA_WORD, w);
//        intent.putExtra(EXTRA_ETYM, petym);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void copyAsset(String filename, String destinationPath) {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(destinationPath);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
