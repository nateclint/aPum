package com.hcmus.apum;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

import static com.hcmus.apum.MainActivity.mediaManager;

public class MediaManager {
    private ArrayList<String> images, albums, faces, favorites;
    private ArrayList<Integer> albumCounts;
    private ArrayList<String>
            extImg = new ArrayList<>(
                    Arrays.asList("gif", "png", "bmp", "jpg", "svg", "raw", "jpeg", "webp")
            ), extVid = new ArrayList<>(
                    Arrays.asList("mp4", "mov", "mkv", "wmv", "avi", "flv", "webm")
            );
    DatabaseFavorites db;

    public void updateLocations(Context context) {
        ArrayList<String> images = new ArrayList<>(),
                albums = new ArrayList<>();
        ArrayList<Integer> albumCounts = new ArrayList<>();
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            String imagePath = cursor.getString(column_index_data),
                    albumPath = "";
            String[] folders = imagePath.split("/");
            for (int i = 0; i < folders.length - 1; ++i) {
                albumPath += folders[i];
                if (i < folders.length - 2) {
                    albumPath += "/";
                }
            }

            images.add(imagePath);
            if (!albums.contains(albumPath)) {
                albums.add(albumPath);
            }
        }

        for (String a: albums) {
            Integer count = 0;
            for (String i: images) {
                if (i.contains(a)) {
                    count++;
                }
            }
            albumCounts.add(count);
        }

        this.images = images;
        this.albums = albums;
        this.albumCounts = albumCounts;
    }

    public void updateFavoriteLocations(Context context) {
        ArrayList<String> listFavorites = new ArrayList<>();
        //listFavorites = db.getAllFavorite();
        favorites = listFavorites;
    }

    public void addFavorites(ArrayList<String> thumbs, int pos, DatabaseFavorites db){
        if(!favorites.contains(thumbs.get(pos))){
            favorites.add(thumbs.get(pos));
            db.addData(thumbs.get(pos));
        }else{
            favorites.remove(thumbs.get(pos));
            db.removeData(thumbs.get(pos));
        }
        //System.out.println("TEST 123 " + db.addData(thumbs.get(pos)));
        //db.addData(favorites.get(favorites.size()-1));
        //db.addData(i.get(pos));
    }

    public boolean checkFavorites(ArrayList<String> thumbs, int pos){
        boolean check = thumbs.contains(thumbs.get(pos));
        return check;
    }

    public ArrayList<String> getImages() { return images; }
    public ArrayList<String> getAlbums() { return  albums; }
    public ArrayList<String> getFavorites() { return favorites; }
    public ArrayList<Integer> getAlbumCounts() { return albumCounts; }

    public FilenameFilter getFileFilter(String type) {
        FilenameFilter filter;
        switch (type) {
            case "img":
                filter = (dir, file) -> {
                    for (final String ext : extImg) {
                        if (file.endsWith("." + ext)) {
                            return true;
                        }
                    }
                    return false;
                };
                break;
            case "vid":
                filter = (dir, file) -> {
                    for (final String ext : extVid) {
                        if (file.endsWith("." + ext)) {
                            return true;
                        }
                    }
                    return false;
                };
                break;
            case "visual":
                filter = (dir, file) -> {
                    ArrayList<String> extVisual = extImg;
                    extVisual.addAll(extVid);
                    for (final String ext : extVisual) {
                        if (file.endsWith("." + ext)) {
                            return true;
                        }
                    }
                    return false;
                };
            default:
                filter = (dir, file) -> {return true;};
                break;
        }
        return filter;
    }

    public File getLastModified(File[] list) {
        long modified = Long.MIN_VALUE;
        File file = null;
        for (File f : list) {
            if (f.lastModified() > modified) {
                file = f;
                modified = f.lastModified();
            }
        }
        return file;
    }

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

        // Use the thumbnail on an ImageView or recycle it!
        return thumbnail;
    }

    public ArrayList<String> search(String query, String scope) {
        ArrayList<String> results = new ArrayList<>(),
            scopedList;
        switch (scope) {
            case "overview":
                scopedList = images;
                break;
            case "albums":
                scopedList = albums;
                break;
            case "faces":
                scopedList = faces;
                break;
            case "favorite":
                scopedList = favorites;
                break;
            default:
                return results;
        }
        query = query.toLowerCase();
        for (String i: scopedList) {
            String i_lower = i.toLowerCase();
            if (i_lower.substring(i.lastIndexOf("/") + 1).contains(query)) {
                results.add(i);
            }
        }

        return results;
    }
}
