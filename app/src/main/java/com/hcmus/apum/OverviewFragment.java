package com.hcmus.apum;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class OverviewFragment extends Fragment {

    // GUI controls
    AppBarLayout appbar;
    CollapsingToolbarLayout collapsingToolbar;
    Toolbar toolbar;
    NestedScrollView scroll;
    GridView grid;
    ThumbnailAdapter adapter;

    // Test values
    final String[] items = {"Ant","Baby","Clown", "Duck", "Elephant", "Family", "Good", "Happy", "Igloo",
            "Jumping", "King", "Love", "Mother", "Napkin", "Orange", "Pillow"};
    final int[] images = {R.drawable.ant, R.drawable.baby, R.drawable.clown, R.drawable.duck,
            R.drawable.elephant, R.drawable.family, R.drawable.good, R.drawable.happy,
            R.drawable.igloo, R.drawable.jumping, R.drawable.king, R.drawable.love,
            R.drawable.mother, R.drawable.napkin, R.drawable.orange, R.drawable.pillow};

    public OverviewFragment() {
        // Required empty public constructor
    }

    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container,false);

        // Init controls
        appbar = (AppBarLayout) view.findViewById(R.id.appbar);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // Change icon to black/white depending on scroll state
                if ((collapsingToolbar.getHeight() + verticalOffset) < (collapsingToolbar.getScrimVisibleHeightTrigger())) {
                    toolbar.getOverflowIcon().setColorFilter(getContext().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                } else {
                    toolbar.getOverflowIcon().setColorFilter(getContext().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);
                }
            }
        });
        collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbar);
        scroll = (NestedScrollView) view.findViewById(R.id.scroll);
        try {
            int scrollState = savedInstanceState.getInt("scrollState");
            scroll.setScrollY(scrollState);
        } catch (Exception e) {}
        adapter = new ThumbnailAdapter(getActivity(), images);
        grid = (GridView) view.findViewById(R.id.grid);
        grid.setEmptyView(view.findViewById(R.id.empty));
        grid.setAdapter(adapter);
        grid.setOnItemClickListener((adapterView, view1, i, l) -> showPreview(i));

        // Init actionbar buttons
        toolbar = (Toolbar) view.findViewById(R.id.menu_main);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_add:
                    Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        startActivityForResult(takePicIntent, 71);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(OverviewFragment.super.getContext(), getString(R.string.err_camera), Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.action_search:
                    break;
                case R.id.action_select:
                    break;
                case R.id.action_zoom:
                    break;
                case R.id.action_reload:
                    break;
                case R.id.action_trash:
                    break;
                case R.id.action_vault:
                    break;
                case R.id.action_settings:
                    break;
                case R.id.action_about:
                    break;
            }
            return true;
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        int scrollState = scroll.getScrollY();
        outState.putInt("scrollState", scrollState);
    }

    private void showPreview(int pos) {
        Intent mainPreview = new Intent(this.getContext(), PreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArray("items", items);
        bundle.putIntArray("thumbnails", images);
        bundle.putInt("position", pos);
        mainPreview.putExtras(bundle);
        startActivityForResult(mainPreview, 97);
//        finish();
    }
}