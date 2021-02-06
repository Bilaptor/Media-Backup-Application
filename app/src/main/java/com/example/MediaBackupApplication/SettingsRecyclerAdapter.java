package com.example.MediaBackupApplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SettingsRecyclerAdapter extends RecyclerView.Adapter<SettingsRecyclerAdapter.ViewHolder> {

    private List<String> listeDeDirectories;
    private String checkedDirectories;
    static ViewHolder ViewHolder;//j'utilise cette variable de classe pour y avoir acces dans isChecked()
    //private CheckBox mCheckBox;

    public SettingsRecyclerAdapter(ArrayList<String> listeDeDirectories, String checkedDirectories) {
        this.listeDeDirectories = listeDeDirectories;
        this.checkedDirectories = checkedDirectories;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_row_data, parent,false);
        ViewHolder = new ViewHolder(view);//j'utilise cette variable de classe pour y avoir acces dans isChecked()

        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.album.setText(listeDeDirectories.get(position));
        if(checkedDirectories.contains(listeDeDirectories.get(position))) {
            holder.album.setChecked(true);
        }
        else {
            holder.album.setChecked(false);
        }
    }

    public boolean isChecked(int position) {
        boolean debug;
        debug = ViewHolder.album.isChecked();
        return debug;
    }

    public String getText(int position) {
        return listeDeDirectories.get(position);
    }

    @Override
    public int getItemCount() {
        return listeDeDirectories.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox album;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            album = itemView.findViewById(R.id.SettingsDirectoriesCheckBox);
        }
    }
}
