package de.htwberlin.f4.ai.ma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;

import de.htwberlin.f4.ai.ma.android.BaseActivity;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;

/**
 * Created by Johann Winter
 */

public class NodeShowActivity extends BaseActivity {

    TextView idTextview;
    TextView descriptionTextview;
    TextView wifiNameTextview;
    TextView coordinatesTextView;
    ImageView cameraImageView;

    private Node node;
    DatabaseHandler databaseHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_node_show, contentFrameLayout);

        idTextview = (TextView) findViewById(R.id.id_textview_show);
        descriptionTextview = (TextView) findViewById(R.id.description_textview_show);
        wifiNameTextview = (TextView) findViewById(R.id.wifi_name_textview_show);
        coordinatesTextView = (TextView) findViewById(R.id.coordinates_textview_show);
        cameraImageView = (ImageView) findViewById(R.id.camera_imageview_show);

        final Intent intent = getIntent();
        final String nodeName = (String) intent.getExtras().get("nodeName");

        databaseHandler = DatabaseHandlerFactory.getInstance(this);
        node = databaseHandler.getNode(nodeName);

        idTextview.setText(node.getId());
        descriptionTextview.setText(node.getDescription());

        if (node.getFingerprintImpl() != null) {
            wifiNameTextview.setText(node.getFingerprintImpl().getWifiName());
        }
        coordinatesTextView.setText(node.getCoordinates());


        if (node.getPicturePath() != null) {
            Glide.with(this).load(node.getPicturePath()).into(cameraImageView);
        } else {
            Glide.with(this).load(R.drawable.unknown).into(cameraImageView);
        }

        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MaxPictureActivity.class);
                intent.putExtra("picturePath", node.getPicturePath());
                startActivity(intent);
            }
        });

    }
}
