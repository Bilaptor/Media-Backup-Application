package com.example.MediaBackupApplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;


public class CustomSettingsActivity extends AppCompatActivity {

    private SwitchCompat mSettingsSwitch;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private ArrayList<String> allDirectories = new ArrayList<String>();
    private String wantedDirectoriesFromSp;

    private int settingsGridQuantity = 1;
    private SettingsRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private View v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_settings);

        Intent intent = getIntent();
        String intentDirectoriesInOneString = intent.getStringExtra("directories");
        allDirectories = MainActivity.unfoldDirectories(intentDirectoriesInOneString);


        mSettingsSwitch = findViewById(R.id.switch1);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);//création du fichier de préférences
        mEditor = mPreferences.edit();//permet de mettre des shits dedans
        checkSharedPreferences();

        mSettingsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mEditor.putString(getString(R.string.sp_settings_switch),"True");
                }
                else {
                    mEditor.putString(getString(R.string.sp_settings_switch),"False");
                }
                mEditor.commit();
            }
        });//sauvegarde l'état de la switch automatiquement quand elle est flicked

        afficherDirectoriesSiPossible();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override// permet d'associer un back press sur la fleche d'action dans la barre de menu
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void afficherDirectoriesSiPossible() {

        if(allDirectories.size() == 0) {
            Toast.makeText(this, "Il n'y a pas de photos dans le téléphone", Toast.LENGTH_LONG).show();
        }
        else {
            for (int i = 0; i<allDirectories.size(); i++) {

                recyclerView = findViewById(R.id.settings_recycler_view);
                layoutManager = new GridLayoutManager(this, settingsGridQuantity);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new SettingsRecyclerAdapter(allDirectories, wantedDirectoriesFromSp);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private void checkSharedPreferences(){//doit lire les preférences des checkbox des settings et de la switch
        wantedDirectoriesFromSp = mPreferences.getString(getString(R.string.sp_wanted_directories), "");
        String switchState = mPreferences.getString(getString(R.string.sp_settings_switch), "False");
        if(switchState.equals("True")) {
            mSettingsSwitch.setChecked(true);
        }
        else {
            mSettingsSwitch.setChecked(false);
        }
    }

    private void storeSharedPreferences() {//sauvegarde les checkbox qui sont coché dans les shared preferences
        String directoriesCheckedInSettings = "";
        SettingsRecyclerAdapter.ViewHolder view;
        CheckBox box;
        for(int i=0; i<adapter.getItemCount(); i++) {//permet de tout mettre les checked boxes dans un string pour les sauvegarder
            boolean bitch3 = adapter.isChecked(i);//valeur bool de la derniere checkbox visible
            //view = recyclerView.findViewHolderForAdapterPosition(i);
            view = (SettingsRecyclerAdapter.ViewHolder);
            int bitch = adapter.ViewHolder.getAdapterPosition();//position de la derniere checkbox visible
            boolean bitch2 = adapter.ViewHolder.album.isChecked();//utilise la variable static dans la classe settingsrecycleradapter revient au meme que adapter.isChecked()
            if(view.album.isChecked()) {
                directoriesCheckedInSettings = directoriesCheckedInSettings + ";" + adapter.getText(i);
            }
        }
        mEditor.putString(getString(R.string.sp_wanted_directories), directoriesCheckedInSettings);
        mEditor.commit();
    }


    @Override//permet de retourner au main activity avec un back press
    public void onBackPressed() {
        storeSharedPreferences();
        Intent a = new Intent(this, MainActivity.class);
        startActivity(a);
    }
}