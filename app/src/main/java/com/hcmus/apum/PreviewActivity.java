package com.hcmus.apum;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.hcmus.apum.MainActivity.PREVIEW_REQUEST_CODE;
import static com.hcmus.apum.MainActivity.mediaManager;

public class PreviewActivity extends AppCompatActivity {

    // GUI controls
    Toolbar toolbar;
    BottomNavigationView bottomToolbar;

    // Elements
    ImageView imgPreview;

    //DB
    DatabaseFavorites db_fav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        // Init preview layout
        imgPreview = findViewById(R.id.img_preview);

        //Database
        db_fav = new DatabaseFavorites(this);
        try {
            db_fav.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            db_fav.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        // Get values from bundle
        Intent mainPreview = getIntent();
        Bundle bundle = mainPreview.getExtras();
        String[] items = bundle.getStringArray("items");
        ArrayList<String> thumbnails = bundle.getStringArrayList("thumbnails");
        int pos = bundle.getInt("position");
        File imgFile = new File(thumbnails.get(pos));

        // Init actionbar buttons
        toolbar = findViewById(R.id.menu_preview);
        toolbar.inflateMenu(R.menu.menu_preview);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(imgFile.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        bottomToolbar = findViewById(R.id.bottomBar_preview);
        bottomToolbar.setOnNavigationItemSelectedListener(item -> bottomToolbarAction((String) item.getTitle()));

        // Apply data
        Menu menu = bottomToolbar.getMenu();
        MenuItem fav = menu.findItem(R.id.action_favorite);
        if (imgFile.exists()) {
            if (mediaManager.isFavorite(thumbnails.get(pos))) {
                fav.setIcon(R.drawable.ic_fav);
            } else {
                fav.setIcon(R.drawable.ic_fav_outline);
            }
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgPreview.setImageBitmap(myBitmap);
        }

        // Set values to return
        Intent previewMain = new Intent();
        Bundle returnBundle = new Bundle();
        returnBundle.putString("caller", bundle.getString("caller"));
        previewMain.putExtras(returnBundle);
        setResult(Activity.RESULT_OK, previewMain);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Intent mainPreview = getIntent();
        Bundle bundle = mainPreview.getExtras();
        String[] items = bundle.getStringArray("items");
        ArrayList<String> thumbnails = bundle.getStringArrayList("thumbnails");
        int pos = bundle.getInt("position");
        File imgFile = new File(thumbnails.get(pos));
        Menu menu = toolbar.getMenu();
        MenuItem fav = menu.findItem(R.id.action_favorite);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgPreview.setImageBitmap(myBitmap);
        } else {

        }
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            // Top toolbar
            case R.id.action_info:
                Toast.makeText(this, pos, Toast.LENGTH_LONG).show();
                //Toast.makeText(getContext(),img.toString(), Toast.LENGTH_LONG).show();
                break;
            case R.id.action_wallpaper:
                break;

            // Bottom toolbar
            case R.id.action_favorite:
                mediaManager.addFavorites(thumbnails, pos, db_fav);
                if(mediaManager.isFavorite(thumbnails.get(pos))) {
                    fav.setIcon(R.drawable.ic_fav);
                    Toast.makeText(this, "Added to Favorite", Toast.LENGTH_LONG).show();
                } else {
                    fav.setIcon(R.drawable.ic_fav_outline);
                    Toast.makeText(this, "Removed from Favorite", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.action_edit:
                break;
            case R.id.action_share:
                break;
            case R.id.action_delete:
                deleteImg(thumbnails.get(pos));
                break;
            default:
                Toast.makeText(this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private boolean bottomToolbarAction(String title) {
        if (title.equals(getResources().getString(R.string.fragment_favorite))) {

        } else if (title.equals(getResources().getString(R.string.action_edit))) {

        } else if (title.equals(getResources().getString(R.string.action_share))) {

        } else if (title.equals(getResources().getString(R.string.action_delete))) {

        } else {
            Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void deleteImg(String path_img){
        File f_del = new File(path_img);
        if(f_del.exists()){
            if(f_del.delete()){
                Log.e("Delete", "file Deleted :" + path_img);
                callBroadCast();
            }else{
                Log.e("Delete", "file not Deleted :" + path_img);
            }
        }
    }

    public void callBroadCast() {
        MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.e("ExternalStorage", "Scanned " + path + ":");
                Log.e("ExternalStorage", "-> uri=" + uri);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity(PREVIEW_REQUEST_CODE);
    }
}