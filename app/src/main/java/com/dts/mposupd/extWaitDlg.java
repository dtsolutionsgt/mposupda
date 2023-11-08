package com.dts.mposupd;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class extWaitDlg {

    public  Dialog wdhandle;

    private TextView mTitleLabel,mBtnLeft,mBtnMid,mBtnRight;
    private RelativeLayout mRel,mRelTop,mRelBot;
    private LinearLayout mbuttons;

    private Dialog dialog;
    private Context cont;

    private int buttonCount;
    private int bwidth=520,bheight=400,mwidth=0,mheight=0,mlines=6,mminlines=1;

    //region Public methods

    private void buildDialogbase(Activity activity,String titletext,String butleft,String butmid,String butright) {

        dialog = new Dialog(activity);wdhandle=dialog;
        cont=dialog.getContext();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.extwaitdlg);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        mRel = dialog.findViewById(R.id.extlistdialogrel);
        mRelTop = dialog.findViewById(R.id.xdlgreltop);
        mRelBot = dialog.findViewById(R.id.xdlgrelbut);
        mbuttons = dialog.findViewById(R.id.linbuttons);

        mTitleLabel = dialog.findViewById(R.id.lbltitulo);
        mTitleLabel.setText(titletext);

        mBtnLeft = dialog.findViewById(R.id.btnexit);
        mBtnLeft.setText(butleft);
        mBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        mBtnMid = dialog.findViewById(R.id.btndel);
        mBtnMid.setText(butmid);
        mBtnMid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        mBtnRight = dialog.findViewById(R.id.btnadd);
        mBtnRight.setText(butright);
        mBtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        switch (buttonCount) {
            case 1:
                mBtnMid.setVisibility(View.GONE);mBtnRight.setVisibility(View.GONE);
                mbuttons.setWeightSum(1);break;
            case 2:
                mBtnRight.setVisibility(View.GONE);
                mbuttons.setWeightSum(2);break;
            case 3:
                mbuttons.setWeightSum(3);break;
        }

        mwidth=0;mheight=0;
        mlines =0;

    }

    public void buildDialog(Activity activity,String titletext) {
        buttonCount=1;
        buildDialogbase(activity,titletext,"Salir","","");
    }

    public void buildDialog(Activity activity,String titletext,String butleft) {
        buttonCount=1;
        buildDialogbase(activity,titletext,butleft,"","");
    }

    public void buildDialog(Activity activity,String titletext,String butleft,String butmid) {
        buttonCount=2;
        buildDialogbase(activity,titletext,butleft,butmid,"");
    }

    public void buildDialog(Activity activity,String titletext,String butleft,String butmid,String butright) {
        buttonCount=3;
        buildDialogbase(activity,titletext,butleft,butmid,butright);
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void setOnLeftClick(@Nullable View.OnClickListener l) {
        mBtnLeft.setOnClickListener(l);
    }

    public void setOnMiddleClick(@Nullable View.OnClickListener l) {
        mBtnMid.setOnClickListener(l);
    }

    public void setOnRightClick(@Nullable View.OnClickListener l) {
        mBtnRight.setOnClickListener(l);
    }

    public void setWidth(int pWidth) {
        mwidth=pWidth;
        if (mwidth<100) mwidth=0;

    }

    public void setHeight(int pHeight) {
        mheight=pHeight;
        if (mheight<100) mheight=0;
    }

    public void setLines(int pLines) {
        mlines=pLines;
        if (mlines<1) mlines=0;
        if (mlines>0) mheight=0;
    }

    public void setMinLines(int pLines) {
        mminlines=pLines;
        if (mminlines<1) mminlines=1;
    }

    public void show() {
        int fwidth,fheight,tw,rlcount;
        int itemHeight,headerHeight,footerHeight;

        if (mwidth==0) mwidth=bwidth;
        fwidth=mwidth;
        fheight=bheight;

        DisplayMetrics displayMetrics = cont.getResources().getDisplayMetrics();
        tw = displayMetrics.widthPixels;tw=(int) (0.8*tw);
        if (fwidth>tw) fwidth=tw;

        mRel.getLayoutParams().width = fwidth;
        mRel.getLayoutParams().height =fheight;

        dialog.getWindow().setLayout(fwidth, fheight);

        dialog.show();
    }

    //endregion


}
