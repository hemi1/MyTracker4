package vocko.sk.mytracker;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TrackListAdapter extends CursorAdapter {

    TrackerDbHelper dbHelper;
    DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public TrackListAdapter(Context context, Cursor cursor, boolean autoRequery) {
        super(context, cursor, autoRequery);
        dbHelper = TrackerDbHelper.getHelper(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_track, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        long trackid = cursor.getLong(
                cursor.getColumnIndexOrThrow(TablesAPI.COL_SUMMARY_TRACKID));
        long time = cursor.getLong(
                cursor.getColumnIndexOrThrow(TablesAPI.COL_SUMMARY_TIME));
        double distance = cursor.getDouble(
                cursor.getColumnIndexOrThrow(TablesAPI.COL_SUMMARY_DISTANCE));

        TextView tvRoute = (TextView) view.findViewById(R.id.tvBody);
        TextView tvTime = (TextView) view.findViewById(R.id.tvPriority);
        TextView tvTimeTotal = (TextView) view.findViewById(R.id.tvTimeTotal);

        tvRoute.setText(
                formatter.format(trackid));
        tvTimeTotal.setText(
                SummaryFragment.convertTime(time));
        tvTime.setText(
                SummaryFragment.convertMeters(distance));
    }
}
