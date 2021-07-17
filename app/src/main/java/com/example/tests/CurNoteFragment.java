package com.example.tests;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.sql.SQLException;

public class CurNoteFragment extends Fragment {
    private Toolbar toolbar;
    private static final String KEY_To = "To";
    private String NoteName;
    private SQLiteDatabase mdb;
    private String toolTitle;
    private EditText et;
    private Button notedelete;
    private DBhelper dbhelper;
    public CurNoteFragment() {
    }

    public CurNoteFragment(String noteName, String toolname) {
        NoteName = noteName;
        toolTitle = toolname;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) toolTitle=savedInstanceState.getString(KEY_To);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.curnote_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        dbhelper=new DBhelper(getContext());
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
        mdb=dbhelper.getWritableDatabase();
        Cursor cur=mdb.rawQuery("select "+DBhelper.NoteText+" from "+DBhelper.Notes+" where "+DBhelper.NoteName+" = '"+NoteName+"' ;",null);
        cur.moveToFirst();
        et=view.findViewById(R.id.curnoteet);
        if(savedInstanceState==null)
            et.setText(cur.getString(cur.getColumnIndex(DBhelper.NoteText)));
        notedelete=view.findViewById(R.id.notedelete);
        notedelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(getActivity().getSupportFragmentManager().findFragmentByTag("CurNote")).commit();
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragmentholder, new NoteListFragment(),"NoteList").commit();
                mdb.delete(DBhelper.Notes,DBhelper.NoteName+"=?",new String[] {NoteName});
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar= getActivity().findViewById(R.id.toolbar);
        if(savedInstanceState==null) {
            toolTitle = toolbar.getTitle().toString();
        }
        toolbar.setTitle("Заметки   "+NoteName);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        et = null;
        notedelete = null;
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
        ContentValues notesp=new ContentValues();
        notesp.put(DBhelper.NoteText,et.getText().toString());
        mdb.update(DBhelper.Notes,notesp,DBhelper.NoteName+"=?",new String[] {NoteName});
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbhelper.close();
    }
}
