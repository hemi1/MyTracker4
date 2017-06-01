package vocko.sk.mytracker;

/**
 * Created by Rastislav on 31.5.2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class KalorieFragment extends Fragment {
    private TextView t;
    private View myInflatedView;
    private Bundle mBundle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_kalorie, container, false);

        mBundle = getArguments();
        t = (TextView) myInflatedView.findViewById(R.id.kalorie_long);
        t.setText("            "+mBundle.getLong("pocetKalorii"));
        return myInflatedView;
    }




}