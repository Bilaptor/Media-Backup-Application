package com.example.MediaBackupApplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UsernameActivity extends AppCompatActivity {


    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    //variables qui servent à afficher les edit text dans le username activity
    private EditText mIp , mUsername;

    private String adresseip, username, path;//utilisés pour envoyer au serveur
    private boolean checkbox;
    private List<String> database_usernames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);

        database_usernames.add(0, "guillaume_lamothe@hotmail.ca");//émule le fait que le serveur reconnait l'adresse courriel
        database_usernames.add(1, "patryl_@hotmail.com");

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);//création du fichier de préférences
        mEditor = mPreferences.edit();//permet de mettre des shits dedans

        mIp = (EditText) findViewById((R.id.et_ip));
        mUsername = (EditText) findViewById((R.id.et_email));

        checkSharedPreferences();//appel la fonction qui regarde les préférences
    }

    public void btn_email_login(View v) {
        Intent intent;

        //permet de prendre les valeurs dans les edittexts et les met dans les strings
        adresseip = ((EditText)findViewById(R.id.et_ip)).getText().toString();
        username = ((EditText)findViewById(R.id.et_email)).getText().toString();

        if (!isValidIp(adresseip)) {
            Toast.makeText(this, "adresse Ip non valide", Toast.LENGTH_SHORT).show();
        }
        else {

            if (!isValidEmail(username)) {
                Toast.makeText(this, "adresse courriel non valide", Toast.LENGTH_SHORT).show();
            }
            else {
                //permet de faire le choix de choisir le "signin" or "login" activity dépendamment de l'adresse email
                if (database_usernames.contains(username)) {
                    intent = new Intent( this, LoginActivity.class);
                }
                else {
                    intent = new Intent( this, SigninActivity.class);
                }
                storeSharedPreferences();

                intent.putExtra("ip", adresseip);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        }
    }

    private void storeSharedPreferences() {
        String ip = mIp.getText().toString();
        mEditor.putString(getString(R.string.sp_adresseip), ip);
        mEditor.commit();
    }

    //regarde dans les préférences et met dans les champs les editText précédement entrée (permet de se souvenir des input du user quand l'application se ferme)
    private void checkSharedPreferences(){
        String ip = mPreferences.getString(getString(R.string.sp_adresseip), "");
        String email = mPreferences.getString(getString(R.string.sp_username), "");
        String checkbox = mPreferences.getString(getString(R.string.sp_checkbox), "False");

        //sert a mettre le string qui était dans les préférences dans le champ du edit text du login activity
        mIp.setText(ip);
        if(checkbox.equals("True")) { mUsername.setText(email); }
        else { mUsername.setText(""); }

    }

    //regarde le edittext du username correspond a une adresse courriel
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    //regarde le edittext de l'adresse ip pour savoir si elle est valide
    public static boolean isValidIp(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.IP_ADDRESS.matcher(target).matches());
    }
}