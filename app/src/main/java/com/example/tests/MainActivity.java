package com.example.tests;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static final String Profislespname="Profilesp";
    private int CNit;
    private static final String KEY_CN = "CN";
    private DrawerLayout mDrawer;
    private NavigationView navigationView;
    private SQLiteDatabase mdb;
    private DBhelper dbhelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        SharedPreferences profilesp = getSharedPreferences(Profislespname, Context.MODE_PRIVATE);
        mDrawer =  findViewById(R.id.Drawerlayout);
        navigationView =  findViewById(R.id.navigationview);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,R.string.app_name , R.string.app_name){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(getCurrentFocus()!=null&&imm!=null)
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
                if(getCurrentFocus()!=null)
                    getCurrentFocus().clearFocus();
            }
        };
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        dbhelper = new DBhelper(getApplicationContext());
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
        mdb=dbhelper.getReadableDatabase();
        Menu navmenu =navigationView.getMenu();
        Cursor res= mdb.rawQuery("select * from '"+DBhelper.NamesSubjects+"';",null);
        while(res.moveToNext()){
            navmenu.add(res.getString(res.getColumnIndex(DBhelper.NameS)));
        }
        res.close();
        View Header=navigationView.getHeaderView(0);
        TextView firstnameview= Header.findViewById(R.id.firstnameview);
        TextView secondnameview= Header.findViewById(R.id.secondnameview);
        firstnameview.setText(profilesp.getString("Firstname", ""));
        secondnameview.setText(profilesp.getString("Secondname", ""));
        /*TextView obstat=Header.findViewById(R.id.obstathead);
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
            stat1.close();
        }
        stat.close();
        if(rtries!=0)
        obstat.setText(String.valueOf(100*right/rtries));*/
        navigationView.setNavigationItemSelectedListener(this);
        if(savedInstanceState==null) {
            navigationView.getMenu().performIdentifierAction(R.id.vusolimpinf, 0);
            CNit = R.id.vusolimpinf;
        }
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm;
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null&&imm!=null)
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        if(savedInstanceState!=null){
            CNit=savedInstanceState.getInt(KEY_CN);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items,menu);
        return true;
}


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile && findViewById(R.id.newinf)==null) {
            if (findViewById(R.id.noteaddlinear)!=null) {
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("NoteList")).commit();
            }
            if(findViewById(R.id.curnoteet)!=null){
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("CurNote")).commit();
            }
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentholder, new ProfileFragment(),"Profile").commit();
            InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(getCurrentFocus()!=null&&imm!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
            }
            if(getCurrentFocus()!=null)
                getCurrentFocus().clearFocus();
            return true;
        } else if (item.getItemId() == R.id.profile && findViewById(R.id.newinf)!=null) {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("Profile")).commit();
            return true;
        }
        if (item.getItemId() == R.id.notelist && findViewById(R.id.noteaddlinear)==null) {
            if(findViewById(R.id.curnoteet)!=null){
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("CurNote")).commit();
            }
            if (findViewById(R.id.newinf)!=null){
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("Profile")).commit();
            }
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentholder, new NoteListFragment(),"NoteList").commit();
            InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(getCurrentFocus()!=null&&imm!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
            }
            if(getCurrentFocus()!=null)
                getCurrentFocus().clearFocus();
            return true;
        } else if (item.getItemId() == R.id.notelist && findViewById(R.id.noteaddlinear)!=null ) {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("NoteList")).commit();
            return true;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.vusolimpinf){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentholder,new VusInformation()).commit();
        }
        else {
            Cursor res=mdb.rawQuery("select "+DBhelper.SId+" from "+DBhelper.NamesSubjects+" where "+DBhelper.NameS+"='"+item.getTitle()+"';",null);
            res.moveToFirst();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentholder, new SubjectFragment(item.getTitle().toString(),
                 res.getInt(res.getColumnIndex(DBhelper.SId)) )).commit();
            res.close();
        }
        CNit=item.getItemId();
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CN,CNit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbhelper.close();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.closeDrawer(GravityCompat.START);
        }
        else if(findViewById(R.id.curnoteet)!=null){
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("CurNote")).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentholder, new NoteListFragment(),"NoteList").commit();
        }
        else if (findViewById(R.id.newinf)!=null) {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("Profile")).commit();
        }
        else if (findViewById(R.id.noteaddlinear)!=null) {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("NoteList")).commit();
        }
        else if (findViewById(R.id.linears)!=null) {
            navigationView.getMenu().performIdentifierAction(CNit,0);
        }
        else if (findViewById(R.id.button1) != null) {
            navigationView.getMenu().performIdentifierAction(R.id.vusolimpinf,0);
        }
        else super.onBackPressed();
    }
}
