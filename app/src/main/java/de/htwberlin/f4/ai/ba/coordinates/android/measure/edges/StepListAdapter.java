package de.htwberlin.f4.ai.ba.coordinates.android.measure.edges;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.carol.bvg.R;

import java.util.List;

import de.htwberlin.f4.ai.ba.coordinates.android.measure.StepData;

/**
 * Created by benni on 13.08.2017.
 */

public class StepListAdapter extends ArrayAdapter<StepData> {

    public StepListAdapter(@NonNull Context context, List<StepData> stepList) {
        super(context, 0, stepList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_edge_details_listview_step, parent, false);
        }

        TextView stepNumberView = (TextView) convertView.findViewById(R.id.listview_group_stepname);
        TextView stepCoordsView = (TextView) convertView.findViewById(R.id.listview_group_step_coords);

        stepNumberView.setText("# " + String.valueOf(position+1));

        StepData stepData = getItem(position);

        float roundX = Math.round(stepData.getCoords()[0] * 100.0f) / 100.0f;
        float roundY = Math.round(stepData.getCoords()[1] * 100.0f) / 100.0f;
        float roundZ = Math.round(stepData.getCoords()[2] * 100.0f) / 100.0f;

        stepCoordsView.setText(roundX + " | " + roundY + " | " + roundZ);

        return convertView;
    }
}
