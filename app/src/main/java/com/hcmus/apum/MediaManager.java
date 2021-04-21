package com.hcmus.apum;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

public class MediaManager {
    private ArrayList<String> locations;
    private ArrayList<String> fav_images;

    public void updateLocations(Context context) {
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage = null;
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            listOfAllImages.add(absolutePathOfImage);
        }
        locations = listOfAllImages;
    }
    public void addFavorites(ArrayList<String> i, int pos){
        ArrayList<String> listFavorites = new ArrayList<>();
        //String absolutePathOfImage = null;
        listFavorites.add(i.get(pos));
        fav_images = listFavorites;
    }
    public void removeFavorites(ArrayList<String> i, int pos){
        fav_images.remove(i.get(pos));
    }
    public ArrayList<String> getLocations() {
        return locations;
    }
    public ArrayList<String> getFavoriteLocations() {return fav_images; }
    public Bitmap createThumbnail(String path) {
        File img = new File(path);
        if (!img.exists()) {
            img = new File(Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + R.drawable.ic_image).toString());
        }
            BitmapFactory.Options bitmapOpt = new BitmapFactory.Options();
            bitmapOpt.inJustDecodeBounds = true;  // Get img size
            BitmapFactory.decodeFile(img.getAbsolutePath(), bitmapOpt);

            // find the best scaling factor for the desired dimensions
            int preferredW = 400, preferredH = 300;
            float wScale = (float) bitmapOpt.outWidth / preferredW,
                    hScale = (float) bitmapOpt.outHeight / preferredH;
            float scale = Math.min(wScale, hScale);
            int sampleSize = 1;
            while (sampleSize < scale) {
                sampleSize *= 2;
            }
            bitmapOpt.inSampleSize = sampleSize;  // inSampleSize must be power of 2
            bitmapOpt.inJustDecodeBounds = false;  // Load the image

            // Load part of image to make thumbnail
            Bitmap thumbnail = BitmapFactory.decodeFile(img.getAbsolutePath(), bitmapOpt);
//            thumbnail = ThumbnailUtils.extractThumbnail(thumbnail,100,100);

        // Save the thumbnail
//        try {
//            File thumbnailFile = null; // TODO: create file into thumbnail folder
//            FileOutputStream fos = new FileOutputStream(thumbnailFile);
//            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, fos);
//            fos.flush();
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // Use the thumbnail on an ImageView or recycle it!
        return thumbnail;
    }
}
