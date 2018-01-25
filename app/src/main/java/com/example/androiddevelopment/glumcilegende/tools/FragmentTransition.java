package com.example.androiddevelopment.glumcilegende.tools;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.example.androiddevelopment.glumcilegende.R;

/**
 * Created by BBLOJB on 23.1.2018..
 */

public class FragmentTransition {
    public static void to(Fragment newFragment, FragmentActivity activity){
        to(newFragment, activity, true);
    }

    public static void to(Fragment newFragment, FragmentActivity activity, boolean addToBackstack){
        FragmentTransaction transaction = activity.getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.mainContent, newFragment);
        if (addToBackstack) transaction.addToBackStack(null);
        transaction.commit();
    }

}
