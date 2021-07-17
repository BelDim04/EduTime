package com.example.tests;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.sql.SQLException;

public class CurExerciseFragment extends Fragment implements Parcelable {
    private String ExIdB;
    private String Id;
    private int CurStatus;
    private int CurHelp;
    private static final String KEY_ExIdB = "ExIdB";
    private static final String KEY_Id = "Id";
    private static final String KEY_St = "St";
    private static final String KEY_He = "He";

    private DBhelper dbhelper;
    private TextView exid;
    private EditText ett;
    private ImageButton send;
    private LinearLayout linears;
    private TextView exhel;
    private TextView exhelna;
    private LinearLayout exmainlinear;
    private RelativeLayout exmainrel;
    private ImageButton helpbtn;
    private TextView etext;

    int getCurStatus() {
        return CurStatus;
    }

    void setCurStatus(int curStatus) {
        CurStatus = curStatus;
    }

    CurExerciseFragment(String a,String b){
        super();
        this.Id=a;
        ExIdB=b;
    }
    public CurExerciseFragment(String a, int b){
        super();
        this.Id=a;
        this.CurStatus=b;
    }
    public CurExerciseFragment(){
        super();
    }


    private CurExerciseFragment(Parcel in) {
        CurStatus = in.readInt();
        Id=in.readString();
        CurHelp=in.readInt();
    }

    public static final Creator<CurExerciseFragment> CREATOR = new Creator<CurExerciseFragment>() {
        @Override
        public CurExerciseFragment createFromParcel(Parcel in) {
            return new CurExerciseFragment(in);
        }

        @Override
        public CurExerciseFragment[] newArray(int size) {
            return new CurExerciseFragment[size];
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            Id=savedInstanceState.getString(KEY_Id);
            CurStatus=savedInstanceState.getInt(KEY_St);
            CurHelp=savedInstanceState.getInt(KEY_He);
            ExIdB=savedInstanceState.getString(KEY_ExIdB);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.curexercise,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        exid= view.findViewById(R.id.exid);
        dbhelper = new DBhelper(getContext());
        try {
            dbhelper.createDataBase();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        try {
            dbhelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        final SQLiteDatabase mdb = dbhelper.getWritableDatabase();
        ett= view.findViewById(R.id.ett);
        send= view.findViewById(R.id.send);
        linears= view.findViewById(R.id.linears);
        exhel= view.findViewById(R.id.exhelp);
        exhelna= view.findViewById(R.id.exhelpna);
        exmainlinear = view.findViewById(R.id.exmainlinear);
        exmainrel= view.findViewById(R.id.exmainrel);
        helpbtn= view.findViewById(R.id.helpbtn);



        exid.setText(this.Id);
        final Cursor ExercisenameText = mdb.rawQuery("select " + DBhelper.ExText + " from '" + ExIdB + "' where " + DBhelper.ExTId + " = '" + exid.getText() + "' ;", null);
        ExercisenameText.moveToFirst();
        etext =  view.findViewById(R.id.extext);
        if (ExercisenameText.getCount() > 0)
            etext.setText(ExercisenameText.getString(ExercisenameText.getColumnIndex(DBhelper.ExText)));
        else
            etext.setText("Вы сделали все задания");
        ExercisenameText.close();


        exmainlinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { InputMethodManager imm;
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getActivity().getCurrentFocus() != null)
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0); }
        });
        exmainrel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { InputMethodManager imm;
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getActivity().getCurrentFocus() != null)
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0); }
        });



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!exid.getText().equals("0")) {
                    SharedPreferences profilesp = getActivity().getSharedPreferences(MainActivity.Profislespname, Context.MODE_PRIVATE);
                    Cursor ranserc = mdb.rawQuery("select " + DBhelper.ExAnswer + " from '" + ExIdB + "' where " + DBhelper.ExTId + " = '" + exid.getText() + "' ;", null);
                    ranserc.moveToFirst();
                    String ranser = ranserc.getString(ranserc.getColumnIndex(DBhelper.ExAnswer));
                    ranserc.close();
                    if (ett.getText().toString().equalsIgnoreCase(ranser)) {
                        SharedPreferences.Editor ed=profilesp.edit();
                        ed.putInt(ExIdB+"T",profilesp.getInt(ExIdB+"T",0)+1);
                        ed.putInt(ExIdB+"R",profilesp.getInt(ExIdB+"R",0)+1);
                        ed.commit();
                        mdb.execSQL("update '" + ExIdB + "' set " + DBhelper.ExStatus + " =1 where " + DBhelper.ExTId + " ='" + exid.getText() + "' ;");
                        Cursor ctries = mdb.rawQuery("select " + DBhelper.ExTries + " from '" + ExIdB + "' where " + DBhelper.ExTId + " = '" + exid.getText() + "' ;", null);
                        ctries.moveToFirst();
                        mdb.execSQL("update '" + ExIdB + "' set " + DBhelper.ExTries + " = " + (ctries.getInt(ctries.getColumnIndex(DBhelper.ExTries)) + 1) + " where " + DBhelper.ExTId + " ='" + exid.getText() + "' ;");
                        ctries.close();
                        exhelna.setVisibility(View.VISIBLE);
                        Cursor chel = mdb.rawQuery("select " + DBhelper.ExHelp + " from '" + ExIdB + "' where " + DBhelper.ExTId + " = '" + exid.getText() + "' ;", null);
                        chel.moveToFirst();
                        exhel.setText(chel.getString(chel.getColumnIndex(DBhelper.ExHelp)));
                        exhel.setVisibility(View.VISIBLE);
                        chel.close();
                        send.setVisibility(View.INVISIBLE);
                        exmainrel.setBackground(getResources().getDrawable(R.drawable.scrollright));
                        linears.setBackground(getResources().getDrawable(R.drawable.sendright));
                        ett.setEnabled(false);
                        helpbtn.setVisibility(View.INVISIBLE);
                        CurStatus = 1;
                        ListView listidview= getActivity().findViewById(R.id.landlistview);
                        if(listidview!=null) {
                            ArrayAdapter<String> a = (ArrayAdapter<String>) listidview.getAdapter();
                            a.notifyDataSetChanged();
                        }
                    } else {
                        SharedPreferences.Editor ed=profilesp.edit();
                        ed.putInt(ExIdB+"T",profilesp.getInt(ExIdB+"T",0)+1);
                        ed.commit();
                        Cursor ctries = mdb.rawQuery("select " + DBhelper.ExTries + " from '" + ExIdB + "' where " + DBhelper.ExTId + " = '" + exid.getText() + "' ;", null);
                        ctries.moveToFirst();
                        mdb.execSQL("update '" + ExIdB + "' set " + DBhelper.ExTries + " = " + (ctries.getInt(ctries.getColumnIndex(DBhelper.ExTries)) + 1) + " where " + DBhelper.ExTId + " ='" + exid.getText() + "' ;");
                        ctries.close();
                        linears.setBackground(getResources().getDrawable(R.drawable.sendwrong));
                        CurStatus = -1;
                        ListView listidview= getActivity().findViewById(R.id.landlistview);
                        if(listidview!=null){
                            ArrayAdapter<String> a=(ArrayAdapter<String>) listidview.getAdapter();
                            a.notifyDataSetChanged();
                        }
                    }
                   /* NavigationView navigationView=getActivity().findViewById(R.id.navigationview);
                    View header=navigationView.getHeaderView(0);
                    TextView obstat=header.findViewById(R.id.obstathead);
                    int right=0;
                    int rtries=0;
                    Cursor stat=mdb.rawQuery("select * from '"+DBhelper.NamesSubjects+"' ;",null);
                    Cursor stat1;
                    for(int i=1;i<=stat.getCount();i++){
                        stat1=mdb.rawQuery("select * from '"+ i +"' ;",null);
                        for(int j=1;j<=stat1.getCount();j++){
                            rtries+=profilesp.getInt(String.valueOf(i)+"_"+String.valueOf(j)+"T",0);
                            right+=profilesp.getInt(String.valueOf(i)+"_"+String.valueOf(j)+"R",0);
                        }
                    }
                    if(rtries!=0)
                        obstat.setText(String.valueOf(100*right/rtries));*/
                }
            }
        });
        if(CurStatus==1){
            exhelna.setVisibility(View.VISIBLE);
            Cursor chel = mdb.rawQuery("select " + DBhelper.ExHelp+" , "+ DBhelper.ExAnswer + " from '" + ExIdB + "' where " + DBhelper.ExTId + " = '" + exid.getText() + "' ;", null);
            if(chel.getCount()!=0){
                chel.moveToFirst();
                exhel.setText(chel.getString(chel.getColumnIndex(DBhelper.ExHelp)));
                exhel.setVisibility(View.VISIBLE);
                ett.setText(chel.getString(chel.getColumnIndex(DBhelper.ExAnswer)));}
            else {
                exhelna.setVisibility(View.INVISIBLE);
                etext.setText("Вы не сделали ни одного задания");
            }
            chel.close();
            send.setVisibility(View.INVISIBLE);
            exmainrel.setBackground(getResources().getDrawable(R.drawable.scrollright));
            linears.setBackground(getResources().getDrawable(R.drawable.sendright));
            ett.setEnabled(false);
        }
        if(CurStatus==-1){
            linears.setBackground(getResources().getDrawable(R.drawable.sendwrong));
        }

        helpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Id.equals("0")){
                    if(exhelna.getVisibility()==View.INVISIBLE) {
                        exhelna.setVisibility(View.VISIBLE);
                        if(exhel.getText().equals("")) {
                            Cursor chel = mdb.rawQuery("select " + DBhelper.ExHelp + " from '" + ExIdB + "' where " + DBhelper.ExTId + " = '" + exid.getText() + "' ;", null);
                            chel.moveToFirst();
                            exhel.setText(chel.getString(chel.getColumnIndex(DBhelper.ExHelp)));
                            chel.close();
                        }
                        exhel.setVisibility(View.VISIBLE);
                        CurHelp=1;
                    }
                    else{
                        exhel.setVisibility(View.INVISIBLE);
                        exhelna.setVisibility(View.INVISIBLE);
                        CurHelp=0;
                    }
                }
            }
        });
        if(this.CurHelp==1){
            exhelna.setVisibility(View.VISIBLE);
            Cursor chel = mdb.rawQuery("select " + DBhelper.ExHelp + " from '" + ExIdB + "' where " + DBhelper.ExTId + " = '" + exid.getText() + "' ;", null);
            chel.moveToFirst();
            exhel.setText(chel.getString(chel.getColumnIndex(DBhelper.ExHelp)));
            chel.close();
            exhel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        exid = null;
        ett = null;
        send = null;
        linears = null;
        exhel = null;
        exhelna = null;
        exmainlinear = null;
        exmainrel = null;
        helpbtn = null;
        etext=null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_Id,Id);
        outState.putInt(KEY_St,CurStatus);
        outState.putInt(KEY_He,CurHelp);
        outState.putString(KEY_ExIdB,ExIdB);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbhelper.close();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(CurStatus);
        dest.writeString(Id);
        dest.writeInt(CurHelp);
    }
}
