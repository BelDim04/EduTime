package com.example.tests;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.IOException;
import java.sql.SQLException;

public class VusInformation extends Fragment {

    private TableLayout vusinf;
    private TableLayout olimpinf;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vusinformation,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vusinf= view.findViewById(R.id.vusinf);
        olimpinf=view.findViewById(R.id.olimpinf);
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
        SQLiteDatabase mdb=dbhelper.getReadableDatabase();
       Cursor vusc=mdb.rawQuery("select "+DBhelper.VusName+" , "+DBhelper.VusBall+" from "+DBhelper.Vuses+" ;",null);
        while (vusc.moveToNext()){
            TableRow tablerowvus=(TableRow) getLayoutInflater().inflate(R.layout.tablerowvus,(ViewGroup)view,false);
            TextView vusname=(TextView) tablerowvus.getChildAt(0);
            TextView vusball=(TextView) tablerowvus.getChildAt(1);
            vusname.setText(vusc.getString(vusc.getColumnIndex(DBhelper.VusName)));
            vusball.setText(vusc.getString(vusc.getColumnIndex(DBhelper.VusBall)));
            vusinf.addView(tablerowvus);
        }
        vusc.close();
        Cursor olimpc=mdb.rawQuery("select "+DBhelper.OlimpName+" , "+DBhelper.OlimpSubj+" ,"+DBhelper.OlimpLevel+" from "+DBhelper.Olimps+" ;",null);
        while (olimpc.moveToNext()){
            TableRow tablerowolimp=(TableRow) getLayoutInflater().inflate(R.layout.tablerowolimp,(ViewGroup)view,false);
            TextView olimpname=(TextView) tablerowolimp.getChildAt(0);
            TextView olimpsubj=(TextView) tablerowolimp.getChildAt(1);
            TextView olimplevel=(TextView) tablerowolimp.getChildAt(2);
            olimpname.setText(olimpc.getString(olimpc.getColumnIndex(DBhelper.OlimpName)));
            olimpsubj.setText(olimpc.getString(olimpc.getColumnIndex(DBhelper.OlimpSubj)));
            olimplevel.setText(olimpc.getString(olimpc.getColumnIndex(DBhelper.OlimpLevel)));
            olimpinf.addView(tablerowolimp);
        }
        olimpc.close();
        dbhelper.close();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MaterialToolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        vusinf= null;
        olimpinf=null;
    }
}
