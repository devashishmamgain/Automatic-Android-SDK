package com.automatic.automaticsdksampleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.automatic.net.responses.ResultSet;
import com.automatic.net.responses.Trip;

import java.util.List;

/**
 * Created by duncancarroll on 4/3/15.
 */
public class TripAdapter extends BaseAdapter {

    Context context;
    List<Trip> trips;

    public TripAdapter(Context context, ResultSet<Trip> trips) {
        this.context = context;
        this.trips = trips.results;
    }

    @Override
    public int getCount() {
        return trips.size();
    }

    @Override
    public Object getItem(int position) {
        return trips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.item_trip, null);
            // configure view holder
            TripViewHolder viewHolder = new TripViewHolder();
            viewHolder.startDate = (TextView) rowView.findViewById(R.id.startDate);
            viewHolder.endDate = (TextView) rowView.findViewById(R.id.endDate);
            viewHolder.startName = (TextView) rowView.findViewById(R.id.startName);
            viewHolder.endName = (TextView) rowView.findViewById(R.id.endName);
            viewHolder.cost = (TextView) rowView.findViewById(R.id.cost);
            rowView.setTag(viewHolder);
        }

        // fill data
        TripViewHolder holder = (TripViewHolder) rowView.getTag();
        Trip trip = trips.get(position);
        holder.startDate.setText(trip.getStartTimeFormatted());
        holder.endDate.setText(trip.getEndTimeFormatted());
        holder.startName.setText(trip.getStartAddressDisplayName());
        holder.endName.setText(trip.getEndAddressDisplayName());
        holder.cost.setText("$" + trip.fuel_cost_usd);
        return rowView;
    }

    public void updateTrips(List<Trip> trips) {
        this.trips = trips;
        notifyDataSetChanged();
    }

    // do some array shuffling and add the new page
    public void addPage(List<Trip> newPage) {
        trips.addAll(newPage);
        notifyDataSetChanged();
    }

    private class TripViewHolder {
        TextView startName;
        TextView endName;
        TextView startDate;
        TextView endDate;
        TextView cost;
    }
}
