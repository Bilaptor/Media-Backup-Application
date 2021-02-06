package com.example.MediaBackupApplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MainActivityOld extends AppCompatActivity {

    private static final int My_READ_PERMISSION_CODE = 101;

    private int gridQuantity = 1;
    private String adresseIp;
    private String path = "";
    private List<String> listeDeNomsVideos = new ArrayList<String>();
    private List<String> listeDeNomsPhotos = new ArrayList<String>();//À faire: Afficher avec le GridView à la place d'un ImageView + trouver comment afficher le thmbnail d'une video + trouver comment afficher les photos en ordre chronologiques
                                           // + trouver comment afficher dans les settings touts les répertoire qui contiennent une image et mettre une checkbox sur celui à afficher
    //ImageView img;//utilisé dans loadimagefromstoragedevice()
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_old);

        Intent intent = getIntent();
        adresseIp = intent.getStringExtra("ip");

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        path = mPreferences.getString(getString(R.string.sp_directory_path), getString(R.string.path_directory_default));//valeur par defaut jamais utilisé car il prend toujours, permet de prendre le path à partir des shared preferences

        //calculerHauteurImages();

        if(ContextCompat.checkSelfPermission(MainActivityOld.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions( MainActivityOld.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, My_READ_PERMISSION_CODE);
        }else {//quoi faire par défault une fois que les permission sont autorisé

            listFilesInDirectory();
            afficherPhotosSiPossible();
        }
    }

    public void afficherPhotosSiPossible() {

        if(listeDeNomsPhotos.size() == 0) {
            Toast.makeText(this, "Il n'y a pas de photos dans le répertoire : "+ path, Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, path, Toast.LENGTH_LONG).show();
            for (int i = 0; i<listeDeNomsPhotos.size(); i++) {

                recyclerView = findViewById(R.id.recycler_view);
                layoutManager = new GridLayoutManager(this, gridQuantity);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new RecyclerAdapter(path, listeDeNomsPhotos);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == My_READ_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Lecture de la mémoire interne autorisé", Toast.LENGTH_LONG).show();
                listFilesInDirectory();
                afficherPhotosSiPossible();

            }else {
                Toast.makeText(this, "Lecture de la mémoire interne refusé", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Afficher une image où le path est le chemin a emprunter pour se rendre au dossier et nameAndExtention est la photo/video ex: 2020420.jpg
    private void loadImageFromStorage(String path, String nameAndExtention) {
    /*
        try {
            File f = new File(path, nameAndExtention);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            //img = (ImageView)findViewById(R.id.image1);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }

    private void loadVideoThumbnailFromStorage(String path, String nameAndExtention) {
        /*try {

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }

    //cette fonction nous donne aussi les nom des videos
    public void listFilesInDirectory() {
        File directory = new File(path);
        File[] files = directory.listFiles();
        List<String> listePhotosTemporaire = new ArrayList<String>();
        List<String> listeVidoeTemporaire = new ArrayList<String>();

        if(files != null) {
            //met en ordre chonologique ou l'item le plux vieux est dans [0]
            Arrays.sort(files, new Comparator<File>(){
                public int compare(File f1, File f2)
                {
                    return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                } });

            //split le répertoire de fichier en 2 liste, photos et vidéos
            for (int i = 0; i < files.length; i++) {
                if ((getExtentionType(files[i].getName()).equals("jpg"))) {
                    listePhotosTemporaire.add(files[i].getName());
                }
                else {
                    listeVidoeTemporaire.add(files[i].getName());
                }
            }

            //pour afficher l'item le plus recent en premier on doit réinverser la liste
            for(int i = listePhotosTemporaire.size(); i>0; i--) {
                listeDeNomsPhotos.add(listePhotosTemporaire.get(i-1));
            }
            for(int i = listeVidoeTemporaire.size(); i>0; i--) {
                listeDeNomsVideos.add(listeVidoeTemporaire.get(i-1));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override//permet de mettre l'application en background quand on fait back button au lieu de retourner a la page de connection
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_parametres:
                startActivity((new Intent( this, SettingsActivity.class)));
                return true;
            case R.id.menu_telephone:
                return true;
            case R.id.menu_serveur:
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    //retourne un String contenant le nom de l'extention
    public String getExtentionType(String fileName) {
            String[] filenameArray = fileName.split("\\.");
            if (filenameArray.length != 0) {
                return filenameArray[filenameArray.length-1];
            }
            return "Le fichier n'a pas d'extention";
    }

    //je voulais implementer une facon de set la hauteur du imageview a partir de la largeur de l'écran du user... marche pas pour l'instant
    //j'arrive pas a lire la largeur du image view et j'arrive pas a modifier le parametre hauteur des imageview holder dans le rowdata gridview
    public void calculerHauteurImages() {
        ImageView v = (ImageView) findViewById(R.id.dimensions_display_holder);
        int largeur = v.getWidth();
        ImageView v2 = findViewById(R.id.images);
        //v2.requestLayout();
        //v2.getLayoutParams().height = largeur/3;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, largeur/3);
        v2.setLayoutParams(layoutParams);
    }

}