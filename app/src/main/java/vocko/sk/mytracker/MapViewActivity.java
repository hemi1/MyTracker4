package vocko.sk.mytracker;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapViewActivity extends FragmentActivity  {

    Bundle mBundle;
    private GoogleMap mMap;
    private TrackerDbHelper dbHelper;
    private TabsPagerAdapter mAdapter;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        viewPager = (ViewPager) findViewById(R.id.pager);
        mBundle = getIntent().getExtras();
        dbHelper = TrackerDbHelper.getHelper(getApplicationContext());
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(0);
    }
    private class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    SupportMapFragment mapFragment = SupportMapFragment.newInstance();
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap map) {

                            long trackid = mBundle.getLong("trackid");

                            PolylineOptions po = new PolylineOptions();
                            LatLng firstLatLng = null;
                            LatLng latLng = null;

                            Cursor cursor = dbHelper.getRoute(trackid);

                            if (cursor.moveToFirst()) {
                                while (cursor.moveToNext()) {
                                    double lat = cursor.getDouble(0);
                                    double lng = cursor.getDouble(1);
                                    latLng = new LatLng(lat, lng);
                                    po = po.add(latLng);

                                    if (firstLatLng == null) {
                                        firstLatLng = latLng;
                                    }
                                }
                            }

                            cursor.close();
                            po = po.width(5);
                            po = po.color(Color.RED);

                            if (firstLatLng != null) {
                                map.addPolyline(po);
                                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLatLng, 15));

                                map.addMarker(new MarkerOptions()
                                        .position(firstLatLng)
                                        .title("Štart"));
                                map.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title("Cieľ"));
                            }
                        }
                    });
                    return mapFragment;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }}
