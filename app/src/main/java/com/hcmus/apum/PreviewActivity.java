package com.hcmus.apum;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import static com.hcmus.apum.MainActivity.mediaManager;

public class PreviewActivity extends AppCompatActivity {

    // GUI controls
    Toolbar toolbar;
    AppBarLayout appbar;
    // Elements
    ImageView imgPreview;
    //DB
    DatabaseFavorites db_fav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        // Init preview layout
        setContentView(R.layout.activity_preview);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
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
        toolbar = (Toolbar) findViewById(R.id.menu_preview);
        toolbar.inflateMenu(R.menu.menu_preview);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(imgFile.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
            Toast.makeText(this, "test", Toast.LENGTH_LONG).show();
        }
        Menu menu = toolbar.getMenu();
        MenuItem fav = menu.findItem(R.id.action_favorite);

        if(imgFile.exists()){
            if(mediaManager.checkFavorites(thumbnails,pos)) {
                fav.getIcon().setColorFilter(this.getColor(R.color.red), PorterDuff.Mode.SRC_IN);
            }else {
                fav.getIcon().setColorFilter(this.getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            }
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgPreview.setImageBitmap(myBitmap);
        } else {

        }
//        imgPreview.setImageResource(thumbnails.get(pos));


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
            case R.id.action_favorite:
                mediaManager.addFavorites(thumbnails, pos, db_fav);
                if(mediaManager.checkFavorites(thumbnails,pos)) {
                    fav.getIcon().setColorFilter(this.getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                }else {
                    fav.getIcon().setColorFilter(this.getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                }
//                fav_ic.setTint(Color.parseColor("#FF0000"));
                Toast.makeText(this, "oke", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_info:
                Toast.makeText(this, pos, Toast.LENGTH_LONG).show();
                //Toast.makeText(getContext(),img.toString(), Toast.LENGTH_LONG).show();
                break;
            case R.id.action_wallpaper:
                break;
        }
        return true;
    }

}
