package com.example.MediaBackupApplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder> {

    private String path;
    private List<String> listeDeNomImages;
    static private String fullname;

    public RecyclerAdapter(String path, List<String> listeDeNomImages) {
        this.path = path;
        this.listeDeNomImages = listeDeNomImages;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_data_gridview, parent,false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(view);

        return imageViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        fullname = path + "/" + listeDeNomImages.get(position);
        //try {
            //File f = new File(path, listeDeNomImages.get(position));
            //Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        //options.inScaled = true;
        //options.inDensity = 400; //400 est la densité de pixels average pour les téléphones
        //options.inTargetDensity = 400; ///densité de pixels du imageview
        options.inSampleSize = 16;

        Bitmap b = BitmapFactory.decodeFile(fullname, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        //BitmapFactory.decodeFile(fullname, options);

            //int nh = (int) ( b.getHeight() * (20.0 / b.getWidth()) );
            //Bitmap scaled = Bitmap.createScaledBitmap(b, 20, nh, true);

            //holder.album.setImageBitmap(scaled);
        //Bitmap b = decodeSampledBitmapFromResource(options, fullname, 1, 1);
        holder.album.setImageBitmap(b);
        /*}
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public int getItemCount() {
        return listeDeNomImages.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView album;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            album = itemView.findViewById(R.id.images);
        }
    }

    //calcule la meilleure résolution a avoir pour bien s'afficher selon le imageView, reqwidth et reqheight sont les dimention du imageview
    public static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int rawHeight = options.outHeight;
        final int rawWidth = options.outWidth;
        int inSampleSize = 1;

        if (rawHeight > reqHeight || rawWidth > reqWidth) {

            final int halfHeight = rawHeight / 2;
            final int halfWidth = rawWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(BitmapFactory.Options options, String fullName, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        //final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fullName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(fullName, options);
    }
}
