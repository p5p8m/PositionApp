package com.tests.pascal.positionapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Formatter;
import java.util.List;
import java.lang.Object;
import java.util.Comparator;

import android.content.Context;
import android.app.Activity;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;


public class PositionActivity extends AppCompatActivity {


    // Make strings for logging
    private final String TAG = this.getClass().getSimpleName();

    // The string "fortytwo" is used as an example of state
    private final String state = "fortytwo";
    private final String gps = "gps";
    private final String ANSWER = "answer";

    private LocationManager lm;
    private boolean locationManagerInitialized = false;
    private LocationListener locListener;
    public TextView valLatitude;
    public TextView valLongitude;
    public TextView valAltitude;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_position);
        String answer = null;
        valLatitude = (TextView) findViewById(R.id.valLatitude);
        valLongitude = (TextView) findViewById(R.id.valLongitude);
        valAltitude = (TextView) findViewById(R.id.valAltitude);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = null;
        try {
            loc = lm.getLastKnownLocation(gps);
            locationManagerInitialized = true;
        }
        catch ( SecurityException ex) {
            Log.i(TAG, getMethodName() + "No permission to use GPS:" + ex.getMessage());
        }
        updateLocationDisplay(loc);
        // savedState could be null
        if (null != savedState) {
            answer = savedState.getString(ANSWER);
        }
        locListener = new DispLocListener();
        activateLogger();
        Log.i(TAG, getMethodName()
                + (null == savedState ? "" : (getString(R.string.restore) + " " + answer)));
    }

    private void activateLogger() {
        /****
         *
         if ( locationManagerInitialized == false ) {
         try {
         Location loc = lm.getLastKnownLocation(gps);
         locationManagerInitialized = true;
         } catch (SecurityException ex) {
         Log.i(TAG, getMethodName() + "No permission to use GPS:" + ex.getMessage());
         }
         }
         */
        try {
            lm.requestLocationUpdates(gps, 30000L, 10.0f, locListener);
        }
        catch ( SecurityException ex) {
            Log.i(TAG, getMethodName() + "No permission to use GPS:" + ex.getMessage());
        }
    }

    private void updateLocationDisplay(Location location) {
        if (location != null) {
            valLatitude.setText((Double.toString(location.getLatitude())));
            valLongitude.setText((Double.toString(location.getLongitude())));
            valAltitude.setText((Double.toString(location.getAltitude())));
        } else {
            valLatitude.setText(getString(R.string.noGpsActive));
            valLongitude.setText(getString(R.string.noGpsActive));
            valAltitude.setText(getString(R.string.noGpsActive));
        }
    }

    private class DispLocListener implements LocationListener {
        public void onLocationChanged(Location location) {
            updateLocationDisplay(location);
        }

        public void onProviderDisabled(String provider) {
            updateLocationDisplay(null);
            Log.i(TAG, getMethodName() + ", GPS Provider disabled");
        }

        public void onProviderEnabled(String provider) {
            activateLogger();
            Location loc = null;
            try {
                loc = lm.getLastKnownLocation(gps);
                locationManagerInitialized = true;
            }
            catch ( SecurityException ex) {
                Log.i(TAG, getMethodName() + "No permission to use GPS:" + ex.getMessage());
            }
            updateLocationDisplay(loc);
            Log.i(TAG, getMethodName() + ", GPS Provider enabled");
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, getMethodName() + ", GPS Provider status changed");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Notification that the activity will be started
        Log.i(TAG, getMethodName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Notification that the activity is starting
        Log.i(TAG, getMethodName());
        try {
        } catch (NullPointerException ex) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Notification that the activity will interact with the user
        Log.i(TAG, getMethodName());
        if (lm != null)
        {
            lm.removeUpdates(locListener);
            activateLogger();
            Log.i(TAG, getMethodName() + ", readded GPS updates");
        }
        Log.i(TAG, getMethodName());
    }

    protected void onPause() {
        super.onPause();
        Log.i(TAG, getMethodName());
        if (lm != null)
        {
            lm.removeUpdates(locListener);
            Log.i(TAG, getMethodName() + ", removed GPS updates");
        }
        // Notification that the activity will stop interacting with the user
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Notification that the activity is no longer visible
        Log.i(TAG, getMethodName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Notification the activity will be destroyed
        Log.i(TAG,
                getMethodName()
                        // Log which, if any, configuration changed
                        + Integer.toString(getChangingConfigurations(), 16));
    }

    private String getMethodName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[3].getMethodName();
    }
    // ////////////////////////////////////////////////////////////////////////////
    // Called during the lifecycle, when instance state should be saved/restored
    // ////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save instance-specific state
        outState.putString(ANSWER, state);
        super.onSaveInstanceState(outState);
        Log.i(TAG, getMethodName());

    }
    /*
    @Override
    public Object onRetainNonConfigurationInstance() {
        Log.i(TAG, getMethodName());
        return getTaskId();
    }*/

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        // Restore state; we know savedState is not null
        String answer = null != savedState ? savedState.getString(ANSWER) : "";
        Object oldTaskObject = getLastNonConfigurationInstance();
        if (null != oldTaskObject) {
            int oldtask = ((Integer) oldTaskObject).intValue();
            int currentTask = getTaskId();
            // Task should not change across a configuration change
            assert oldtask == currentTask;
        }
        Log.i(TAG, getMethodName()
                + (null == savedState ? "" : getString(R.string.restore)) + " " + answer);
    }

    // ////////////////////////////////////////////////////////////////////////////
    // These are the minor lifecycle methods, you probably won't need these
    // ////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onPostCreate(Bundle savedState) {
        super.onPostCreate(savedState);
        String answer = null;
        // savedState could be null
        if (null != savedState) {
            answer = savedState.getString(ANSWER);
        }
        Log.i(TAG, getMethodName()
                + (null == savedState ? "" : (getString(R.string.restore) + " " + answer)));

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i(TAG, getMethodName());
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Log.i(TAG, getMethodName());
    }

}
