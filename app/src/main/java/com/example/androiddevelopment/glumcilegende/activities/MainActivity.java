package com.example.androiddevelopment.glumcilegende.activities;



import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.androiddevelopment.glumcilegende.R;
import com.example.androiddevelopment.glumcilegende.adapters.DrawerListAdapter;
import com.example.androiddevelopment.glumcilegende.db.DbHelper;
import com.example.androiddevelopment.glumcilegende.dialogs.AboutDialog;
import com.example.androiddevelopment.glumcilegende.fragments.DetailFragment;
import com.example.androiddevelopment.glumcilegende.fragments.MyListFragment;
import com.example.androiddevelopment.glumcilegende.fragments.MyListFragment.OnGlumacSelectedListener;
import com.example.androiddevelopment.glumcilegende.model.NavigationItem;
import com.example.androiddevelopment.glumcilegende.provider.GlumacContract;
import com.example.androiddevelopment.glumcilegende.provider.model.Glumac;
import com.j256.ormlite.android.apptools.OpenHelperManager;

// Each activity extends Activity class or AppCompatActivity class
public class MainActivity extends AppCompatActivity implements OnGlumacSelectedListener {

    private static final int SELECT_PICTURE = 1;

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
            MyListFragment myListFragment = new MyListFragment();
            ft.add(R.id.displayList, myListFragment, "List_Fragment");
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

    /**Kada koristimo  Content provider sve sto zelimo da radimo nad bazom
     * Moramo da navedemo kroz odredjenu URI putanju
     *
     * Ako zelimo da unesemo novi product moramo koristiti insert metodu prethodno
     * definisanog URI-a
     * content://AUTHORITY/CONTENT_URI_PATH/ odnosno u nasem slucaju
     *
     * content:com.example.androiddevelopment.glumcilegende.provider.model.Glumac
     * I moramo formirati ContentValues objekat kljuc-vrednost sta zelimo i u koju kolonu da unesemo
     */
    private void addItemWP() {
       //insert test
        ContentValues values = new ContentValues();
        values.clear();
        values.put(GlumacContract.Glumac.FILED_NAME_NAME, "Zoran");
        values.put(GlumacContract.Glumac.FILED_NAME_BIOGRAFIJA, "Jedan jedini i neponovljivi Radovan III...");
        values.put(GlumacContract.Glumac.FILED_NAME_RATING, 5.0f);
        values.put(GlumacContract.Glumac.FILED_NAME_IMAGE, "zoran.jpg");
        getContentResolver().insert(GlumacContract.Glumac.contentUri, values);

        Toast.makeText(this, "Inerted", Toast.LENGTH_SHORT).show();
        /**
         * Jednostavan test kako proci kroz celu tabelu glumci koja se nalazi na URI:
         *
         *  content://com.example.androiddevelopment.glumcilegende.provider.model.Glumac
         *
         *  Prvi parametar kursora je projekcija tj sta zelimo da selektujemo iz tabele
         *  Drugi parametar je selekcioni naziv praktcno WHERE u klasicnom SQL-u tj po kojim kolonama zelimo da filtriramo
         *  Treci paramerar su vrednosti tih selekcionih argumenta
         *  Peti je po cemu zelimo da sortiramo Cursor
         */
        Cursor c = getContentResolver().query(GlumacContract.Glumac.contentUri, null, null, null, null);
        if (c != null){
            while (c.moveToNext()){
                for (int i = 0; i < c.getColumnCount(); i++){
                    Log.i("REZ", c.getColumnName(i) + " : " + c.getString(i));
                }
            }
            //obavezno zatavarmo kursor!!!
            c.close();
        }
        finish();
        startActivity(getIntent());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_item_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }
    /**
     * Da bi dobili pristup Galeriji slika na uredjaju
     * moramo preko URI-ja pristupiti delu baze gde su smestene
     * slike uredjaja. Njima mozemo pristupiti koristeci sistemski
     * ContentProvider i koristeci URI images/* putanju
     *
     * Posto biramo sliku potrebno je da pozovemo aktivnost koja iscekuje rezultat
     * Kada dobijemo rezultat nazad prikazemo sliku i dobijemo njenu tacnu putanju
     * */
    private void selectPicture(){
       Intent intent = new Intent();
       intent.setType("image/*");
       intent.setAction(Intent.ACTION_GET_CONTENT);
       startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }
    /**
     * Sistemska metoda koja se automatksi poziva ako se
     * aktivnost startuje u startActivityForResult rezimu
     * Ako je ti slucaj i ako je sve proslo ok, mozemo da izvucemo
     * sadrzaj i to da prikazemo. Rezultat NIJE slika nego URI do te slike.
     * Na osnovu toga mozemo dobiti tacnu putnaju do slike ali i samu sliku
     * */
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            if (requestCode == SELECT_PICTURE){
                Uri selectedIU = data.getData();

                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.image_dialog);
                dialog.setTitle("Image dialog");

                ImageView image = (ImageView) dialog.findViewById(R.id.image);

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedIU);
                    image.setImageBitmap(bitmap);
                    Toast.makeText(this, selectedIU.getPath(), Toast.LENGTH_SHORT).show();

                    dialog.show();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                break;
            case R.id.action_add:
                addItemWP();
                break;
            case R.id.action_photo:
                selectPicture();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // refresh() prikazuje novi sadrzaj. Povucemo nov sadrzaj iz baze i popunimo listu
    private void refresh() {
     /*   ListView listView = (ListView) findViewById(R.id.glumci);

        if (listView != null) {
            ArrayAdapter<Glumac> adapter = (ArrayAdapter<Glumac>) listView.getAdapter();

            if (listView != null) {
                try {
                    adapter.clear();
                    List<Glumac> list = getDbHelper().getGlumacDao().queryForAll();
                    adapter.addAll(list);
                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }*/
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
            MyListFragment myListFragment = new MyListFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.displayList, myListFragment, "List_Fragment");
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
}
