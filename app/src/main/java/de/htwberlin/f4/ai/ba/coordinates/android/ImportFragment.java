package de.htwberlin.f4.ai.ba.coordinates.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carol.bvg.R;

/**
 * Created by benni on 17.07.2017.
 */

public class ImportFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coordinates_import, container, false);

        return root;
    }
}
