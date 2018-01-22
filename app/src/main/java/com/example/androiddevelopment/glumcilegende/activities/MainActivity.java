package com.example.androiddevelopment.glumcilegende.activities;



import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.androiddevelopment.glumcilegende.R;
import com.example.androiddevelopment.glumcilegende.adapters.DrawerListAdapter;
import com.example.androiddevelopment.glumcilegende.db.DbHelper;
import com.example.androiddevelopment.glumcilegende.db.model.Glumac;
import com.example.androiddevelopment.glumcilegende.dialogs.AboutDialog;
import com.example.androiddevelopment.glumcilegende.fragments.DetailFragment;
import com.example.androiddevelopment.glumcilegende.fragments.ListFragment;
import com.example.androiddevelopment.glumcilegende.fragments.ListFragment.OnGlumacSelectedListener;
import com.example.androiddevelopment.glumcilegende.model.NavigationItem;
import com.example.androiddevelopment.glumcilegende.net.MyService;
import com.example.androiddevelopment.glumcilegende.net.model.Event;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Each activity extends Activity class or AppCompatActivity class
public class MainActivity extends AppCompatActivity implements OnGlumacSelectedListener {

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

    private int glumacId = 0; // selected item id

    private DbHelper databaseHelper;

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

        if (actionBar != null) {
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
        if (savedInstanceState == null) {
            // FragmentTransaction is a set of changes (e.g. adding, removing and replacing fragments) that you want to perform at the same time.
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ListFragment listFragment = new ListFragment();
            ft.add(R.id.displayList, listFragment, "List_Fragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            selectItemFromDrawer(0);
        }

        // If the device is in the landscape mode and the detail fragment is null create detail fragment
        if (findViewById(R.id.displayDetail) != null) {
            landscapeMode = true;
            getFragmentManager().popBackStack();

            DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.displayDetail);
            if (detailFragment == null) {
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
        glumacId = 0;
    }

    //da bi dodali podatak u bazu, potrebno je da napravimo objekat klase
    //koji reprezentuje tabelu i popunimo podacima
    private void addItem() {
       Glumac glumac = new Glumac();
       glumac.setmName("Zoran Radmilovic");
       glumac.setBiografija("Jedan jedini i neponovljivi Radovan III");
       glumac.setRating(5.0f);
       glumac.setImage("zoran.jpg");

       //pozovemo metodu create da bi upisali u bazu
        try{
            getDbHelper().getGlumacDao().create(glumac);

            refresh();

            Toast.makeText(this, "Glumac je dodat", Toast.LENGTH_SHORT).show();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_item_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                break;
            case R.id.action_add:
                addItem();
                break;
            case R.id.action_image:
                showRandomImage();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showRandomImage(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.band_layout);

        ImageView image = (ImageView) dialog.findViewById(R.id.band_image);

        Picasso.with(this).load("https://source.unsplash.com/random").into(image);

        Button close = (Button) dialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // refresh() prikazuje novi sadrzaj. Povucemo nov sadrzaj iz baze i popunimo listu
    private void refresh() {
       ListView listView = (ListView) findViewById(R.id.glumci);

        if (listView != null) {
            ArrayAdapter<Glumac> adapter = (ArrayAdapter<Glumac>) listView.getAdapter();

            if (adapter != null) {
                try {
                    adapter.clear();
                    List<Glumac> list = getDbHelper().getGlumacDao().queryForAll();
                    adapter.addAll(list);
                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // Overrides setTitle method to support older api levels
    @Override
    public void setTitle(CharSequence title) {
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
    public void onConfigurationChanged(Configuration newCconfig) {
        super.onConfigurationChanged(newCconfig);
        // Pass any configuration change to the drawer toggle
        drawerToggle.onConfigurationChanged(newCconfig);
    }

    // selectItemFromDrawer method is called when NavigationDrawer item is selected to update UI accordingly
    private void selectItemFromDrawer(int position) {
        if (position == 0) {
            // FirstActivity is already shown
        } else if (position == 1) {
            Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
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
    public void onGlumacSelected(int id) {

        glumacId = id;

        try {
            Glumac glumac = getDbHelper().getGlumacDao().queryForId(id);

            if (landscapeMode) {
                DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.displayDetail);
                detailFragment.updateGlumac(glumac);
            } else {
                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setGlumac(glumac);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.displayList, detailFragment, "Detail_Fragment2");
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack("Detail_Fragment2");
                ft.commit();
                listShown = false;
                detailShown = true;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (landscapeMode) {
            finish();
        } else if (listShown == true) {
            finish();
        } else if (detailShown == true) {
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

    //Metoda koja komunicira sa bazom podataka
    public DbHelper getDbHelper(){
        if (databaseHelper == null){
            databaseHelper = OpenHelperManager.getHelper(this, DbHelper.class);
        }
        return databaseHelper;
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();

        //nekon rada sa bazom podataka porebno je obavezno osloboditi resurse
        if(databaseHelper != null){
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //samo pozovemo metodu kada je potrebno da se ona izvrsi i ne moramo da vodimo vise racuna
        //o pozadinskom desavanju
        getArtistByName("Metallica");
    }
    /**
     * Poziv REST servisa se odvija u pozadini i mi ne moramo da vodimo racuna o tome
     * Samo je potrebno da registrujemo sta da se desi kada odgovor stigne od nas
     * Taj deo treba da implementiramo dodavajuci Callback<List<Event>> unutar enqueue metode
     *
     * Servis koji pozivamo izgleda:
     * http://rest.bandsintown.com/artists/Metallica/events?app_id=test
     *
     * gde je :
     *
     * http://rest.bandsintown.com/ osnova servisa
     * artists/Metallica/events poziv servisa koji ce vratiti informacije o grupi
     * ?app_id=test spisak dodatnih parametara ili upit nad servisom
     * */
    private void getArtistByName(String name){
        Map<String, String> query = new HashMap<>();
        query.put("app_id", "test");

        Call<List<Event>> call = MyService.apiInterface().getArtistByName(name, query);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                //obavezno proveriti da li je upit zavrsen uspesno 200 kod
                if (response.code() == 200){
                    List<Event> events = response.body();
                    //posto sliku ucitavamo sa interneta, moramo je u pozadini skinuti na nas urejdja
                    //da bi je prikazali. Da ne bi mi morali voditi racina o tome Picasso biblioteka
                    // nam moze pomoci u tome. Potrebno je da kazemo sa koje adrese se slika ucitava
                    // u koji imgeview objekat se ucitaca i u kojoj aktivnosti se posao odvija
                    //Picasso.with(MainActivity.this).load(putanja_do_slike)
                    // .into(imageView_u_koji_zelimo_da_smestimo_sliku);
                    String evs = "";
                    for (Event e : events){
                        evs += "" + e.getVenue().getName() + "\n";
                    }
                    Toast.makeText(MainActivity.this, evs, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                //u slucaju da je nesto poslo po zlu, ispisemo sta nije u redu tj sta je poruka greske
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
