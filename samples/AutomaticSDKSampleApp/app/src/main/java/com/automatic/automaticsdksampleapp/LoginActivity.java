package com.automatic.automaticsdksampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.automatic.android.sdk.Automatic;
import com.automatic.android.sdk.AutomaticLoginCallbacks;
import com.automatic.android.sdk.LoginButton;
import com.automatic.android.sdk.Scope;

import retrofit.RestAdapter;
import retrofit.RetrofitError;


public class LoginActivity extends ActionBarActivity {

    public static final Scope[] scopes = {Scope.Public, Scope.VehicleVin, Scope.Trips, Scope.Location, Scope.VehicleEvents, Scope.VehicleProfile, Scope.UserProfile};
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (LoginButton) findViewById(R.id.automatic_login);

        // test callbacks
        loginButton.setOnLoginResultCallback(new AutomaticLoginCallbacks() {
            @Override
            public void onLoginSuccess() {
                Log.d("AutomaticLoginCallbacks", "Got login success callback!");
            }

            @Override
            public void onLoginFailure(RetrofitError retrofitError) {
                Toast.makeText(LoginActivity.this, "Error.", Toast.LENGTH_LONG).show();
                Log.d("AutomaticLoginCallbacks", "Got login failure callback!");
            }
        });

        Automatic.initialize(
                new Automatic.Builder(this)
                        .addScopes(scopes)
                        .logLevel(RestAdapter.LogLevel.FULL)
                        .useLoginButton(loginButton, this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Automatic.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }else if (id == R.id.action_trips) {
            if (Automatic.isLoggedIn()) {
                startActivity(new Intent(this, TripViewerActivity.class));
                finish();
            }else {
                Toast.makeText(this, "Must log in first.", Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Automatic.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                startActivity(new Intent(this, TripViewerActivity.class));
                //finish();
            }
        }
    }

}
