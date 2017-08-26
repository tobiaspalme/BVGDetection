package de.htwberlin.f4.ai.ma.nodelist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carol.bvg.R;

/**
 * Created by Johann Winter
 */

class ViewHolder {
    ImageView nodeImageView;
    TextView nodeIdTextView;
    TextView nodeDescriptionTextView;

    ImageView arrowImageview;
    TextView distanceTextview;

    ViewHolder(View view) {
        nodeImageView = (ImageView) view.findViewById(R.id.preview_imageview);
        nodeIdTextView = (TextView) view.findViewById(R.id.node_name);
        nodeDescriptionTextView = (TextView) view.findViewById(R.id.node_description);

        arrowImageview = (ImageView) view.findViewById(R.id.arrow_imageview);
        distanceTextview = (TextView) view.findViewById(R.id.distance_textview);
    }
}