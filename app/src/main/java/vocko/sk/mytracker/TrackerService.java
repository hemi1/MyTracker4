package vocko.sk.mytracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class TrackerService extends Service {

    public static final String ACTION_DATA_RECEIVED = "mytracker.intent.action.DATA_RECEIVED";
    public static final String ACTION_PROVIDER_DISABLED = "mytracker.intent.action.PROVIDER_DISABLED";

    public static final String EXTRA_LAST_LOCATION = "EXTRA_LAST_LOCATION";
    private static final String TAG = "TRACKERSERVICE";
    private static final float MINIMUM_ACCURACY = 200f;
    private static final float LOCATION_DISTANCE = 0f;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int LOCATION_INTERVAL = 1000 * 1;
    private final IBinder mBinder;
    LocationListener locationListener;
    TrackerDbHelper dbHelper = null;
    boolean logGps = false;
    long trackid = 0;
    private LocationManager mLocationManager = null;
    private Location mLastLocation = null;

    public TrackerService() {
        mBinder = new LocalBinder();
        locationListener = new LocationListener();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        initGps();
        return START_NOT_STICKY;
    }

    public void startTracking(long trackid, boolean isNew) {
        mLastLocation = null;
        logGps = true;
        this.trackid = trackid;

        if (isNew) {
            dbHelper.putSummaryInfo(trackid, false);
        }
    }

    public void pauseTracking() {
        logGps = false;
    }

    public void stopTracking() {
        dbHelper.putSummaryInfo(trackid, true);
        logGps = false;
        trackid = 0;
    }

    public void turnOffGps() {
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(locationListener);
            } catch (SecurityException e) {
                Log.d(TAG, "fail to remove location listeners, ignore", e);
            }
        }
    }

    public void initGps() {
        dbHelper = TrackerDbHelper.getHelper(this);
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    locationListener);
        } catch (SecurityException e) {
            Log.d(TAG, "fail to request location update, ignore", e);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "network provider does not exist," + e.getMessage());
        }

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    locationListener);
        } catch (SecurityException e) {
            Log.d(TAG, "fail to request location update, ignore", e);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "gps provider does not exist," + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (dbHelper != null) {
            dbHelper.close();
        }
        turnOffGps();
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager)
                    getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            if (!mLocationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER)) {
                Intent intent = new Intent(ACTION_PROVIDER_DISABLED);
                sendBroadcast(intent);
            }
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > MINIMUM_ACCURACY;

        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private void publishResults(Location location) {
        Intent intent = new Intent(ACTION_DATA_RECEIVED);
        intent.putExtra(EXTRA_LAST_LOCATION, location);

        sendBroadcast(intent);

    }


    private class LocationListener implements android.location.LocationListener {

        public LocationListener() {
        }

        @Override
        public void onLocationChanged(Location location) {
            double distance;
            String provider = location.getProvider();

            if (isBetterLocation(location, mLastLocation)
                    && provider.equals("gps")) {
                float accuracy = location.getAccuracy();
                // publikuj cas, len ked je presnost <= 20, toto by sa hodilo aby uzivatel nespustil trackovanie na nepresnych datach..
                if (accuracy <= 20) {
                    publishResults(location);
                }

                distance = 0;

                if (logGps) {
                    if (mLastLocation != null) {
                        if (location.getSpeed() > 0) {
                            distance = location.distanceTo(mLastLocation);
                        }
                    }

                    int satellites = location.getExtras().getInt("satellites");

                    MyLocation myLocation = new MyLocation();

                    myLocation.setDistance(
                            distance);
                    myLocation.setSpeed(
                            location.getSpeed());
                    myLocation.setTime(
                            location.getTime());
                    myLocation.setSatellites(
                            satellites);
                    myLocation.setTrackid(
                            trackid);
                    myLocation.setAccuracy(
                            location.getAccuracy());
                    myLocation.setAltitude(
                            location.getAltitude());
                    myLocation.setBearing(
                            location.getBearing());
                    myLocation.setLatitude(
                            location.getLatitude());
                    myLocation.setLongitude(
                            location.getLongitude());
                    myLocation.setProvider(
                            location.getProvider());

                    dbHelper.insertLocation(myLocation);
                    mLastLocation = new Location(location);
                }
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    public class LocalBinder extends Binder {

        TrackerService getService() {
            return TrackerService.this;
        }
    }
}
