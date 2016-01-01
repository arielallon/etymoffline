package com.arielallon.etymoffline;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.arielallon.etymoffline.model.Etym;

public class EtymItemView extends RelativeLayout
{
    @Bind(R.id.word_item)
    TextView word;

    @Bind(R.id.etymology_item)
    TextView etymology;

    Etym etym = null;

    public EtymItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.etym_item_view, this);
        ButterKnife.bind(this);
    }

    public void bind(Etym etym) {
        word.setText(etym.getWord());
        etymology.setText(etym.getEtymology());
        this.etym = etym;
    }

    public Etym getEtym()
    {
        return etym;
    }
}