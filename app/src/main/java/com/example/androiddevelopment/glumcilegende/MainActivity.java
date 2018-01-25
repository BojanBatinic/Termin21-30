package com.example.androiddevelopment.glumcilegende;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.androiddevelopment.glumcilegende.adapters.DrawerListAdapter;
import com.example.androiddevelopment.glumcilegende.dialogs.LocationDialog;
import com.example.androiddevelopment.glumcilegende.fragments.MapFragment;
import com.example.androiddevelopment.glumcilegende.model.NavItem;
import com.example.androiddevelopment.glumcilegende.tools.FragmentTransition;

import java.util.ArrayList;

/**
 * Created by BBLOJB on 23.1.2018..
 */

public class MainActivity extends AppCompatActivity{
    private DrawerLayout mDLayout;
    private ListView mDList;
    private ActionBarDrawerToggle mDToggle;
    private RelativeLayout mDPane;
    private CharSequence mDTitle;
    private CharSequence mTitle;
    private ArrayList<NavItem> mNavItems = new ArrayList<>();
    private AlertDialog dialog;

    private String synctime;
    private boolean allowSync;
    private String lookupRadius;

    private boolean allowReviewNotfit;
    private boolean allowCommentedNotif;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareMenu(mNavItems);

        mTitle = mDTitle = getTitle();
        mDLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDList = (ListView) findViewById(R.id.navList);

        //populate the Navigation Drawer with options
        mDPane = (RelativeLayout) findViewById(R.id.drawerPane);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);

        //set a custom shadow that overlays the main content when the drawer opens
        mDLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDList.setOnItemClickListener(new DrawerItemClickListener());
        mDList.setAdapter(adapter);

        //enable Action Bar app icon to behave as a action to toggle nav drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_launcher);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
            actionBar.setHomeButtonEnabled(true);
        }
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon

        // OVO NE MORA DA SE KORISTI, UKOLIKO SE NE KORISTI
        // ONDA SE NE MENJA TEKST PRILIKOM OPEN CLOSE DRAWERA POGLEDATI JOS
        mDToggle = new ActionBarDrawerToggle(
                this, //host Activity
                mDLayout, //DrawerLayout object
                toolbar, //nav drawer image to replace UP caret
                R.string.drawer_open, //open drawer description for accessibility
                R.string.drawer_close //close drawer desciption for accessibility
                ){
            public void onDrawerClosed(View view){
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();//creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView){
                getSupportActionBar().setTitle("iReviewer");
                invalidateOptionsMenu();//creates call to onPrepareOptionsMenu()
            }
        };
        if (savedInstanceState == null){
          selectItemFromDrawer(0);
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    private void consultPreferences(){
        synctime = sharedPreferences.getString(getString(R.string.pref_sync_list), "1"); //1min
        allowSync = sharedPreferences.getBoolean(getString(R.string.pref_sync), false);

        lookupRadius = sharedPreferences.getString(getString(R.string.pref_radius), "1");//1min

        allowCommentedNotif = sharedPreferences.getBoolean(getString(R.string.notif_on_my_comment_key), false);
        allowReviewNotfit = sharedPreferences.getBoolean(getString(R.string.notif_on_my_review_key), false);

    }
    private void showLocationDialog(){
        if (dialog == null){
            dialog = new LocationDialog(MainActivity.this).prepareDialog();
        }else {
            if (dialog.isShowing()){
                dialog.dismiss();
            }
        }
         dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        showLocationDialog();

        consultPreferences();
    }

    private void prepareMenu(ArrayList<NavItem> mNavItems){
        mNavItems.add(new NavItem(getString(R.string.home), getString(R.string.home_long), R.drawable.ic_action_map));
        mNavItems.add(new NavItem(getString(R.string.places), getString(R.string.places_long), R.drawable.ic_action_place));
        mNavItems.add(new NavItem(getString(R.string.preferences), getString(R.string.preferences_long), R.drawable.ic_action_settings));
        mNavItems.add(new NavItem(getString(R.string.about), getString(R.string.about_long), R.drawable.ic_action_about));
        mNavItems.add(new NavItem(getString(R.string.sync_data), getString(R.string.sync_data_long), R.drawable.ic_action_refresh));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
     /* The click listner for ListView in the navigation drawer */
     private class DrawerItemClickListener implements ListView.OnItemClickListener{
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id){
             selectItemFromDrawer(position);
         }
     }
    private void selectItemFromDrawer(int position){
         if(position == 0){
             FragmentTransition.to(MapFragment.newInstance(), this, false);
         }else if(position == 1){
             //..
         }else if(position == 2){
             //..
         }else if(position == 3){
             //..
         }else if(position == 4){
             //..
         }else if(position == 5){
             //...
         }else{
             Log.e("DRAWER", "Nesto van opsega!");
         }

         mDList.setItemChecked(position, true);
         if(position != 5) //za sve osim za sync
         {
             setTitle(mNavItems.get(position).getmTitle());
         }
         mDLayout.closeDrawer(mDPane);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
