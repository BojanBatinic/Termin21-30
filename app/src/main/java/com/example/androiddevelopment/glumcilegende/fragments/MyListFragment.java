package com.example.androiddevelopment.glumcilegende.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.support.v4.widget.SimpleCursorAdapter;

import com.example.androiddevelopment.glumcilegende.R;
import com.example.androiddevelopment.glumcilegende.activities.MainActivity;
import com.example.androiddevelopment.glumcilegende.db.DbHelper;
import com.example.androiddevelopment.glumcilegende.provider.GlumacContract;
import com.example.androiddevelopment.glumcilegende.provider.model.Glumac;
import com.j256.ormlite.dao.CloseableIterator;

/**
 * Created by BBLOJB on 20.1.2018..
 */
/**
 * Kada zelimo automatsko osvezanja liste moramo koristii ListFragment
 * **/
public class MyListFragment extends ListFragment {

    private DbHelper dbHelper;
    private Cursor c;
    private MainActivity activity;

    //potrebno je dodati i jedan iterator koji ce automatski osvezavati prikaz
    private CloseableIterator<Glumac> iterator;

    //Container Activity must implement this interface
    public interface OnGlumacSelectedListener {
        void onGlumacSelected(int id);
    }

    OnGlumacSelectedListener listener;
    ListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((MainActivity)getActivity());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        /**
         * Na svaki klik, uzimamo kursor adaptera i trazimo _id elmenta
         * sa pozicije koji smo kliknuli.
         *
         * NAPOMENA: biblioteka zahteva postojanje _id elementa _id nije isto sto i id!
         * */
        Cursor cur = (Cursor) adapter.getItem(position);
        cur.moveToPosition(position);
        String cID = cur.getString(cur.getColumnIndexOrThrow("_id"));

        //Kada dobijemo kljuc, pretvorimo ga u Integer i izazivamo klik dogadjaja
        listener.onGlumacSelected(Integer.parseInt(cID));
    }

    @Override
    public void onResume() {
        super.onResume();
        c = activity.getContentResolver().query(GlumacContract.Glumac.contentUri, null, null, null, null);
        if (c != null){
            //iz kursora izvlacimo kolone baze koji nas zanimaju
            String[] from = new String[]{GlumacContract.Glumac.FILED_NAME_NAME, GlumacContract.Glumac.FILED_NAME_BIOGRAFIJA};

            //i smestamo u nas layout na poziciju gde zelimo, pozicije su opisane id-om elemenata
            int[] to = new int[]{R.id.name, R.id.biografija};

            //inicijalizujemo adapter da se sam osvezava
            adapter = new SimpleCursorAdapter(activity, R.layout.cinema_list, c, from, to, 0);

            //ListFrafgment vec u sebi ima listu sto znaci da je potrebno da samo dodamo adapter
            setListAdapter(adapter);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (c != null){
            c.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (OnGlumacSelectedListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnProductSelectedListener");
        }
    }
}
