package de.htwberlin.f4.ai.ma.android.measure.edges;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.android.BaseActivity;
import de.htwberlin.f4.ai.ma.measurement.WKT;
import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.node.Node;

/**
 * EdgeDetailsViewImpl class which implements the EdgeDetailsView Interface
 *
 * Used for managing edge details
 *
 * Author: Benjamin Kneer
 */

public class EdgeDetailsViewImpl extends BaseActivity implements EdgeDetailsView{

    private ImageView startNodeImage;
    private ImageView targetNodeImage;

    private TextView startNodeCoordsView;
    private TextView targetNodeCoordsView;
    private TextView startNodeIdView;
    private TextView targetNodeIdView;
    private TextView distanceValueView;

    private EditText infoView;

    private Switch handycapSwitch;

    private Button deleteBtn;
    private Button saveBtn;

    // shows all individual steps for the edge
    private ListView stepListView;
    // stores all individual steps for the edge
    private StepListAdapter stepListAdapter;

    private EdgeDetailsController controller;


    public EdgeDetailsViewImpl() {
        controller = new EdgeDetailsControllerImpl();
        controller.setView(this);
    }


    /************************************************************************************
    *                                                                                   *
    *                               Activity Methods                                    *
    *                                                                                   *
    *************************************************************************************/


    /**
     * Activity Event
     *
     * Load layout and register listeners
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // find contentframe
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        // inflate layout
        getLayoutInflater().inflate(R.layout.activity_edge_details, contentFrameLayout);
        setTitle("Wegdetails");

        // get start and target node from bundle
        Bundle bundle = getIntent().getBundleExtra(EDGE_DETAILS_BUNDLE);

        if (bundle != null) {
            String startNodeID = bundle.getString(STARTNODE_KEY);
            String targetNodeID = bundle.getString(TARGETNODE_KEY);
            // inform controller
            if (controller != null) {
                controller.setNodes(startNodeID, targetNodeID);
            }
        }

        /************        find UI Elements and set listeners          ************/

        startNodeImage = (ImageView) findViewById(R.id.edgedetails_start_image);
        targetNodeImage = (ImageView) findViewById(R.id.edgedetails_target_image);

        startNodeCoordsView = (TextView) findViewById(R.id.edgedetails_start_coords);
        targetNodeCoordsView = (TextView) findViewById(R.id.edgedetails_target_coords);
        startNodeIdView = (TextView) findViewById(R.id.edgedetails_start_id);
        targetNodeIdView = (TextView) findViewById(R.id.edgedetails_target_id);
        distanceValueView = (TextView) findViewById(R.id.edgedetails_distance_value);

        infoView = (EditText) findViewById(R.id.edgedetails_info_edit);

        handycapSwitch = (Switch) findViewById(R.id.edgedetails_handycap_switch);

        deleteBtn = (Button) findViewById(R.id.edgedetails_delete);
        saveBtn = (Button) findViewById(R.id.edgedetails_save);

        stepListView = (ListView) findViewById(R.id.edgedetails_steplist);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null) {
                    controller.onDeleteClicked();
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null) {
                    controller.onSaveClicked();
                }
            }
        });

        handycapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (controller != null) {
                    controller.onHandycapChanged(b);
                }
            }
        });

        infoView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // inform listener about changed description
                if (controller != null) {
                    controller.onEdgeInfoChanged(editable.toString());
                }
            }
        });


        stepListAdapter = new StepListAdapter(getContext(), new ArrayList<StepData>());
        stepListView.setAdapter(stepListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (controller != null) {
            controller.onResume();
        }
    }


    /************************************************************************************
    *                                                                                   *
    *                               Interface Methods                                   *
    *                                                                                   *
    *************************************************************************************/


    /**
     * get the view's context
     *
     * @return Context
     */
    @Override
    public Context getContext() {
        return this;
    }


    /**
     * update start node
     *
     * update the picture, coordinate, name
     *
     * @param node start node
     */
    @Override
    public void updateStartNodeInfo(Node node) {
        // update node name
        startNodeIdView.setText(node.getId());

        // check if there is a picture saved for this node
        if (node.getPicturePath() == null) {
            startNodeImage.setImageResource(R.drawable.unknown);
        } else {
            Uri imageUri = Uri.parse(node.getPicturePath());
            File image = new File(imageUri.getPath());

            if (image.exists()) {
                //using glide to reduce ui lag
                Glide.with(getContext())
                        .load(node.getPicturePath())
                        .into(startNodeImage);
            }
        }

        // check if the node has coordinates
        if (node.getCoordinates() != null && node.getCoordinates().length() > 0) {
            // convert wkt coordinates to float[]
            float[] nodeCoordinates = WKT.strToCoord(node.getCoordinates());
            // round the coordinates to fit ui
            float roundX = Math.round(nodeCoordinates[0] * 100.0) / 100.0f;
            float roundY = Math.round(nodeCoordinates[1] * 100.0) / 100.0f;
            float roundZ = Math.round(nodeCoordinates[2] * 100.0) / 100.0f;
            // update view with coordinates
            startNodeCoordsView.setText(roundX + " | " + roundY + " | " + roundZ);
        }
        // if the node doesn't have coordinates yet
        else {
            startNodeCoordsView.setText("0.0 | 0.0 | 0.0");
        }
    }


    /**
     * update target node
     *
     * update the picture, coordinate, name
     *
     * @param node start node
     */
    @Override
    public void updateTargetNodeInfo(Node node) {
        // update name
        targetNodeIdView.setText(node.getId());

        // check if there is a picture
        if (node.getPicturePath() == null) {
            targetNodeImage.setImageResource(R.drawable.unknown);
        } else {
            Uri imageUri = Uri.parse(node.getPicturePath());
            File image = new File(imageUri.getPath());

            if (image.exists()) {
                //using glide to reduce ui lag
                Glide.with(getContext())
                        .load(node.getPicturePath())
                        .into(targetNodeImage);
            }
        }


        // check if the node has coordinates
        if (node.getCoordinates() != null && node.getCoordinates().length() > 0) {
            // convert wkt string to float[]
            float[] nodeCoordinates = WKT.strToCoord(node.getCoordinates());
            // round coordinates
            float roundX = Math.round(nodeCoordinates[0] * 100.0) / 100.0f;
            float roundY = Math.round(nodeCoordinates[1] * 100.0) / 100.0f;
            float roundZ = Math.round(nodeCoordinates[2] * 100.0) / 100.0f;
            // update view with rounded coordinates
            targetNodeCoordsView.setText(roundX + " | " + roundY + " | " + roundZ);
        } else {
            targetNodeCoordsView.setText("0.0 | 0.0 | 0.0");
        }
    }


    /**
     * update edge
     *
     * updated informations: distance, handycapfriendly, description
     * @param edge
     */
    @Override
    public void updateEdgeInfo(Edge edge) {
         float edgeDistance = edge.getWeight();
        distanceValueView.setText(String.valueOf(Math.round(edgeDistance * 100.0) / 100.0f) + " m");

        // check for handycap
        if (edge.getAccessibility()) {
            handycapSwitch.setChecked(true);
        } else {
            handycapSwitch.setChecked(false);
        }

        // update view with description
        infoView.setText(edge.getAdditionalInfo());

        // fill steplisteradapter with step data from edge
        List<StepData> stepDataList = new ArrayList<>();
        List<String> stepCoords = edge.getStepCoordsList();
        for (String coordStr : stepCoords) {
            float[] coordinates = WKT.strToCoord(coordStr);
            StepData stepData = new StepData();
            stepData.setCoords(coordinates);
            stepDataList.add(stepData);
        }

        stepListAdapter.addAll(stepDataList);
    }
}
