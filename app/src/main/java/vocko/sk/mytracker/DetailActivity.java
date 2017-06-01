package vocko.sk.mytracker;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class DetailActivity extends FragmentActivity implements
    ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private Bundle mBundle;
    private TrackerDbHelper dbHelper;
    private String[] tabs = {"Track info", "Kalorie"};
    private long pocetKalorii;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        actionBar = getActionBar();
        mBundle = getIntent().getExtras();
        dbHelper = TrackerDbHelper.getHelper(getApplicationContext());
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.summary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_map:
                Intent intent = new Intent(getApplicationContext(), MapViewActivity.class);
                intent.putExtras(mBundle);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    private class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    SummaryFragment summaryFragment =
                            new SummaryFragment();
                    summaryFragment.setArguments(mBundle);

                    return summaryFragment;

                case 1:
                     KalorieFragment fragment = new KalorieFragment();
                    Intent in = getIntent();
                    Bundle iBundle  = in.getExtras();
                    long trackid = iBundle.getLong("trackid");
                    Track trat = dbHelper.getTrack(trackid);
                    pocetKalorii = vypocitajKalorie(trat);
                    iBundle.putLong("pocetKalorii", pocetKalorii);
                    fragment.setArguments(iBundle);
                    return fragment;
                         }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private long vypocitajKalorie(Track trat) {

        return 12 * (trat.getTime()/100000);
    }

}
