package vocko.sk.mytracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MainActivity extends Activity {

    private static final String TAG = "MAINACTIVITY";

    private static final int TRACKER_START_MODE = 1;
    private static final int TRACKER_PAUSE_MODE = 2;
    private static final int TRACKER_STOP_MODE = 0;

    private int trackermode = TRACKER_STOP_MODE;

    TextView timeText;
    TextView statusText;
    ImageButton startButton;
    ImageButton stopButton;
    ListView trackList;
    Button musicButton;

    TrackerDbHelper dbHelper = TrackerDbHelper.getHelper(this);
    SQLiteDatabase db;
    Cursor mCursor;
    TrackListAdapter trackAdapter;
    TrackerService mTrackerService = null;
    Intent trackIntent = null;
    long trackid = 0;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        DateFormat dateTimeString = new SimpleDateFormat("HH:mm:ss");

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String action = intent.getAction();

            if (action.equals(TrackerService.ACTION_PROVIDER_DISABLED)) {
                buildAlertMessageNoGps();
            }
            else if (action.equals(TrackerService.ACTION_DATA_RECEIVED)) {
                if (bundle != null) {
                    Location location = bundle.getParcelable(TrackerService.EXTRA_LAST_LOCATION);
                    timeText.setText(
                            dateTimeString.format(location.getTime()));
                }
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mTrackerService = ((TrackerService.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mTrackerService = null;
        }
    };

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.gps_disabled)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(
                                new Intent(android.provider.Settings
                                        .ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(
                TrackerService.ACTION_DATA_RECEIVED);
        filter.addAction(
                TrackerService.ACTION_PROVIDER_DISABLED);

        registerReceiver(receiver, filter);

        boolean mode = (trackermode == TRACKER_PAUSE_MODE) ||
                (trackermode == TRACKER_STOP_MODE);
        if (mode && (mTrackerService != null)) {
            mTrackerService.initGps();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);

        boolean mode = (trackermode == TRACKER_PAUSE_MODE) ||
                (trackermode == TRACKER_STOP_MODE);

        if (mode) {
            mTrackerService.turnOffGps();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong("trackid", trackid);
        outState.putInt("trackermode", trackermode);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        trackid = bundle.getLong("trackerid");
        trackermode = bundle.getInt("trackermode");

        switch (trackermode) {
            case TRACKER_START_MODE:
                startButton.setBackgroundResource(R.drawable.btn3);
                break;

            case TRACKER_STOP_MODE:
            case TRACKER_PAUSE_MODE:
                startButton.setBackgroundResource(R.drawable.btn1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        trackIntent = new Intent(this, TrackerService.class);
        startService(trackIntent);
        bindService(trackIntent, mConnection,
                Context.BIND_AUTO_CREATE);

        timeText = (TextView) findViewById(R.id.tracker_time);
        statusText = (TextView) findViewById(R.id.tracker_status);
        startButton = (ImageButton) findViewById(R.id.startbtn);
        stopButton = (ImageButton) findViewById(R.id.stopbtn);
        trackList = (ListView) findViewById(R.id.tracklist);
        musicButton = (Button)  findViewById(R.id.musicbtn);

        db = dbHelper.getReadableDatabase();
        mCursor = dbHelper.getAllTracks();

        trackAdapter = new TrackListAdapter(this, mCursor, false);
        trackList.setAdapter(trackAdapter);

        registerForContextMenu(trackList);

        trackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor =
                        (Cursor) trackList.getAdapter().getItem(i);
                long trackid = cursor.getLong(
                        cursor.getColumnIndexOrThrow("trackid"));

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("trackid", trackid);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Cursor cursor = (Cursor) trackAdapter.getItem(position);
        long trackid = cursor.getLong(
                cursor.getColumnIndexOrThrow(TablesAPI.COL_SUMMARY_TRACKID));

        switch (item.getItemId()) {
            case R.id.cm_send:
                sendRoute(trackid);
                return true;
            case R.id.cm_delete:
                deleteRoute(trackid);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void sendRoute(long trackid) {
        String url =
                getResources().getString(R.string.comm_srv_url);

        new CallAPI(this, trackid).execute(url);
    }

    private void deleteRoute(long trackid) {
        dbHelper.deleteRoute(trackid);
        refreshTrackList();
    }

    private void refreshTrackList() {
        mCursor.requery();
        trackAdapter.notifyDataSetChanged();
    }

    public void startPauseBtnAction(View view) {
        boolean isNew = false;

        switch (trackermode) {
            case TRACKER_START_MODE:
                dbHelper.insertMarker(trackid, "TRACKER_PAUSE");

                mTrackerService.pauseTracking();
                startButton.setBackgroundResource(R.drawable.btn1);
                trackermode = TRACKER_PAUSE_MODE;

                statusText.setText(R.string.rec_pause);
                break;

            case TRACKER_STOP_MODE:
            case TRACKER_PAUSE_MODE:
                if (trackid == 0) {
                    trackid = System.currentTimeMillis();
                    isNew = true;
                }

                dbHelper.insertMarker(trackid, "TRACKER_RESUME");

                mTrackerService.startTracking(trackid, isNew);
                trackermode = TRACKER_START_MODE;

                statusText.setText(R.string.rec_inprg);
                startButton.setBackgroundResource(R.drawable.btn3);
                refreshTrackList();
                break;
            default:
                break;
        }
    }

    public void stopBtnAction(View view) {
        if (trackermode != TRACKER_STOP_MODE) {
            trackermode = TRACKER_STOP_MODE;
            trackid = 0;
            mTrackerService.stopTracking();

            startButton.setBackgroundResource(R.drawable.btn1);
            statusText.setText("");
            refreshTrackList();
        }
    }

    public void musicBtnAction(View view) {
        Intent intent = new Intent(MainActivity.this, MusicActivity.class);
        startActivity(intent);
    }


}
