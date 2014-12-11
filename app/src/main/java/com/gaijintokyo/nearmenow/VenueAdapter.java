package com.gaijintokyo.nearmenow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by 22707561 on 12/11/2014.
 */
public class VenueAdapter extends ArrayAdapter<Venue> {
    private LayoutInflater mInflater;
    private ArrayList<Venue> mVenueList;

    public VenueAdapter (Context context, ArrayList<Venue> venues) {
        super(context, 0, venues);
        mInflater = LayoutInflater.from(context);
        mVenueList = venues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //if (convertView == null) {
            View rowView = mInflater.inflate(R.layout.venue_layout, null);

            Venue venue = mVenueList.get(position);
            TextView tvName = (TextView)rowView.findViewById(R.id.tvName);
            TextView tvRating = (TextView)rowView.findViewById(R.id.tvRating);
            TextView tvCheckins = (TextView)rowView.findViewById(R.id.tvCheckins);
            tvName.setText(venue.mName);
            tvRating.setText(venue.mRating);
            tvCheckins.setText(venue.mCheckins);
        //}
        return rowView;
    }
}
