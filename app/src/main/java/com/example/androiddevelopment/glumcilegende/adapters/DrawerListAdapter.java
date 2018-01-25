package com.example.androiddevelopment.glumcilegende.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androiddevelopment.glumcilegende.R;
import com.example.androiddevelopment.glumcilegende.model.NavItem;

import java.util.ArrayList;

/**
 * Created by BBLOJB on 22.11.2017..
 */
// Custom adapter extends BaseAdapter and overrides its methods
public class DrawerListAdapter extends BaseAdapter {

    // A reference to the context (i.e. the activity containing the adapter)
    Context mContext;
    ArrayList<NavItem> mNavItems;

    // Constructor should at least have context as a parameter


    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
        mContext = context;
        mNavItems = navItems;
    }

    // Returns the item count
    @Override
    public int getCount() {
        return mNavItems.size();
    }

    // Returns an item
    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    // Returns an item ID
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_list_item, null);
        }
        else{
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);

        titleView.setText(mNavItems.get(position).getmTitle());
        subtitleView.setText(mNavItems.get(position).getmSubtitle());
        iconView.setImageResource(mNavItems.get(position).getmIcon());

        return view;
    }
}
