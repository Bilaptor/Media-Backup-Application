package com.example.MediaBackupApplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SigninActivity extends AppCompatActivity {


    private String username;
    private TextView email_entrée;
    private EditText password1, password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        password1 = (EditText) findViewById(R.id.et_password1);
        password2 = (EditText) findViewById(R.id.et_password2);
        email_entrée = (TextView) findViewById(R.id.affichage_text_email_signin);
        email_entrée.setText(username);

    }

    public void btn_signin(View view) {
        Intent intent = new Intent( this, UsernameActivity.class);

        if(isMotDePasseConforme(password1.getText().toString(), password2.getText().toString())) {
            startActivity(intent);
        }
    }

    public boolean isMotDePasseConforme(String pw1, String pw2) {
        boolean reponse = false;
        if (pw1.length() > 3) {

            if(pw1.equals(pw2)) {
                //doit communiquer le mot de passe au serveur**********************************************************************************************************************
                reponse = true;
                Toast.makeText(this, "Le compte a été créé", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Les mots de passes ne sont pas identiques", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "Le mot de passe doit avoir au moins 4 caractères", Toast.LENGTH_LONG).show();
        }
        return reponse;
    }
}