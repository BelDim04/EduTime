package com.example.tests;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class DBhelper extends SQLiteOpenHelper {

        private static String DB_PATH = "/data/data/com.example.tests/databases/";
        private static String DB_NAME = "DB5.db";
        private SQLiteDatabase myDataBase;
        private final Context mContext;

    public static final String NamesSubjects="NamesSubjects";
    public static final String SId="SId";
    public static final String NameS="NameS";


    public static final String ExId="ExId";
    public static final String ExName="ExName";


    public static final String ExTId="ExTId";
    public static final String ExText="ExText";
    public static final String ExHelp="ExHelp";
    public static final String ExAnswer="ExAnswer";
    public static final String ExStatus="ExStatus";
    public static final String ExTries="ExTries";






    public static final String Vuses="Vuses";
    public static final String VusId="VusId";
    public static final String VusName="VusName";
    public static final String VusBall="VusBall";

    public static final String Olimps="Olimps";
    public static final String OlimpId="OlimpId";
    public static final String OlimpName="OlimpName";
    public static final String OlimpSubj="OlimpSubj";
    public static final String OlimpLevel="OlimpLevel";

    public static final String Notes="Notes";
    public static final String NoteId="NoteId";
    public static final String NoteName="NoteName";
    public static final String NoteText="NoteText";


        public DBhelper(Context context) {
            super(context, DB_NAME, null, 1);
            this.mContext = context;
        }


        public void createDataBase() throws IOException{
            boolean dbExist = checkDataBase();

            if(dbExist){
            }else{
                //вызывая этот метод создаем пустую базу, позже она будет перезаписана
                this.getReadableDatabase();
                try {
                    copyDataBase();
                } catch (IOException e) {

                }
            }
        }

        private boolean checkDataBase(){
            SQLiteDatabase checkDB = null;

            try{
                String myPath = DB_PATH + DB_NAME;
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            }catch(SQLiteException e){

            }
            if(checkDB != null){
                checkDB.close();
            }
            return checkDB != null;
        }


        private void copyDataBase() throws IOException {
            //Открываем локальную БД как входящий поток
            InputStream myInput = mContext.getAssets().open(DB_NAME);

            //Путь ко вновь созданной БД
            String outFileName = DB_PATH + DB_NAME;

            //Открываем пустую базу данных как исходящий поток
            OutputStream myOutput = new FileOutputStream(outFileName);

            //перемещаем байты из входящего файла в исходящий
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            //закрываем потоки
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }

        public void openDataBase() throws SQLException {
            //открываем БД
            String myPath = DB_PATH + DB_NAME;
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }

        @Override
        public synchronized void close() {
            if(myDataBase != null)
                myDataBase.close();
            super.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }
}
