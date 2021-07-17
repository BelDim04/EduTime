package com.example.tests;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class ExcerciseShowLandscapeFragment extends Fragment {
    private String Exid;
    private  String Exnumber;
    private  String Exsubject;
    private ArrayList<String> CountEx;
    private int CurrentPosition;;
    private ArrayList<CurExerciseFragment> ExList;
    private int NStatus;
    private ProfileFragment Pr;

    private static final String KEY_Exid = "Exid";
    private static final String KEY_Num = "Num";
    private static final String KEY_Sub = "Sub";
    private static final String KEY_Cou = "Cou";
    private static final String KEY_CurP = "CurP";
    private static final String KEY_ExL = "ExL";
    private static final String KEY_NS = "NS";

    private ListView listview;

    public ExcerciseShowLandscapeFragment(String a, String b, int ns,String c){
        super();
        Exid=c;
        if (a != null)
            this.Exnumber = a;
        if (b != null)
            this.Exsubject = b;
        this.CountEx=null;
        this.CurrentPosition = 0;
        this.ExList=new ArrayList<>();
        NStatus=ns;
    }
    public ExcerciseShowLandscapeFragment(){
        super();
    }
    public ExcerciseShowLandscapeFragment(String s, String n, int p, ArrayList<CurExerciseFragment> ac, ArrayList<String> h, int ns, ProfileFragment yr) {
        super();
        this.Exsubject=s;
        this.Exnumber=n;
        this.CurrentPosition = p;
        this.ExList=ac;
        this.CountEx=h;
        NStatus=ns;
        Pr=yr;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            this.Exid=savedInstanceState.getString(KEY_Exid);
            this.Exnumber=savedInstanceState.getString(KEY_Num);
            this.Exsubject=savedInstanceState.getString(KEY_Sub);
            this.CountEx=savedInstanceState.getStringArrayList(KEY_Cou);
            this.CurrentPosition=savedInstanceState.getInt(KEY_CurP);
            this.ExList=savedInstanceState.getParcelableArrayList(KEY_ExL);
            NStatus=savedInstanceState.getInt(KEY_NS);
        }
        if(this.CountEx==null){
            CountEx = new ArrayList<String>();
            DBhelper dbhelper=new DBhelper(getContext());
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
            SQLiteDatabase mdb = dbhelper.getReadableDatabase();
            Cursor exsid=mdb.rawQuery("select * from '"+Exid+"' where "+DBhelper.ExStatus+" = "+NStatus,null);
            while(exsid.moveToNext()){
                CountEx.add(exsid.getString(exsid.getColumnIndex(DBhelper.ExTId)));
            }
            exsid.close();
        }
        if(CountEx.size()==0) CountEx.add("0");
        if(ExList.size()==0) {
            Cursor exstr = null;
            if(NStatus==0){
                DBhelper dbhelper=new DBhelper(getContext());
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
                SQLiteDatabase mdb = dbhelper.getReadableDatabase();
                exstr=mdb.rawQuery("select "+DBhelper.ExTries+" from '"+Exid+"' where "+DBhelper.ExStatus+" = "+NStatus,null);
            }
            for(int i=0;i<CountEx.size();i++) {
                CurExerciseFragment ca = new CurExerciseFragment(CountEx.get(i),Exid);
                ExList.add(i, ca);
                if(NStatus==1) ExList.get(i).setCurStatus(1);
                else{
                    exstr.moveToPosition(i);
                    if(exstr.getCount()>0&&exstr.getInt(0)>0) ExList.get(i).setCurStatus(-1);
                }
            }
            if(exstr!=null) exstr.close();
        }
        Pr=(ProfileFragment) getActivity().getSupportFragmentManager().findFragmentByTag("Profile");
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT)
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentholder, new ExerciseShowFragment(Exsubject,Exnumber,CurrentPosition,ExList,CountEx,NStatus,Pr)).commit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.exerciseshowlayoutlandscape,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        class MyIdListAdapter extends ArrayAdapter<String>{

            public MyIdListAdapter(@NonNull Context context) {
                super(context, android.R.layout.simple_list_item_1,CountEx);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View simleitem=convertView;
                if(simleitem==null)
                    simleitem=getLayoutInflater().inflate(android.R.layout.simple_list_item_1,parent,false);
                TextView text1= simleitem.findViewById(android.R.id.text1);
                text1.setText(CountEx.get(position));
                if(ExList.get(position).getCurStatus()==-1) simleitem.setBackground(getResources().getDrawable(R.drawable.scrollwrong));
                if(ExList.get(position).getCurStatus()==1) simleitem.setBackground(getResources().getDrawable(R.drawable.scrollright));
                return simleitem;
            }
        }
        MyIdListAdapter listadapter=new MyIdListAdapter(getContext());
        listview= view.findViewById(R.id.landlistview);
        listview.setAdapter(listadapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CurrentPosition=position;
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.exshowlandfragment,ExList.get(position)).commit();
            }
        });
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.exshowlandfragment,ExList.get(CurrentPosition)).commit();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toolbar toolbar= getActivity().findViewById(R.id.toolbar);
        if(Exnumber!=null&&Exsubject!=null)
        toolbar.setTitle(Exsubject+"   "+Exnumber);
        if(Pr!=null)
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragmentholder,Pr,"Profile").commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listview=null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_Exid,this.Exid);
        outState.putString(KEY_Num,this.Exnumber);
        outState.putString(KEY_Sub,this.Exsubject);
        outState.putStringArrayList(KEY_Cou,this.CountEx);
        outState.putInt(KEY_CurP,this.CurrentPosition);
        outState.putParcelableArrayList(KEY_ExL,this.ExList);
        outState.putInt(KEY_NS,NStatus);
    }
}
