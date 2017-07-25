package de.htwberlin.f4.ai.ma.prototype_temp.nodelist;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;

import java.util.ArrayList;

/**
 * Created Johann Winter
 */

class NodeListAdapter extends ArrayAdapter {

    Activity context;
    ArrayList<String> nodeNames;
    ArrayList<String> nodePicturePaths;


    public NodeListAdapter(Activity context, ArrayList<String> nodeNames, ArrayList<String> nodePicturePaths) {
        super(context, R.layout.item_nodes_listview);

        this.context = context;
        this.nodeNames = nodeNames;
        this.nodePicturePaths = nodePicturePaths;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder viewHolder = null;

        if (row == null) {

            LayoutInflater layoutInflater = context.getLayoutInflater();
            row = layoutInflater.inflate(R.layout.item_nodes_listview, null, true);
            viewHolder = new ViewHolder(row);
            row.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

        viewHolder.nodeIdTextView.setText(nodeNames.get(position));
        System.out.println("#### nodeNames.get POSITION:" + position + " nodeName: " + nodeNames.get(position));
        System.out.println("#### nodePicturePaths.get POSITION:" + position + " nodePicturePath: " + nodePicturePaths.get(position));
        Glide.with(getContext()).load(nodePicturePaths.get(position)).into(viewHolder.nodeImageView);

        return row;
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

