package vocko.sk.mytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import java.util.ArrayList;

public class TrackerDbHelper extends SQLiteOpenHelper {

    private static TrackerDbHelper instance = null;

    private static final String SQL_CREATE_TRACKER =
            "create table "
            + TablesAPI.TABLE_TRACKER_BASE
            + "("
            + TablesAPI._ID
            + " integer primary key,"
            + TablesAPI.COL_BASE_PROVIDER
            + " text,"
            + TablesAPI.COL_BASE_ACCURACCY
            + " numeric,"
            + TablesAPI.COL_BASE_TIME
            + " numeric,"
            + TablesAPI.COL_BASE_LONGITUDE
            + " numeric,"
            + TablesAPI.COL_BASE_LATITUDE
            + " numeric,"
            + TablesAPI.COL_BASE_ALTITUDE
            + " numeric,"
            + TablesAPI.COL_BASE_BEARING
            + " numeric,"
            + TablesAPI.COL_BASE_SPEED
            + " numeric,"
            + TablesAPI.COL_BASE_TRACKID
            + " numeric,"
            + TablesAPI.COL_BASE_DISTANCE
            + " numeric,"
            + TablesAPI.COL_BASE_SATELLITES
            + " numeric)"
            ;
    private static final String SQL_CREATE_IDENTITY =
            "create table "
            + TablesAPI.TABLE_TRACKER_IDENTITY
            + "("
            + TablesAPI._ID
            + " integer primary key,"
            + TablesAPI.COL_IDENTITY_UID
            + " text,"
            + TablesAPI.COL_IDENTITY_FIRSTNAME
            + " text,"
            + TablesAPI.COL_IDENTITY_LASTNAME
            + " text,"
            + TablesAPI.COL_IDENTITY_EMAIL
            + " text)";

    private static final String SQL_CREATE_SUMMARY =
            "create table "
            + TablesAPI.TABLE_TRACKER_SUMMARY
            + "("
            + TablesAPI._ID
            + " integer primary key,"
            + TablesAPI.COL_SUMMARY_TRACKID
            + " numeric,"
            + TablesAPI.COL_SUMMARY_DISTANCE
            + " numeric,"
            + TablesAPI.COL_SUMMARY_TIME
            + " numeric,"
            + TablesAPI.COL_SUMMARY_MOVTIME
            + " numeric,"
            + TablesAPI.COL_SUMMARY_MAXSPEED
            + " numeric,"
            + TablesAPI.COL_SUMMARY_MAXALT
            + " numeric,"
            + TablesAPI.COL_SUMMARY_AVGSPEED
            + " numeric,"
            + TablesAPI.COL_SUMMARY_ROUTEDESC
            + " text,"
            + TablesAPI.COL_SUMMARY_ACTTYPE
            + " text,"
            + TablesAPI.COL_SUMMARY_STATUS
            + " text)"
            ;

    private static final String SQL_DELETE_TRACKER =
            "drop table if exists "
            + TablesAPI.TABLE_TRACKER_BASE;

    private static final String SQL_DELETE_IDENTITY =
            "drop table if exists "
            + TablesAPI.TABLE_TRACKER_IDENTITY;

    private static final String SQL_DELETE_SUMMARY =
            "drop table if exists "
            + TablesAPI.TABLE_TRACKER_SUMMARY;

    public static synchronized TrackerDbHelper getHelper(Context context) {
        if (instance == null) {
            instance = new TrackerDbHelper(context);
        }

        return instance;
    }

    public TrackerDbHelper(Context context) {
        super(context, TablesAPI.DATABASE, null, TablesAPI.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TRACKER);
        db.execSQL(SQL_CREATE_IDENTITY);
        db.execSQL(SQL_CREATE_SUMMARY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_TRACKER);
        db.execSQL(SQL_DELETE_IDENTITY);
        db.execSQL(SQL_DELETE_SUMMARY);
        onCreate(db);
    }

    public void insertLocation(MyLocation myLocation) {
        ContentValues values = new ContentValues();

        values.put(
                TablesAPI.COL_BASE_PROVIDER, myLocation.getProvider());
        values.put(
                TablesAPI.COL_BASE_ACCURACCY, myLocation.getAccuracy());
        values.put(
                TablesAPI.COL_BASE_TIME, myLocation.getTime());
        values.put(
                TablesAPI.COL_BASE_LONGITUDE, myLocation.getLongitude());
        values.put(
                TablesAPI.COL_BASE_LATITUDE, myLocation.getLatitude());
        values.put(
                TablesAPI.COL_BASE_ALTITUDE, myLocation.getAltitude());
        values.put(
                TablesAPI.COL_BASE_BEARING, myLocation.getBearing());
        values.put(
                TablesAPI.COL_BASE_SPEED, myLocation.getSpeed());
        values.put(
                TablesAPI.COL_BASE_TRACKID, myLocation.getTrackid());
        values.put(
                TablesAPI.COL_BASE_DISTANCE, myLocation.getDistance());
        values.put(
                TablesAPI.COL_BASE_SATELLITES, myLocation.getSatellites());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(
                TablesAPI.TABLE_TRACKER_BASE, null, values);
    }

    public void deleteRoute(long trackid) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(
                TablesAPI.TABLE_TRACKER_SUMMARY,
                TablesAPI.COL_SUMMARY_TRACKID
                + "=?",
                new String[] {String.valueOf(trackid)}
        );
        db.delete(
                TablesAPI.TABLE_TRACKER_BASE,
                TablesAPI.COL_BASE_TRACKID
                        + "=?",
                new String[] {String.valueOf(trackid)}
        );
    }

    public void insertMarker(long trackid, String marker) {
        ContentValues values = new ContentValues();

        values.put(
                TablesAPI.COL_BASE_PROVIDER, marker);
        values.put(
                TablesAPI.COL_BASE_TIME, System.currentTimeMillis());
        values.put(
                TablesAPI.COL_BASE_TRACKID, trackid);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(
                TablesAPI.TABLE_TRACKER_BASE, null, values);
    }

    public String getUid() {
        String uid = null;
        SQLiteDatabase db = getReadableDatabase();

        String query =
                "select * from "
                + TablesAPI.TABLE_TRACKER_IDENTITY;

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            uid = c.getString(c.getColumnIndexOrThrow(TablesAPI.COL_IDENTITY_UID));
        }
        c.close();

        return uid;
    }

    public void setUid(String uid) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TablesAPI.TABLE_TRACKER_IDENTITY, "1=1", null);

        ContentValues values = new ContentValues();

        values.put(
                TablesAPI.COL_IDENTITY_UID, uid);
        db.insert(
                TablesAPI.TABLE_TRACKER_IDENTITY, null, values);
    }

    public long getIdle(long trackid) {
        SQLiteDatabase db = getReadableDatabase();
        long totalPause = 0;

        String query =
                "select "
                + TablesAPI.COL_BASE_TIME
                + ","
                + TablesAPI.COL_BASE_PROVIDER
                + " from "
                + TablesAPI.TABLE_TRACKER_BASE
                + " where "
                + TablesAPI.COL_BASE_PROVIDER
                + "!='gps' and "
                + TablesAPI.COL_BASE_TRACKID
                + "=? order by "
                + TablesAPI.COL_BASE_TIME;
                ;

        Cursor c = db.rawQuery(query, new String[] {String.valueOf(trackid)});

        if (c.moveToFirst()) {
            long pause = 0;
            long resume = 0;

            while (c.moveToNext()) {
                String operation = c.getString(1);
                if (operation.equals("TRACKER_PAUSE")) {
                    pause = c.getLong(0);
                }
                if (operation.equals("TRACKER_RESUME")) {
                    resume = c.getLong(0);
                }
                if ((pause != 0) && (resume != 0)) {
                    totalPause += (resume - pause);
                    pause = 0;
                    resume = 0;
                }
            }
        }

        c.close();

        return totalPause;
    }

    public Cursor getRoute(long trackid) {
        SQLiteDatabase db = getReadableDatabase();

        String query =
                "select "
                + TablesAPI.COL_BASE_LATITUDE
                + ","
                + TablesAPI.COL_BASE_LONGITUDE
                + " from "
                + TablesAPI.TABLE_TRACKER_BASE
                + " where "
                + TablesAPI.COL_BASE_LATITUDE
                + " is not null and "
                + TablesAPI.COL_BASE_LONGITUDE
                + " is not null and "
                + TablesAPI.COL_BASE_SPEED
                + " is not null and "
                + TablesAPI.COL_BASE_SPEED
                + ">0 and "
                + TablesAPI.COL_BASE_TRACKID
                + "=? order by "
                + TablesAPI.COL_BASE_TIME;

        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(trackid)});

        return cursor;
    }

    private final MyLocation getLocationFromBase(Cursor cursor) {
        MyLocation myLocation = new MyLocation();

        myLocation.setSpeed(cursor.getFloat(
                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_SPEED))
        );
        myLocation.setTrackid(cursor.getLong(
                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_TRACKID))
        );
        myLocation.setBearing(cursor.getFloat(
                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_BEARING))
        );
        myLocation.setAltitude(cursor.getDouble(
                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_ALTITUDE))
        );
        myLocation.setLatitude(cursor.getDouble(
                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_LATITUDE))
        );
        myLocation.setLongitude(cursor.getDouble(
                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_LONGITUDE))
        );
        myLocation.setProvider(cursor.getString(
                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_PROVIDER))
        );
        myLocation.setAccuracy(cursor.getFloat(
                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_ACCURACCY))
        );
        myLocation.setTime(cursor.getLong(
                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_TIME))
        );
        myLocation.setDistance(cursor.getDouble(
                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_DISTANCE))
        );
        myLocation.setSatellites(cursor.getInt(
                cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_SATELLITES))
        );

        return myLocation;
    }

    private Track getTrackInfo(long trackid) {
        Track track = null;
        SQLiteDatabase db = getReadableDatabase();

        String query =
                "select * from "
                + TablesAPI.TABLE_TRACKER_SUMMARY
                + " where "
                + TablesAPI.COL_SUMMARY_TRACKID
                + "=?";
        Cursor cursor = db.rawQuery(query,
                new String[] {String.valueOf(trackid)});

        if (cursor.moveToFirst()) {
            track = new Track();

            track.setMovtime(cursor.getLong(
                    cursor.getColumnIndexOrThrow(TablesAPI.COL_SUMMARY_MOVTIME))
            );
            track.setTime(cursor.getLong(
                    cursor.getColumnIndexOrThrow(TablesAPI.COL_SUMMARY_TIME))
            );
            track.setAvgspeed(cursor.getFloat(
                    cursor.getColumnIndexOrThrow(TablesAPI.COL_SUMMARY_AVGSPEED))
            );
            track.setDistance(cursor.getDouble(
                    cursor.getColumnIndexOrThrow(TablesAPI.COL_SUMMARY_DISTANCE))
            );
            track.setMaxalt(cursor.getDouble(
                    cursor.getColumnIndexOrThrow(TablesAPI.COL_SUMMARY_MAXALT))
            );
            track.setActtype(cursor.getString(
                    cursor.getColumnIndexOrThrow(TablesAPI.COL_SUMMARY_ACTTYPE))
            );
            track.setMaxspeed(cursor.getFloat(
                    cursor.getColumnIndexOrThrow(TablesAPI.COL_SUMMARY_MAXSPEED))
            );
            track.setRoutedesc(cursor.getString(
                    cursor.getColumnIndexOrThrow(TablesAPI.COL_SUMMARY_ROUTEDESC))
            );
            track.setStatus(cursor.getString(
                    cursor.getColumnIndexOrThrow(TablesAPI.COL_SUMMARY_STATUS))
            );
            track.setTrackid(cursor.getLong(
                    cursor.getColumnIndexOrThrow(TablesAPI.COL_SUMMARY_TRACKID))
            );
        }

        return track;
    }

    public Track getTrack(long trackid) {

        Track track = getTrackInfo(trackid);

        if ((track.getStatus() != null) && (track.getStatus().equals("CLOSE"))) {
            return track;
        }

        Cursor cursor = getTrackCursor(trackid);

        if (cursor.moveToFirst()) {
            track = new Track();

            double minAlt = 100000000;
            double maxAlt = 0;
            float maxSpeed = 0;
            long speed_counter = 0;
            float speed_total = 0;
            long start = -1;
            long finish = 0;

            Location location;

            double nDistance = 0;

            while (cursor.moveToNext()) {
                MyLocation myLocation = getLocationFromBase(cursor);
                if (myLocation.getProvider().equals("gps")) {
                    nDistance += myLocation.getDistance();

                    if (myLocation.getAltitude() > maxAlt) {
                        maxAlt = myLocation.getAltitude();
                    }
                    if (minAlt > myLocation.getAltitude()) {
                        minAlt = myLocation.getAltitude();
                    }
                    if (myLocation.getSpeed() > maxSpeed) {
                        maxSpeed = myLocation.getSpeed();
                    }

                    if (myLocation.getSpeed() > 0) {
                        speed_total += myLocation.getSpeed();
                        speed_counter++;

                        location = new Location("gps");
                        location.setLatitude(
                                myLocation.getLatitude());
                        location.setLongitude(
                                myLocation.getLongitude());
                    }
                }
                if (start == -1) {
                    start = myLocation.getTime();
                }
                finish = myLocation.getTime();
            }

            track.setMaxalt(maxAlt - minAlt);
            track.setMaxspeed(maxSpeed);
            track.setDistance(nDistance);
            track.setAvgspeed(speed_total / speed_counter);
            track.setTime((finish - start) - getIdle(trackid));
            track.setMovtime(speed_counter * 1000);
            track.setTrackid(trackid);
        }
        cursor.close();

        return track;
    }

    public void putSummaryInfo(long trackid, boolean onStop) {
        SQLiteDatabase db  = getWritableDatabase();
        ContentValues cv = new ContentValues();

        if (!onStop) {
            cv.put(
                    TablesAPI.COL_SUMMARY_TRACKID, trackid);
            db.insert(
                    TablesAPI.TABLE_TRACKER_SUMMARY, null, cv);
        } else {
            Track track = getTrack(trackid);

            track.setStatus("CLOSE");

            cv.put(
                    TablesAPI.COL_SUMMARY_ACTTYPE, track.getActtype());
            cv.put(
                    TablesAPI.COL_SUMMARY_AVGSPEED, track.getAvgspeed());
            cv.put(
                    TablesAPI.COL_SUMMARY_DISTANCE,track.getDistance());
            cv.put(
                    TablesAPI.COL_SUMMARY_MAXALT, track.getMaxalt());
            cv.put(
                    TablesAPI.COL_SUMMARY_MAXSPEED, track.getMaxspeed());
            cv.put(
                    TablesAPI.COL_SUMMARY_MOVTIME, track.getMovtime());
            cv.put(
                    TablesAPI.COL_SUMMARY_ROUTEDESC, track.getRoutedesc());
            cv.put(
                    TablesAPI.COL_SUMMARY_STATUS, track.getStatus());
            cv.put(
                    TablesAPI.COL_SUMMARY_TIME, track.getTime());

            db.update(
                    TablesAPI.TABLE_TRACKER_SUMMARY,
                    cv,
                    TablesAPI.COL_SUMMARY_TRACKID
                    + "=?" ,
                    new String[] {String.valueOf(trackid)}
            );
        }
    }

    public Cursor getAllTracks() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TablesAPI.TABLE_TRACKER_SUMMARY,
                new String[] {
                        TablesAPI._ID,
                        TablesAPI.COL_SUMMARY_TIME,
                        TablesAPI.COL_SUMMARY_STATUS,
                        TablesAPI.COL_SUMMARY_DISTANCE,
                        TablesAPI.COL_SUMMARY_TRACKID
                },
                null,
                null,
                null,
                null,
                TablesAPI.COL_SUMMARY_TRACKID
                + " desc",
                null);
        return cursor;
    }

    public final Cursor getTrackCursor(long trackid) {
        SQLiteDatabase db = getReadableDatabase();

        String query =
                "select * from "
                        + TablesAPI.TABLE_TRACKER_BASE
                        + " where "
                        + TablesAPI.COL_BASE_TRACKID
                        + "=? "
                        + "order by "
                        + TablesAPI.COL_BASE_TIME;

        Cursor cursor = db.rawQuery(query,
                new String[] {String.valueOf(trackid)});

        return cursor;
    }

    public ArrayList<Tempo> calculateTempo(long trackid) {

        Cursor cursor = getTrackCursor(trackid);
        ArrayList<Tempo> tempos = new ArrayList<>();

        float distance;
        float lap = 0;
        float tempo;
        int record_count = 0;

        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                distance = cursor.getFloat(
                        cursor.getColumnIndexOrThrow(TablesAPI.COL_BASE_DISTANCE));
                if (distance != 0) {
                    lap += distance;
                    record_count++;

                    if (lap >= 1000) {
                        tempo = lap / record_count;

                        tempos.add(new Tempo(lap, tempo));
                        record_count = 0;
                        lap = 0;
                    }
                }
            }

            if (lap != 0) {
                tempo = lap / record_count;

                tempos.add(new Tempo(lap, tempo));
            }
        }
        cursor.close();

        return tempos;
    }
}
