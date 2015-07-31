package com.example.kskhom.myeditor;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;
import java.util.prefs.Preferences;


public class TextEditor extends ActionBarActivity {

    private final static String CHECKBOX_PREF = "CHB_PREF";
    private final static String CHECKBOX_DB = "CHB_DB";
    private final static String CHECKBOX_FILE = "CHB_FILE";
    private final static String ET_JUST_TEXT = "JUST_TEXT";
    private final String FILENAME = "file";

    CheckBox chb_pref;
    CheckBox chb_database;
    CheckBox chb_file;
    EditText editText;
    SharedPreferences defPrefs = null;

    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);

        chb_pref = (CheckBox) findViewById(R.id.checkbox_prefs);
        chb_database = (CheckBox) findViewById(R.id.checkbox_db);
        chb_file = (CheckBox) findViewById(R.id.checkbox_file);
        defPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // restoring status
        chb_pref.setChecked(defPrefs.getBoolean(CHECKBOX_PREF, false));
        chb_database.setChecked(defPrefs.getBoolean(CHECKBOX_DB, false));
        chb_file.setChecked(defPrefs.getBoolean(CHECKBOX_FILE, false));

        editText = (EditText) findViewById(R.id.just_text);

        dbHelper = new DBHelper(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onStart() {
        super.onStart();
        if (chb_pref.isChecked()) {
            if (defPrefs.getString(ET_JUST_TEXT, "") != null) {
                editText.setText(defPrefs.getString(ET_JUST_TEXT, ""));
            }
            return;
        }

        if (chb_database.isChecked()) {
            db = dbHelper.getWritableDatabase();
            Cursor c = db.query("mytable", null, null, null, null, null, null);
            if (c.moveToFirst()) {
                int nameColIndex = c.getColumnIndex(ET_JUST_TEXT);
                editText.setText(c.getString(nameColIndex));
                c.close();
            }
            dbHelper.close();
            return;
        }

        if (chb_file.isChecked()) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        openFileInput(FILENAME)));
                String str = "";
                while ((str = br.readLine()) != null) {
                    editText.setText(str);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = defPrefs.edit();
        editor.putBoolean(CHECKBOX_PREF, chb_pref.isChecked());
        editor.putBoolean(CHECKBOX_DB, chb_database.isChecked());
        editor.putBoolean(CHECKBOX_FILE, chb_file.isChecked());
        if (chb_pref.isChecked()) {
            editor.putString(ET_JUST_TEXT, editText.getText().toString());
        }
        editor.commit();
        if (chb_database.isChecked()) {
            db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(ET_JUST_TEXT, editText.getText().toString());
            db.delete("mytable", null, null);
            long rowID = db.insert("mytable", null, cv);
            Log.d("Info", "row inserted, ID = " + rowID);
            dbHelper.close();
        }
        if (chb_file.isChecked()) {
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                        openFileOutput(FILENAME, MODE_PRIVATE)));
                bw.write(editText.getText().toString());
                bw.close();
                Log.d("Info", editText.getText().toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onCheckboxPrefsClicked(View view) {
        // chb_pref.setChecked(!chb_pref.isChecked());
        Log.println(0, "Info", "onCheckboxPrefsClicked");
    }

    public void onCheckboxDBClicked(View view) {
        //chb_database.setChecked(!chb_database.isChecked());
        Log.println(0, "Info", "onCheckboxDBClicked");
    }

    public void onCheckboxFileClicked(View view) {
        // chb_file.setChecked(!chb_file.isChecked());
        Log.println(0, "Info", "onCheckboxFileClicked");
    }


    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("Info", "--- onCreate database ---");
            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + ET_JUST_TEXT
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}


