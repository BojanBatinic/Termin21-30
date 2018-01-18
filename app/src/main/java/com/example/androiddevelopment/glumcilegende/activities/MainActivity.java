package com.example.androiddevelopment.glumcilegende.activities;


import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.util.ArrayList;

import com.example.androiddevelopment.glumcilegende.R;
import com.example.androiddevelopment.glumcilegende.adapters.DrawerListAdapter;
import com.example.androiddevelopment.glumcilegende.async.SimpleReceiver;
import com.example.androiddevelopment.glumcilegende.async.SimpleService;
import com.example.androiddevelopment.glumcilegende.dialogs.AboutDialog;
import com.example.androiddevelopment.glumcilegende.fragments.DetailFragment;
import com.example.androiddevelopment.glumcilegende.fragments.ListFragment;
import com.example.androiddevelopment.glumcilegende.fragments.ListFragment.OnGlumacSelectedListener;
import com.example.androiddevelopment.glumcilegende.model.NavigationItem;
import com.example.androiddevelopment.glumcilegende.async.SimpleSyncTask;
import com.example.androiddevelopment.glumcilegende.tools.ReviewerTools;

// Each activity extends Activity class or AppCompatActivity class
public class MainActivity extends AppCompatActivity implements OnGlumacSelectedListener {

  //  boolean landscape = false;

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItemFromDrawer(position);

        }

    }

    // Attributes used by NavigationDrawer
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private RelativeLayout drawerPane;
    private CharSequence drawerTitle;
    private CharSequence title;

    private ArrayList<NavigationItem> navigationItems = new ArrayList<NavigationItem>();

    // Attributes used by Dialog
    private AlertDialog dialog;

    // Attributes representing the activity's state
    private boolean landscapeMode = false; // Is the device in the landscape mode?
    private boolean listShown = false; // Is the ListFragment fragment shown?
    private boolean detailShown = false; // Is the DetailFragment fragment shown?

    private int productId = 0; // selected item id

    private SimpleReceiver sync;
    private AlarmManager manager;
    private PendingIntent pendingIntent;

    // onCreate method is a lifecycle method called when he activity is starting
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Each lifecycle method should call the method it overrides
        super.onCreate(savedInstanceState);
        // setContentView method draws UI
        //draws activity`s layout
        setContentView(R.layout.main);

       //Manages NavigationDrawer

        //Populates a list of NavigationDrawer items
        navigationItems.add(new NavigationItem(getString(R.string.drawer_home), getString(R.string.drawer_home_long), R.drawable.ic_action_product));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_settings), getString(R.string.drawer_settings_long), R.drawable.ic_action_settings));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_about), getString(R.string.drawer_about_long), R.drawable.ic_action_about));

        title = drawerTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerList = (ListView) findViewById(R.id.navList);

        // Populates NavigtionDrawer with options
        drawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        DrawerListAdapter adapter = new DrawerListAdapter(this, navigationItems);

        // Sets a custom shadow that overlays the main content when NavigationDrawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerList.setAdapter(adapter);

        // Enable ActionBar app icon to behave as action to toggle nav drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }

        drawerToggle = new ActionBarDrawerToggle(
                this,          /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,              /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(title);
                invalidateOptionsMenu(); // Creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // Creates call to onPrepareOptionsMenu()
            }

        };

        // Manages fragments

        // If the activity is started for the first time create master fragment
        if(savedInstanceState == null) {
            // FragmentTransaction is a set of changes (e.g. adding, removing and replacing fragments) that you want to perform at the same time.
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ListFragment listFragment = new ListFragment();
            ft.add(R.id.displayList, listFragment, "List_Fragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            selectItemFromDrawer(0);
        }

        // If the device is in the landscape mode and the detail fragment is null create detail fragment
        if(findViewById(R.id.displayDetail) != null){
            landscapeMode = true;
            getFragmentManager().popBackStack();

            DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.displayDetail);
            if(detailFragment == null){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                detailFragment = new DetailFragment();
                ft.replace(R.id.displayDetail, detailFragment, "Detail_Fragment1");
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                detailShown = true;
            }
        }

        listShown = true;
        detailShown = false;
        productId = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_item_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }
    /**
     * Metoda koja je izmenjena da reflektuje rad sa Asinhronim zadacima
     */
    // onOptionsItemSelected method is called whenever an item in the Toolbar is selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_refresh:
                Toast.makeText(MainActivity.this, "Sinhronizacija pokrenuta u pozadini niti. dobro :)",Toast.LENGTH_SHORT).show();
                int status = ReviewerTools.getConStatus(getApplicationContext());
                Intent intent = new Intent(MainActivity.this, SimpleService.class);
                intent.putExtra("STATUS", status);
                startService(intent);
                break;
            case R.id.action_add:
                try {
                    Toast.makeText(MainActivity.this, "Sinhronizacija pokrenuta u glavnoj niti. Nije dobro :(", Toast.LENGTH_SHORT).show();
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Overrides setTitle method to support older api levels
    @Override
    public void setTitle(CharSequence title){
        getSupportActionBar().setTitle(title);
    }

    // Method(s) that manage NavigationDrawer.
    // onPostCreate method is called ofthen onRestoreInstanceState to synchronize toggle state
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    // onConfigurationChanged method is called when the device configuration changes to pass configuration change to the drawer toggle
    @Override
    public void onConfigurationChanged(Configuration newCconfig){
        super.onConfigurationChanged(newCconfig);
        // Pass any configuration change to the drawer toggle
        drawerToggle.onConfigurationChanged(newCconfig);
    }

    // selectItemFromDrawer method is called when NavigationDrawer item is selected to update UI accordingly
    private void selectItemFromDrawer(int position) {
        if (position == 0) {
            // FirstActivity is already shown
        } else if (position == 1) {
            Intent settings = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settings);
        } else if (position == 2) {
            if (dialog == null) {
                dialog = new AboutDialog(MainActivity.this).prepareDialog();
            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
            // To not create dialog class
            // call it from here.
            dialog.show();
        }

        drawerList.setItemChecked(position, true);
        setTitle(navigationItems.get(position).getTitle());
        drawerLayout.closeDrawer(drawerPane);

    }

    @Override
    public void onGlumacSelected(int id){

        productId = id;

        if(landscapeMode) {
            DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.displayDetail);
            detailFragment.updateGlumac(id);
        } else {
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setGlumac(id);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.displayList, detailFragment, "Detail_Fragment2");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack("Detail_Fragment2");
            ft.commit();
            listShown = false;
            detailShown = true;

        }
    }

    @Override
    public void onBackPressed(){
        if(landscapeMode){
            finish();
        }else if (listShown == true){
            finish();
        }else if (detailShown == true) {
            getFragmentManager().popBackStack();
            ListFragment listFragment = new ListFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.displayList, listFragment, "List_Fragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            listShown = true;
            detailShown = false;
        }
    }
    /**
     * Prilikom startovanja aplikacije potrebno je registrovati
     * elemente sa kojimaa radimo. Kada aplikacija nije aktivna
     * te elemente moramo da uklonimo.
     */
    @Override
    protected void onResume(){
        super.onResume();

        setUpReceiver();
        setUpManager();
    }
    /**
     * Registrujemo nas BroadcastReceiver i dodajemo mu 'filter'.
     * Filter koristimo prilikom prispeca poruka. Jedan receiver
     * moze da reaguje na vise tipova poruka. One nam kazu
     * sta tacno treba da se desi kada poruka odredjenog tipa (filera)
     * stigne.
     * */
    private void setUpReceiver(){
        sync = new SimpleReceiver();
        //registracija jednog filtera
        IntentFilter filter = new IntentFilter();
        filter.addAction("SYNC_DATA");
        registerReceiver(sync, filter);
    }
    /**
     * Kada zelimo da se odredjeni zadaci ponavljaju, potrebno je
     * da registrujemo manager koji ce motriti kada je vreme da se
     * taj posao obavi. Kada registruje vreme za pokretanje zadatka
     * on emituje Intent operativnom sistemu sta je potrebno da se
     * desi.
     * Takodje potrebno je da definisemo ponavljanja tj. na koliko
     * vremena zelimo da se posao ponovo obavi
     * */
    private void setUpManager(){
        //Intent koji ce manager emitovat operativnom sisitemu
        //Startujemo jedan servis
        Intent intent = new Intent(this, SimpleService.class);
        int status = ReviewerTools.getConStatus(getApplicationContext());
        intent.putExtra("STATUS", status);
        /**definisemo manager i kazemo kada je potrebno da se ponavlja

        parametri:
            context: this - u kom kontekstu zelimo da se intent izvrsava
            requestCode: 0 - nas jedinstev kod
            intent: intent koji zelimo da se izvrsi kada dodje vreme
            flags: 0 - flag koji opisuje sta da se radi sa intent-om kada se poziv desi
            detaljnije:https://developer.android.com/reference/android/app/PendingIntent.html#getService(android.content.Context, int, android.content.Intent, int)
        */
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        //koristicemo sistemski AlarmManager pa je potrebno da dobijemo
        //njegovu instancu.
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //definisemo kako ce alarm manager da reaguje.
        //prvi parametar kaze da ce reagovati u rezimu ponavljanja
        //drugi parametar od kada krece da meri vreme
        //treci parametar na koliko jedinica vremena ce ragovati (minimalno 1min)
        //poslednji parametar nam govori koju akciju treba da preduzmemo kada se alarm iskljuci
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                ReviewerTools.calTimeTillNextSync(1), pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }
    /**
     * Moramo voditi racuna o komponentama koje je potrebno osloboditi
     * kada aplikacija nije aktivna.
     * */
    @Override
    protected void onPause(){
        //ako je manager kreiran potrebno je da ga uklonimo
        if(manager != null){
            manager.cancel(pendingIntent);
            manager = null;
        }
        //osloboditi resurse koje koristi receiver
        if(sync != null){
            unregisterReceiver(sync);
            sync = null;
        }
        super.onPause();
    }

}
