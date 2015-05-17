package com.automatic.automaticsdksampleapp;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.automatic.android.sdk.Automatic;
import com.automatic.net.responses.ResultSet;
import com.automatic.net.responses.Trip;
import com.automatic.net.responses.User;
import com.automatic.net.responses.Vehicle;

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
        Automatic.restApi().getUser(new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                Log.d("AutomaticRestApi", "getUser() Success!");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("AutomaticRestApi", "getUser() Failed!");
            }
        });

        Automatic.restApi().getMyVehicles(new Callback<ResultSet<Vehicle>>() {
            @Override
            public void success(ResultSet<Vehicle> vehicles, Response response) {
                Log.d("AutomaticRestApi", "getVehicles() Success!");
                Automatic.restApi().getVehicle(vehicles.results.get(0).id, new Callback<Vehicle>() {
                    @Override
                    public void success(Vehicle vehicle, Response response) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_invalidate_token) {
            // inject a bad token
            if (BuildConfig.DEBUG) {
                //Automatic.SET_BAD_TOKEN = "bad_token";
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        getTrips();
    }

    private void getTrips() {
        // TODO need to write some convenience methods
        Automatic.restApi().getTrips(null,null,null,null,null,null,null,50,new Callback<ResultSet<Trip>>() {
            @Override
            public void success(ResultSet<Trip> trips, Response response) {
                Log.d(TAG, "Got " + trips.results.size() + " trips!");

                // update or replace trips (TODO: add pagination)
                if (tripAdapter == null) {
                    tripAdapter = new TripAdapter(TripViewerActivity.this, trips);
                    listView.setAdapter(tripAdapter);
                } else {
                    tripAdapter.updateTrips(trips.results);
                }
                swipeRefreshLayout.setRefreshing(false);

                // now try to get the first trip
                Automatic.restApi().getTrip(trips.results.get(0).id, new Callback<Trip>() {
                    @Override
                    public void success(Trip trip, Response response) {
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
