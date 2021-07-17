package com.example.tests;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.sql.SQLException;

import static com.example.tests.MainActivity.Profislespname;

public class ProfileFragment extends Fragment {
    private  String toolTitle;
    private static final String KEY_To = "To";
    private Toolbar toolbar;

    private EditText firstname;
    private EditText secondname;
    private Button newinf;
    private ListView plist;
    private LinearLayout mainlinear;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) toolTitle=savedInstanceState.getString(KEY_To);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final SharedPreferences Profilesp=getActivity().getSharedPreferences(MainActivity.Profislespname, Context.MODE_PRIVATE);
        firstname= view.findViewById(R.id.firstname);
        secondname= view.findViewById(R.id.secondname);
        if(savedInstanceState==null) {
            firstname.setText(Profilesp.getString("Firstname", ""));
            secondname.setText(Profilesp.getString("Secondname", ""));
        }

        newinf= view.findViewById(R.id.newinf);
        newinf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText firstname= getView().findViewById(R.id.firstname);
                EditText secondname= getView().findViewById(R.id.secondname);
                NavigationView navigationView= getActivity().findViewById(R.id.navigationview);
                View Header=navigationView.getHeaderView(0);
                TextView firstnameview= Header.findViewById(R.id.firstnameview);
                TextView secondnameview= Header.findViewById(R.id.secondnameview);
                SharedPreferences.Editor ed=Profilesp.edit();
                ed.putString("Firstname",firstname.getText().toString());
                ed.putString("Secondname",secondname.getText().toString());
                ed.commit();
                firstnameview.setText(Profilesp.getString("Firstname",""));
                secondnameview.setText(Profilesp.getString("Secondname",""));
                InputMethodManager imm=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(getActivity().getCurrentFocus()!=null)
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
            }
        });

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
        final Cursor res=mdb.rawQuery("select * from "+DBhelper.NamesSubjects+";",null);
        class MyIdListAdapter extends BaseAdapter {

            @Override
            public int getCount() {
                return res.getCount();
            }

            @Override
            public Object getItem(int position) {
                return res.getString(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View simleitem=convertView;
                if(simleitem==null) simleitem=getLayoutInflater().inflate(R.layout.profilelistviewitem,parent,false);
                TextView textsub= simleitem.findViewById(R.id.profilesub);
                TextView textper=  simleitem.findViewById(R.id.profileper);
                DBhelper dbhelper=new DBhelper(getContext());
                SQLiteDatabase mdb = dbhelper.getWritableDatabase();
                res.moveToPosition(position);
                int right=0;
                int rob=0;
                Cursor exercise = mdb.rawQuery("select * from '"+res.getInt(res.getColumnIndex(DBhelper.SId))+"' ;", null);
                SharedPreferences profilesp = getActivity().getSharedPreferences(Profislespname, Context.MODE_PRIVATE);
                textsub.setText(res.getString(res.getColumnIndex(DBhelper.NameS)));
                for(int i=1;i<=exercise.getCount();i++) {
                    rob+=profilesp.getInt(res.getInt(res.getColumnIndex(DBhelper.SId))+"_"+i+"T",0);
                    right+=profilesp.getInt(res.getInt(res.getColumnIndex(DBhelper.SId))+"_"+i+"R",0);
                }
                if(rob!=0) textper.setText(100 * right / rob +"%");
                return simleitem;
            }
        }

        plist= view.findViewById(R.id.profilelistview);
        MyIdListAdapter lisad=new MyIdListAdapter();
        plist.setAdapter(lisad);

        mainlinear= view.findViewById(R.id.profilemainlinear);
        mainlinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(getActivity().getCurrentFocus()!=null)
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
            }
        });
        dbhelper.close();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar= getActivity().findViewById(R.id.toolbar);
        if(savedInstanceState==null) {
            toolTitle = toolbar.getTitle().toString();
        }
        toolbar.setTitle("Профиль");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        firstname=null;
        secondname=null;
        newinf=null;
        plist=null;
        mainlinear=null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_To,toolTitle);
    }

    @Override
    public void onStop() {
        super.onStop();
        toolbar.setTitle(toolTitle);
        InputMethodManager imm=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getActivity().getCurrentFocus()!=null)
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
    }



}
