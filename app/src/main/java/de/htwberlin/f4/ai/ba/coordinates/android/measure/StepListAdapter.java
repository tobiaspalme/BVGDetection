package de.htwberlin.f4.ai.ba.coordinates.android.measure;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.carol.bvg.R;

import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.node.Node;

/**
 * Created by benni on 03.08.2017.
 */

public class StepListAdapter extends ArrayAdapter<StepData> {

    // TODO: map für nodename node?
    private List<Node> nodeList;
    private List<String> nodeNames;
    private NodeSpinnerListener listener;
    private static final String DEFAULT_NODE = "Node zuordnen";

    public StepListAdapter(Context context, List<StepData> data, List<Node> nodes) {
        super(context, 0, data);
        nodeList = nodes;
        nodeNames = new ArrayList<>();
        // add default value
        nodeNames.add(DEFAULT_NODE);
        for (Node node : nodeList) {
            nodeNames.add(node.getId());
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_coordinates_measure_step_listview, parent, false);
        }
        final StepData stepData = getItem(position);

        TextView name = (TextView) convertView.findViewById(R.id.listview_group_poiname);
        TextView coords = (TextView) convertView.findViewById(R.id.listview_group_poi_coords);

        Spinner spinner = (Spinner) convertView.findViewById(R.id.listview_group_spinner);
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(convertView.getContext(), android.R.layout.simple_spinner_dropdown_item, nodeNames);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (listener != null) {
                    // if the selected node is not the default entry
                    if (!spinnerAdapter.getItem(i).equals(DEFAULT_NODE)) {
                        // i-1 because nodelist doesnt contain the "default_node"
                        Node node = nodeList.get(i-1);
                        stepData.setNode(node);
                        listener.onNodeSelected(node, stepData);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        if (stepData != null) {
            name.setText(stepData.getStepName());
            double roundX = Math.round(stepData.getCoords()[0] * 100.0) / 100.0;
            double roundY = Math.round(stepData.getCoords()[1] * 100.0) / 100.0;
            double roundZ = Math.round(stepData.getCoords()[2] * 100.0) / 100.0;
            coords.setText(roundX + " / " + roundY + " / " + roundZ);

            // set previously selected node
            if (stepData.getNode() != null) {
                // +1 because nodelist doesnt contain "default_node"
                spinner.setSelection(nodeList.indexOf(stepData.getNode())+1);
            }
        }

        return convertView;
    }

    public void setNodeSpinnerListener(NodeSpinnerListener listener) {
        this.listener = listener;
    }
}
