package com.example.carol.bvg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * adapter for the result list view in locationActivity
 */
public class LocationResultAdapter extends ArrayAdapter<LocationResult> {
    public LocationResultAdapter(Context context, ArrayList<LocationResult> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        LocationResult locationResult = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_locationresult, parent, false);
        }
        // Lookup view for data population
        TextView tvLocationSetting = (TextView) convertView.findViewById(R.id.tx_resultsSetting);
        TextView tvPOI = (TextView) convertView.findViewById(R.id.tx_resultPOI);
        TextView tvMeasuredPOI = (TextView) convertView.findViewById(R.id.tx_resultMeasuredPOI);
        TextView tvMeasuredTime = (TextView) convertView.findViewById(R.id.tx_measuredTime);
        // Populate the data into the template view using the data object
        tvLocationSetting.setText(locationResult.settings);
        tvPOI.setText(locationResult.poi);
        tvMeasuredPOI.setText(locationResult.measuredPoi);
        tvMeasuredTime.setText(locationResult.measuredTime);
        // Return the completed view to render on screen
        return convertView;
    }


}
