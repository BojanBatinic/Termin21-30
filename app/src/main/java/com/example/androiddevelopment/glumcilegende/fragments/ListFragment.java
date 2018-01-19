package com.example.androiddevelopment.glumcilegende.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.androiddevelopment.glumcilegende.R;
import com.example.androiddevelopment.glumcilegende.db.DbHelper;
import com.example.androiddevelopment.glumcilegende.db.model.Glumac;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.List;


/**
 * Created by BBLOJB on 21.11.2017..
 */
// Each Fragment extends Fragment class
public class ListFragment extends Fragment {

    private DbHelper databaseHelper;

    // Container Activity must implement this interface
    public interface OnGlumacSelectedListener {
        void onGlumacSelected(int id);
    }
    OnGlumacSelectedListener listener;
    ListAdapter adapter;

    // onCreate method is a life-cycle method that is called when creating the fragment.
   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // onActivityCreated method is a life-cycle method that is called when the fragment's activity has been created and this fragment's view hierarchy instantiated.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Shows a toast message (a pop-up message)
        //Toast.makeText(getActivity(), "MasterFragemnt.onActivityCreated()", Toast.LENGTH_SHORT).show();

        try {
            List<Glumac> list = getDbHelper().getGlumacDao().queryForAll();

            adapter = new ArrayAdapter<Glumac>(getActivity(), R.layout.list_item, list);
            final ListView listView = (ListView) getActivity().findViewById(R.id.glumci);

            //assigns ArrayAdapter to ListView
            listView.setAdapter(adapter);

            //updates ListFragment
            listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Posto radimo sa bazom podataka, svaki element ima jedinstven id
                    // pa je potrebno da vidimo na koji tacno element smo kliknuli.
                    // To mozemo uraditi tako sto izvucemo proizvod iz liste i dobijemo njegov id
                    Glumac g = (Glumac) listView.getItemAtPosition(position);
                    listener.onGlumacSelected(g.getmId());
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // onCreateView method is a life-cycle method that is called to have the fragment instantiate its user interface view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //shows a toast message ( a pop-up message)
       // Toast.makeText(getActivity(), "ListFragment.onCreateView()", Toast.LENGTH_SHORT).show();
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        return view;
    }

    // onAttach method is a life-cycle method that is called when a fragment is first attached to its context.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //shows a toast messsage (a pop-up message)
       // Toast.makeText(getActivity(), "ListFragment.onAttach()", Toast.LENGTH_SHORT).show();
        try {
            listener = (OnGlumacSelectedListener) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnItemSelectedListener");
        }
    }

    public DbHelper getDbHelper(){
       if (databaseHelper == null){
           databaseHelper = OpenHelperManager.getHelper(getActivity(), DbHelper.class);
           }
           return databaseHelper;
    }

  }
