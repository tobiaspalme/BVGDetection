package de.htwberlin.f4.ai.ma.fingerprint.show_fingerprint;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;

import com.example.carol.bvg.R;

import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.android.BaseActivity;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;

/**
 * Created by Johann Winter
 */

public class ShowFingerprintActivity extends BaseActivity {

    DatabaseHandler databaseHandler;
    ExpandableListView fingerprintListview;
    List<String> accesspointList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_show_fingerprint, contentFrameLayout);

        databaseHandler = DatabaseHandlerFactory.getInstance(this);
        fingerprintListview = (ExpandableListView) findViewById(R.id.fingerprint_expandable_listview);

        Intent intent = getIntent();
        String nodeID = (String) intent.getExtras().get("nodeID");

        if (nodeID != null) {
            Node node = databaseHandler.getNode(nodeID);
            accesspointList = new ArrayList<>();
            ShowFingerprintAdapter adapter = new ShowFingerprintAdapter(this, node.getFingerprint());
            fingerprintListview.setAdapter(adapter);

            for (int i = 0; i < node.getFingerprint().getSignalSampleList().size(); i++) {
                fingerprintListview.expandGroup(i);
            }
        }
    }
}

