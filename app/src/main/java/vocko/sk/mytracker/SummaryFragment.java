package vocko.sk.mytracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;


public class SummaryFragment extends Fragment {

    TextView tvDistance;
    TextView tvTimetotal;
    TextView tvMovetime;
    TextView tvAvgspeed;
    TextView tvMaxspeed;
    TextView tvMaxalt;

    TrackerDbHelper dbHelper;

    static DecimalFormat df = new DecimalFormat("0.00");

    private long mTrackid;

    public SummaryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        mTrackid = bundle.getLong("trackid");
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = TrackerDbHelper.getHelper(getContext());

        tvDistance = (TextView) getView().findViewById(
                R.id.stats_distance);
        tvTimetotal = (TextView) getView().findViewById(
                R.id.stats_timetotal);
        tvMovetime= (TextView) getView().findViewById(
                R.id.stats_movetime);
        tvAvgspeed = (TextView) getView().findViewById(
                R.id.stats_avgspeed);
        tvMaxspeed = (TextView) getView().findViewById(
                R.id.stats_maxspeed);
        tvMaxalt = (TextView) getView().findViewById(
                R.id.stats_maxalt);

        Track track = dbHelper.getTrack(mTrackid);
        if (track != null) {
            tvDistance.setText(convertMeters(track.getDistance())
            );

            tvAvgspeed.setText(
                    convertMsKmH(track.getAvgspeed())
            );
            tvMaxspeed.setText(
                    convertMsKmH(track.getMaxspeed())
            );
            tvMaxalt.setText(
                    convertMeters(track.getMaxalt())
            );
            tvTimetotal.setText(
                convertTime(track.getTime())
            );
            tvMovetime.setText(
                    convertTime(track.getMovtime())
            );
        }
    }

    public static String convertMsKmH(float value) {
        return df.format(value * 3.6) + " km/hod";
    }

    public static String convertMeters(double value) {
        String distance;

        if (value < 1000) {
            distance = df.format(value) + " m";
        } else {
            distance = df.format((value / 1000f)) + " km";
        }
        return distance;
    }
    public static String convertTime(long value) {
        String time;

        long minutes = value / (60 * 1000) % 60;
        long hours = value / (60 * 60 * 1000) % 24;

        if (hours < 1) {
            time = minutes + " min.";
        } else {
            time = hours + " hod. " + minutes + " min.";
        }

        return time;
    }
}
