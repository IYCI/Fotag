package com.YC2010.Fotag;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {
    Model mModel;
    ArrayList<MenuItem> mStars = new ArrayList<>();
    RecyclerView mRecyclerView;
    CourseAdapter mAdapter;
    FloatingActionButton mFab;

    private static final String UUID_KEY = "UUID_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup model
        mModel = Model.getInstance();
        mModel.setContext(getApplicationContext());
        mModel.addObserver(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        if (mFab != null) {
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Model.getInstance().getFirstLoaded()) {
                        mModel.setPreparedImages();
                        mAdapter.updateListWithNotify();
                        Snackbar.make(view, "Images Loaded", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                    else {
                        Snackbar.make(view, "Images Loaded Already", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                }
            });
        }

        // Setup RecyclerView:
        mRecyclerView = (RecyclerView) findViewById(R.id.courses_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // set up layout manager
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        else {
            StaggeredGridLayoutManager gridLayoutManager =
                    new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }

        // specify an adapter (see also next example)
        mAdapter = new CourseAdapter(mModel.getImages());
//        mAdapter.setHasStableIds(true);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.d("MainActivity", "Data set changed");
            }
        });
        mAdapter.setOnItemClickListener(new CourseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ImageFragment mImageFragment = new ImageFragment();
                mImageFragment.setSharedElementEnterTransition(TransitionInflater.from(getApplicationContext()).
                        inflateTransition(R.transition.change_image_transform));
                mImageFragment.setEnterTransition(TransitionInflater.from(getApplicationContext()).
                        inflateTransition(android.R.transition.fade));

                // shared element
                ImageView mImage = (ImageView) findViewById(R.id.cardview_image);

                Bundle args = new Bundle();
                args.putSerializable(UUID_KEY, mAdapter.getmImages().get(position).getmUUID());
                mImageFragment.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, mImageFragment)
                        .addToBackStack("1")
                        .addSharedElement(mImage, "transition " + position)
                        .commit();
            }
        });
        mAdapter.setOnRatingBarChangeListener(new CourseAdapter.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(int rating, int position) {
                mAdapter.changeRating(rating, position);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // set up stars
        mStars.add(0, menu.findItem(R.id.action_star_1));
        mStars.add(1, menu.findItem(R.id.action_star_2));
        mStars.add(2, menu.findItem(R.id.action_star_3));
        mStars.add(3, menu.findItem(R.id.action_star_4));
        mStars.add(4, menu.findItem(R.id.action_star_5));

        // set up searchView
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menu != null) {
                    setOtherItemVisibility(menu, false);
                    mFab.setVisibility(View.INVISIBLE);
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("MainActivity", "Query Submitted: " + query);
                MenuItemCompat.collapseActionView(searchItem);
                setOtherItemVisibility(menu, true);
                mFab.setVisibility(View.VISIBLE);
                mModel.addImageFromURI(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if (!queryTextFocused) {
                    MenuItemCompat.collapseActionView(searchItem);
                    setOtherItemVisibility(menu, true);
                    mFab.setVisibility(View.VISIBLE);
                }
            }
        });
        update(null, null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_clear) {
            mModel.clearImageCollection();
            Snackbar.make(findViewById(R.id.coordinatorLayout), "All Images Removed", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
        else {
            if (id == R.id.action_star_1) {
                mModel.setRating(1);
            } else if (id == R.id.action_star_2) {
                mModel.setRating(2);
            } else if (id == R.id.action_star_3) {
                mModel.setRating(3);
            } else if (id == R.id.action_star_4) {
                mModel.setRating(4);
            } else if (id == R.id.action_star_5) {
                mModel.setRating(5);
            }
            mAdapter.updateListWithNotify();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable observable, Object data) {
        // update stars
        int filter = mModel.getRating();
        int i = 0;
        for (; i < filter; i++){
            if (mStars.get(i) != null) {
                mStars.get(i).setIcon(getDrawable(R.drawable.ic_star_white_24dp));
            }
        }
        for (; i < 5; i ++){
            if (mStars.get(i) != null) {
                mStars.get(i).setIcon(getDrawable(R.drawable.ic_star_border_white_24dp));
            }
        }

        // update mAdapter
        mAdapter.updateListWithNotify();
    }

    @Override
    public void onBackPressed() {
        // pop title and fragment stack
        Log.d("MainActivity", "stack count is " + getFragmentManager().getBackStackEntryCount());
        if (getFragmentManager().getBackStackEntryCount() >= 1) {
            getFragmentManager().popBackStackImmediate();
        }
        else {
            super.onBackPressed();
        }
    }

    private void setOtherItemVisibility(Menu menu, Boolean visibility){
        menu.findItem(R.id.action_clear).setVisible(visibility);
        menu.findItem(R.id.action_star_1).setVisible(visibility);
        menu.findItem(R.id.action_star_2).setVisible(visibility);
        menu.findItem(R.id.action_star_3).setVisible(visibility);
        menu.findItem(R.id.action_star_4).setVisible(visibility);
        menu.findItem(R.id.action_star_5).setVisible(visibility);
    }
}
