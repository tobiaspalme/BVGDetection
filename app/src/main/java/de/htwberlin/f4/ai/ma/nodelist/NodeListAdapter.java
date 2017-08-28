package de.htwberlin.f4.ai.ma.nodelist;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.example.carol.bvg.R;

import java.util.ArrayList;

/**
 * Created Johann Winter
 */

public class NodeListAdapter extends ArrayAdapter {

    private Activity context;
    private ArrayList<String> nodeNames;
    private ArrayList<String> nodeDescriptions;
    private ArrayList<String> nodePicturePaths;

    private final int REGULAR_ITEM = 0;   // For "normal" Nodes
    private final int DISTANCE_ITEM = 1;  // For distances between Nodes in Listview of NavigationActivity



    public NodeListAdapter(Activity context, ArrayList<String> nodeNames, ArrayList<String> nodeDescriptions, ArrayList<String> nodePicturePaths) {
        super(context, R.layout.item_nodes_listview);

        this.context = context;
        this.nodeNames = nodeNames;
        this.nodeDescriptions = nodeDescriptions;
        this.nodePicturePaths = nodePicturePaths;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder viewHolder = null;

        if (row == null) {

            LayoutInflater layoutInflater = context.getLayoutInflater();

            if (getItemViewType(position) == REGULAR_ITEM) {
                row = layoutInflater.inflate(R.layout.item_nodes_listview, null, true);
            } else {
                row = layoutInflater.inflate(R.layout.item_navigation_distance_separator, null, true);
            }

            viewHolder = new ViewHolder(row);
            row.setTag(viewHolder);
        }

        else {
            viewHolder = (ViewHolder) row.getTag();
        }


        if (getItemViewType(position) == REGULAR_ITEM) {
            viewHolder.nodeIdTextView.setText(nodeNames.get(position));
            viewHolder.nodeDescriptionTextView.setText(nodeDescriptions.get(position));

            // Load images
            if (nodePicturePaths.get(position) == null) {
                viewHolder.nodeImageView.setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(R.drawable.unknown).into(viewHolder.nodeImageView);
            } else if (!nodePicturePaths.get(position).equals("")) {
                viewHolder.nodeImageView.setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(nodePicturePaths.get(position)).into(viewHolder.nodeImageView);
            } else {
                viewHolder.nodeImageView.setVisibility(View.INVISIBLE);
            }
            return row;

        } else {
            viewHolder.distanceTextview.setText(nodeNames.get(position));
            Glide.with(getContext()).load(R.drawable.arrow_down).into(viewHolder.arrowImageview);
            return row;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (nodeNames.get(position).startsWith("\t")) {
            return DISTANCE_ITEM;
        } else {
            return REGULAR_ITEM;
        }
    }


    @Override
    public int getViewTypeCount() {
        // Two different row types in NavigationActivity
        return 2;
    }


    @Nullable
    @Override
    public String getItem(int position) {
        return nodeNames.get(position);
    }

    @Override
    public int getCount() {
        return nodeNames.size();
    }
}

