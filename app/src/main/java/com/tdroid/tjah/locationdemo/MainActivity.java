package com.tdroid.tjah.locationdemo;


import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;

public class MainActivity extends AppCompatActivity {

    GoogleApiClient mGoogleApiClient;
    TextView mTextView;
    Button mButton;

    private final int REQUEST_RESOLVE_GOOGLE_CLIENT_ERROR=1;
    boolean mResolvingError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.textView);
        mButton = findViewById(R.id.button);
        mButton.setEnabled(false);
        setupGoogleApiClient();

    }

        GoogleApiClient.ConnectionCallbacks mConnectionCallbacks =
                new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        mButton.setEnabled(true);
                    }
                    @Override
                    public void onConnectionSuspended(int i) {}
                };

        GoogleApiClient.OnConnectionFailedListener
                mOnConnectionFailedListener = new
                GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult
                                                           connectionResult) {
                        if (mResolvingError) {
                            return;
                        } else if (connectionResult.hasResolution()) {
                            mResolvingError = true;
                            try {
                                connectionResult.startResolutionForResult(
                                        MainActivity.this, REQUEST_RESOLVE_GOOGLE_CLIENT_ERROR);
                            } catch (IntentSender.SendIntentException e) {
                                mGoogleApiClient.connect();
                            }
                        }
                    }
                };

        protected synchronized void setupGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(mConnectionCallbacks)
                    .addOnConnectionFailedListener(
                            mOnConnectionFailedListener)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }


    public void getLocation(View view) {
        try {
            Location lastLocation =
                    LocationServices.FusedLocationApi.
                            getLastLocation(
                                    mGoogleApiClient);
            if (lastLocation != null) {
                mTextView.setText(
                        DateFormat.getTimeInstance().format(
                                lastLocation.getTime()) + "\n" +
                                "Latitude="+lastLocation.getLatitude() +
                                "\n" + "Longitude=" +
                                lastLocation.getLongitude());
            } else {
                Toast.makeText(MainActivity.this, "null",
                        Toast.LENGTH_LONG).show();
            }
        }
        catch (SecurityException e) {e.printStackTrace();}
    }

    private void showGoogleAPIErrorDialog(int errorCode) {
        GoogleApiAvailability googleApiAvailability =
                GoogleApiAvailability.getInstance();
        Dialog errorDialog = googleApiAvailability.getErrorDialog(
                this, errorCode, REQUEST_RESOLVE_GOOGLE_CLIENT_ERROR);
        errorDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int
            resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_GOOGLE_CLIENT_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK &&
                    !mGoogleApiClient.isConnecting() &&
                    !mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        }
    }



}
