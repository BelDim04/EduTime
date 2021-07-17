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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class NoteListFragment extends Fragment {
    private  String toolTitle;
    private static final String KEY_To = "To";
    private Toolbar toolbar;

    private DBhelper dbhelper;
    private EditText noteaddet;
    private ImageButton noteaddbutton;
    private ListView notelist;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) toolTitle=savedInstanceState.getString(KEY_To);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notelist_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        notelist=view.findViewById(R.id.noteaddlist);
        final ArrayList<String> notenames=new ArrayList<>();
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
        final SQLiteDatabase mdb=dbhelper.getWritableDatabase();
        Cursor cur=mdb.rawQuery("select "+DBhelper.NoteName+" from "+DBhelper.Notes+";",null);
        while(cur.moveToNext()) notenames.add(cur.getString(cur.getColumnIndex(DBhelper.NoteName)));
        cur.close();
        class myadapter extends ArrayAdapter<String> {
            public myadapter(@NonNull Context context) {
                super(context, android.R.layout.simple_list_item_1,notenames);
            }

            @NonNull
            @Override
            public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view=convertView;
                if (view==null) view=getLayoutInflater().inflate(android.R.layout.simple_list_item_1,parent,false);
                TextView text1= view.findViewById(android.R.id.text1);
                text1.setText(notenames.get(position));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(getActivity().getSupportFragmentManager().findFragmentByTag("NoteList")).commit();
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragmentholder,new CurNoteFragment(notenames.get(position),toolTitle),"CurNote").commit();
                    }
                });
                return view;
            }
        }
        final myadapter adapter=new myadapter(getContext());
        notelist.setAdapter(adapter);
        noteaddet=view.findViewById(R.id.noteaddet);
        noteaddbutton=view.findViewById(R.id.noteaddbutton);
        noteaddbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<notenames.size();i++){
                    if(notenames.get(i).equals(noteaddet.getText().toString())) return;
                }
                if(noteaddet.getText().toString().equals("")) return;
                notenames.add(noteaddet.getText().toString());
                ContentValues notesp=new ContentValues();
                notesp.put(DBhelper.NoteName,noteaddet.getText().toString());
                mdb.insert(DBhelper.Notes,null,notesp);
                adapter.notifyDataSetChanged();
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
        toolbar.setTitle("Заметки");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        noteaddet=null;
        noteaddbutton=null;
        notelist=null;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbhelper.close();
    }
}
