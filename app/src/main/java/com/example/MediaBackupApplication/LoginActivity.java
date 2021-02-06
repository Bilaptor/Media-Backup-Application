package com.example.MediaBackupApplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    //variables qui servent à afficher les EditText dans le login activity
    private EditText mPassword;
    private CheckBox mCheckBox;
    private TextView email_entrée, ip_entrée;

    private String adresseIp, username, path;//variable de classe pour envoyer au serveur
    //private List<String> database_usernames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        adresseIp = intent.getStringExtra("ip");
        username = intent.getStringExtra("username");

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);//création du fichier de préférences
        mEditor = mPreferences.edit();//permet de mettre des shits dedans

        //associe  les variable a leur id de composant
        ip_entrée = (TextView) findViewById(R.id.affichage_text_ip);
        mCheckBox = (CheckBox) findViewById((R.id.checkBox));
        mPassword = (EditText) findViewById((R.id.et_password));
        email_entrée = (TextView) findViewById(R.id.affichage_text_email);
        //puis met les valeur dans les champs de text
        ip_entrée.setText(adresseIp);
        email_entrée.setText(username);

        checkSharedPreferences();
    }

    //regarde dans les préférences et met dans les champs les editText précédement entrée (permet de se souvenir des input du user quand l'application se ferme)
    private void checkSharedPreferences(){
        String checkbox = mPreferences.getString(getString(R.string.sp_checkbox), "False");
        String password = mPreferences.getString(getString(R.string.sp_password), "");

        //sert a mettre le string qui était dans les préférences dans le champ du edit text du login activity
        mPassword.setText(password);

        //met le bon état dans la checkbox du login activity aurait pu etre simplifié avec: mCheckBox.setChecked(checkbox.equals("True"))
        //                                                                                  ceci fait les deux donc si c'est faux, ca met false
        if(checkbox.equals("True")) {
            mCheckBox.setChecked(true); }
        else{
            mCheckBox.setChecked(false); }
    }

    public void btn_login(View view) {
        Intent intent = new Intent( this, MainActivity.class);

        storeSharedPreferences();

        startActivity(intent);
    }

    private void storeSharedPreferences() {

        if (mCheckBox.isChecked()) {
            mEditor.putString(getString(R.string.sp_checkbox), "True");//sauvegarde la boite coché dans les préférences si elle est coché
            mEditor.putString(getString(R.string.sp_username), username);//sauvegarde le username/email
            String password = mPassword.getText().toString();
            mEditor.putString(getString(R.string.sp_password), password);//sauvegarde le mot de passe
        }
        else {
            mEditor.putString(getString(R.string.sp_checkbox), "False");
            mEditor.putString(getString(R.string.sp_username), "");
            mEditor.putString(getString(R.string.sp_password), "");
        }
        mEditor.commit();
    }
}