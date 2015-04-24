package com.automatic.automaticsdksampleapp;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.automatic.android.sdk.Automatic;
import com.automatic.net.ResponsesPublic;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TripViewerActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {
    final String TAG = TripViewerActivity.this.getClass().getSimpleName();
    ListView listView;
    TripAdapter tripAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_viewer);
        listView = (ListView) findViewById(R.id.trip_list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        getTrips();
        testOtherCalls();
    }

    private void testOtherCalls() {
        Automatic.restApi().getUser(new Callback<ResponsesPublic.User>() {
            @Override
            public void success(ResponsesPublic.User user, Response response) {
                Log.d("AutomaticRestApi", "getUser() Success!");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("AutomaticRestApi", "getUser() Failed!");
            }
        });

        Automatic.restApi().getVehicles(new Callback<List<ResponsesPublic.Vehicle>>() {
            @Override
            public void success(List<ResponsesPublic.Vehicle> vehicles, Response response) {
                Log.d("AutomaticRestApi", "getVehicles() Success!");
                Automatic.restApi().getVehicle(vehicles.get(0).id, new Callback<ResponsesPublic.Vehicle>() {
                    @Override
                    public void success(ResponsesPublic.Vehicle vehicle, Response response) {
                        Log.d("AutomaticRestApi", "getVehicle() Success!");
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.e("AutomaticRestApi", "getVehicle() Failed!");
                    }
                });
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("AutomaticRestApi", "getVehicles() Failed!");
            }
        });



    }

    @Override
    public void onRefresh() {
        getTrips();
    }

    private void getTrips() {

        Automatic.restApi().getTrips(new Callback<List<ResponsesPublic.Trip>>() {
            @Override
            public void success(List<ResponsesPublic.Trip> trips, Response response) {
                Log.d(TAG, "Got " + trips.size() + " trips!");

                // update or replace trips (TODO: add pagination)
                if (tripAdapter == null) {
                    tripAdapter = new TripAdapter(TripViewerActivity.this, trips);
                    listView.setAdapter(tripAdapter);
                } else {
                    tripAdapter.updateTrips(trips);
                }
                swipeRefreshLayout.setRefreshing(false);

                // now try to get the first trip
                Automatic.restApi().getTrip(trips.get(0).id, new Callback<ResponsesPublic.Trip>() {
                    @Override
                    public void success(ResponsesPublic.Trip trip, Response response) {
                        Log.d("AutomaticRestApi", "getTrip() Success!");
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.e("AutomaticRestApi", "getTrip() Failed!");
                    }
                });

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                String status = "unknown";
                if (retrofitError != null && retrofitError.getResponse() != null) {
                    status = Integer.toString(retrofitError.getResponse().getStatus());
                    Log.e(TAG, "Couldn't get trips with status " + status + " and raw json: " + retrofitError.getMessage());
                }

                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(TripViewerActivity.this, "Error fetching trips: " + status, Toast.LENGTH_LONG).show();
            }
        });

    }
}
