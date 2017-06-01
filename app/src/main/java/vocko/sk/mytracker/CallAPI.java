package vocko.sk.mytracker;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


class CallAPI extends AsyncTask<String, String, String> {
    Location location = null;
    long trackid;
    Context context;
    String uid;
    TrackerDbHelper dbHelper = TrackerDbHelper.getHelper(this.context);

    public CallAPI(Context context, Location location) {
        this.location = location;
        this.context = context;
    }

    public CallAPI(Context context, long trackid) {
        this.trackid = trackid;
        this.context = context.getApplicationContext();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0];
        String resultToDisplay = "";
        URL url;

        try {
            url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();

            uid = dbHelper.getUid();
                ObjectOutputStream out = new ObjectOutputStream(os);

                out.writeObject(uid);

                Cursor cursor = dbHelper.getTrackCursor(trackid);
                if (cursor.moveToFirst()) {
                    while (cursor.moveToNext()) {
                        MyLocation myLocation = new MyLocation();

                        String provider = cursor.getString(
                                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_PROVIDER)
                        );
                        float accuracy = cursor.getFloat(
                                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_ACCURACCY)
                        );
                        long time = cursor.getLong(
                                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_TIME)
                        );
                        double longitude = cursor.getDouble(
                                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_LONGITUDE)
                        );
                        double latitude = cursor.getDouble(
                                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_LATITUDE)
                        );
                        double altitude = cursor.getDouble(
                                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_ALTITUDE)
                        );
                        float bearing = cursor.getFloat(
                                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_BEARING)
                        );
                        long trackid = cursor.getLong(
                                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_TRACKID)
                        );
                        float speed = cursor.getFloat(
                                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_SPEED)
                        );
                        double distance = cursor.getDouble(
                                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_DISTANCE)
                        );
                        int satellies = cursor.getInt(
                                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_SATELLITES)
                        );

                        myLocation.setProvider(provider);
                        myLocation.setAccuracy(accuracy);
                        myLocation.setTime(time);
                        myLocation.setLongitude(longitude);
                        myLocation.setLatitude(latitude);
                        myLocation.setAltitude(altitude);
                        myLocation.setBearing(bearing);
                        myLocation.setTrackid(trackid);
                        myLocation.setSpeed(speed);
                        myLocation.setDistance(distance);
                        myLocation.setSatellites(satellies);

                        out.writeObject(myLocation);
                    }

                cursor.close();
                out.flush();
                out.close();
            }

            os.close();
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == 200) {
                InputStream is = urlConnection.getInputStream();
                ObjectInputStream in = new ObjectInputStream(is);
                String suid = (String) in.readObject();
                in.close();

                if ((uid == null) || (!uid.equals(suid))) {
                    uid = suid;
                    dbHelper.setUid(suid);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
