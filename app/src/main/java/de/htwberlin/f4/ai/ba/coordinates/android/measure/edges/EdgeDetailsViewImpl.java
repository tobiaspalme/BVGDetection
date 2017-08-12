package de.htwberlin.f4.ai.ba.coordinates.android.measure.edges;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.carol.bvg.R;

import de.htwberlin.f4.ai.ba.coordinates.android.BaseActivity;
import de.htwberlin.f4.ai.ma.prototype_temp.EditTextPreference;

/**
 * Created by benni on 12.08.2017.
 */

public class EdgeDetailsViewImpl extends BaseActivity {

    private ImageView startNodeImage;
    private ImageView targetNodeImage;

    private TextView startNodeCoordsView;
    private TextView targetNodeCoordsView;
    private TextView infoView;

    private EditText distanceView;

    private Switch handycapSwitch;

    private Button deleteBtn;
    private Button saveBtn;

    private ListView stepListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_edge_details, contentFrameLayout);

        Bundle bundle = getIntent().getBundleExtra(EDGE_DETAILS_BUNDLE);
        if (bundle != null) {
            String startNodeID = bundle.getString(STARTNODE_KEY);
            String targetNodeID = bundle.getString(TARGETNODE_KEY);

            Log.d("tmo", "startnode: " + startNodeID);
            Log.d("tmo", "targetnode: " + targetNodeID);
        }

        startNodeImage = (ImageView) findViewById(R.id.edgedetails_start_image);
        targetNodeImage = (ImageView) findViewById(R.id.edgedetails_target_image);

        startNodeCoordsView = (TextView) findViewById(R.id.edgedetails_start_coords);
        targetNodeCoordsView = (TextView) findViewById(R.id.edgedetails_target_coords);
        infoView = (TextView) findViewById(R.id.edgedetails_additional_info);

        distanceView = (EditText) findViewById(R.id.edgedetails_distance_edit);

        handycapSwitch = (Switch) findViewById(R.id.edgedetails_handycap_switch);

        deleteBtn = (Button) findViewById(R.id.edgedetails_delete);
        saveBtn = (Button) findViewById(R.id.edgedetails_save);

        stepListView = (ListView) findViewById(R.id.edgedetails_steplist);
    }
}
