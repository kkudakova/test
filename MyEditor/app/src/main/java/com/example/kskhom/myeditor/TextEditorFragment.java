package com.example.kskhom.myeditor;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;


/**
 * A placeholder fragment containing a simple view.
 */
public class TextEditorFragment extends Fragment {

    EditText editText;
    SharedPreferences defPrefs;

    private final static String ET_JUST_TEXT = "JUST_TEXT";

    public TextEditorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_text_editor, null);
        editText = (EditText)v.findViewById(R.id.just_text);
        return inflater.inflate(R.layout.fragment_text_editor, container, false);
    }

    public void onPause()
    {
        super.onPause();
        String text  = editText.getText().toString();
        defPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = defPrefs.edit();
        editor.putString(ET_JUST_TEXT,text);
        editor.commit();
    }

    public void onStop()
    {
        super.onStop();
        String text  = editText.getText().toString();
        if (((CheckBox)getActivity().findViewById(R.id.checkbox_db)).isChecked())
        {
            //save text in Preferences
            defPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = defPrefs.edit();
            editor.putString(ET_JUST_TEXT,text);
            editor.commit();
            return;
        }
        if (((CheckBox)getActivity().findViewById(R.id.checkbox_prefs)).isChecked())
        {
            //save text in database
            return;
        }

        if (((CheckBox)getActivity().findViewById(R.id.checkbox_file)).isChecked())
        {
            //save text in file
            return;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String text="";
        if (((CheckBox)getActivity().findViewById(R.id.checkbox_db)).isChecked())
        {
            //restore text from Preferences
            defPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            text = defPrefs.getString(ET_JUST_TEXT,"");
            editText.setText(text);
            return;
        }
        if (((CheckBox)getActivity().findViewById(R.id.checkbox_prefs)).isChecked())
        {
            //restore text from database
            return;
        }

        if (((CheckBox)getActivity().findViewById(R.id.checkbox_file)).isChecked())
        {
            //restore text from file
            return;
        }

    }
}
