package de.htwberlin.f4.ai.ba.coordinates.android.measure;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.carol.bvg.R;

import java.util.List;

/**
 * Created by benni on 03.08.2017.
 */

public class StepListAdapter extends ArrayAdapter<StepData> {

    public StepListAdapter(Context context, List<StepData> data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_coordinates_measure_step_listview, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.listview_group_poiname);
        TextView coords = (TextView) convertView.findViewById(R.id.listview_group_poi_coords);

        StepData stepData = getItem(position);
        if (stepData != null) {
            name.setText(stepData.getStepName());
            coords.setText(stepData.getCoords()[0] + " / " + stepData.getCoords()[1] + " / " + stepData.getCoords()[2]);
        }

        return convertView;
    }
}
