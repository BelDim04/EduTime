package com.example.tests;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


public class SubjectFragment extends Fragment{
    private  String Name;
    private  String SubId;

    private static final String KEY_Name = "Name";
    private static final String KEY_SubId = "SubId";

    private ListView exchooselistview;
    SQLiteDatabase mdb;

    public SubjectFragment(String a,int b){
        super();
        Name=a;
        SubId=String.valueOf(b);
    }
    public SubjectFragment(){
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null) {
            this.Name=savedInstanceState.getString(KEY_Name);
            this.SubId=savedInstanceState.getString(KEY_SubId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.exchooselayout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        exchooselistview=view.findViewById(R.id.exchooselistview);
        final ArrayList<String> ArrayListEx=new ArrayList<>();
        DBhelper dbhelper = new DBhelper(getContext());
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
        Cursor exs=mdb.rawQuery("select "+DBhelper.ExName+" from '"+SubId+"' ;",null);
        while(exs.moveToNext()){
            ArrayListEx.add(exs.getString(exs.getColumnIndex(DBhelper.ExName)));
        }
        exs.close();
        class myadapter extends ArrayAdapter {

            public myadapter(@NonNull Context context) {
                super(context,R.layout.exchooselayoutbutton,ArrayListEx);
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view=convertView;
                if (view==null) view=getLayoutInflater().inflate(R.layout.exchooselayoutbutton,parent,false);
                final Button buttonex=view.findViewById(R.id.button1);
                buttonex.setText(ArrayListEx.get(position));
                buttonex.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button pressedbutton = (Button) v;
                        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT) {
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentholder, new ExerciseShowFragment(pressedbutton.getText().toString(), Name,0,SubId+"_"+String.valueOf(position+1))).commit();
                        }
                        else{
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentholder, new ExcerciseShowLandscapeFragment(pressedbutton.getText().toString(), Name,0,SubId+"_"+String.valueOf(position+1))).commit();
                        }
                    }
                });
                final Button onlyexstat= view.findViewById(R.id.onlyexstat);
                SharedPreferences profilesp = getActivity().getSharedPreferences(MainActivity.Profislespname, Context.MODE_PRIVATE);
                int right=profilesp.getInt(SubId+"_"+(position+1)+"R",0);
                int rob=profilesp.getInt(SubId+"_"+(position+1)+"T",0);
                if(rob!=0) onlyexstat.setText(String.valueOf(100*right/rob));
                else onlyexstat.setText(String.valueOf(0));
                onlyexstat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View pressedbuttonsector = (View) v.getParent();
                        Button pressedbutton=pressedbuttonsector.findViewById(R.id.button1);
                        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT) {
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentholder, new ExerciseShowFragment(pressedbutton.getText().toString(), Name,1,SubId+"_"+String.valueOf(position+1))).commit();
                        }
                        else{
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentholder, new ExcerciseShowLandscapeFragment(pressedbutton.getText().toString(), Name,1,SubId+"_"+String.valueOf(position+1))).commit();
                        }
                    }
                });
                return view;
            }
        }
        myadapter arrayadapter=new myadapter(getContext());
        exchooselistview.setAdapter(arrayadapter);
        dbhelper.close();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toolbar toolbar= getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(Name);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        exchooselistview=null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_Name,Name);
        outState.putString(KEY_SubId,SubId);
    }

}
