package com.example.MediaBackupApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int My_READ_PERMISSION_CODE = 101;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private ArrayList<String> media;
    private int gridQuantity = 3;
    private String adresseIp;
    private String path = "";
    private ArrayList<String> allDirectories = new ArrayList<String>();
    private ArrayList<String> wantedDirectories = new ArrayList<String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);//création du fichier de préférences
        mEditor = mPreferences.edit();//permet de mettre des shits dedans
        checkSharedPreferences();

        Intent intent = getIntent();
        adresseIp = intent.getStringExtra("ip");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, My_READ_PERMISSION_CODE);
        }else {//quoi faire par défault une fois que les permission sont autorisé
            showPictures();
        }
    }

    private void checkSharedPreferences(){//doit lire les preférences des checkbox des settings
        String spWantedDirectoriesInOne = mPreferences.getString(getString(R.string.sp_wanted_directories), "");
        wantedDirectories = unfoldDirectories(spWantedDirectoriesInOne);


        //sert a mettre le string qui était dans les préférences dans le champ du edit text du login activity
        //mIp.setText(ip);
        //if(checkbox.equals("True")) { mUsername.setText(email); }
        //else { mUsername.setText(""); }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_parametres:
                Intent intent = new Intent( this, CustomSettingsActivity.class);
                String allDirectoriesInOneString = "";
                for(int i = 0 ; i < allDirectories.size() ; i++) {
                    allDirectoriesInOneString = allDirectoriesInOneString + ";" + allDirectories.get(i);
                }
                intent.putExtra("directories", allDirectoriesInOneString);
                startActivity(intent);
                return true;
            case R.id.menu_telephone:
                return true;
            case R.id.menu_serveur:
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void showPictures(){
        GridView gallery = (GridView) findViewById(R.id.main_gridview);
        gallery.setNumColumns(gridQuantity);

        gallery.setAdapter(new MediaAdapter(this));

        gallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (null != media && !media.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "position " + position + " " + media.get(position), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static ArrayList<String> unfoldDirectories(String allDirectoriesInOneString) {//prend la
        ArrayList<String> unfoldedDirectories = new ArrayList<String>();
        String[] pathArray = allDirectoriesInOneString.split(";");
        if (pathArray.length != 0) {
            for(int i=1; i<pathArray.length;i++) {
                unfoldedDirectories.add(pathArray[i]);
            }
        }
        return unfoldedDirectories;
    }

    @Override//permet de mettre l'application en background quand on fait back button au lieu de retourner a la page de connection
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override//action exécuté tout de suite apres avoir accepté les permissions de lire le internal storage
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == My_READ_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Lecture de la mémoire interne autorisé", Toast.LENGTH_LONG).show();
                showPictures();
            }else {
                Toast.makeText(this, "Lecture de la mémoire interne refusé", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class MediaAdapter extends BaseAdapter {

        private Activity context;

        public MediaAdapter(Activity localContext) {
            context = localContext;
            media = getAllWantedMediaPath(context);
        }

        public int getCount() {
            return media.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView picturesView;
            if (convertView == null) {
                picturesView = new ImageView(context);
                picturesView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //picturesView.setLayoutParams(new GridView.LayoutParams(270, 270));
            }
            else {
                picturesView = (ImageView) convertView;
            }

            // Placeholder est l'image qui affiche le temps de charger l'image
            Glide.with(context).load(media.get(position)).placeholder(R.drawable.ic_launcher_foreground).centerCrop().into(picturesView);

            return picturesView;
        }

        private ArrayList<String> getAllWantedMediaPath(Activity activity) {
            Cursor cursor;
            int column_index_data;
            ArrayList<String> listOfAllMedia = new ArrayList<String>();
            String absolutePathOfMedia;
            Uri uriImage = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


            String[] projection = {MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_MODIFIED, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATE_MODIFIED };

            cursor = activity.getContentResolver().query(uriImage, projection, null, null, "DATE_MODIFIED DESC");

            column_index_data = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            //column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfMedia = cursor.getString(column_index_data);

                String path = "";
                String[] pathArray = absolutePathOfMedia.split("/");
                if (pathArray.length != 0) {
                    for(int i=4;i<pathArray.length -1; i++) {//on commence la boucle à 4pour ne pas ajouter le "storage/emulated/0" vu qu'il est commun a tous les directories
                        path = path + "/" + pathArray[i] ;
                    }
                }//garde seulement la partie du path absolu qui est désiré (n'inclut pas:storage/emulated/0 et le nom de la photo)

                if(!allDirectories.contains(path)) {
                    allDirectories.add(path);
                }//ajoute le path dans la liste s'il n'est pas déjà présent

                Collections.sort(allDirectories, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });//met la liste de directories en ordre alphabétique

                if(wantedDirectories.contains(path)) {//met la photo dans la liste de media a afficher si le path de la photo fait partis de la liste des directories sélectionné dans les settings
                    listOfAllMedia.add(absolutePathOfMedia);
                }
            }
            return listOfAllMedia;
        }
    }
}
