package de.htwberlin.f4.ai.ma.prototype_temp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;

import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerImplementation;

/**
 * Created by Johann Winter
 */

public class NodeShowActivity extends Activity {

    TextView idTextview;
    TextView descriptionTextview;
    TextView coordinatesTextView;
    ImageView cameraImageView;
    String picturePath;

    private Node node;
    DatabaseHandler databaseHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_show);

        idTextview = (TextView) findViewById(R.id.id_textview_show);
        descriptionTextview = (TextView) findViewById(R.id.description_textview_show);
        coordinatesTextView = (TextView) findViewById(R.id.coordinates_textview_show);
        cameraImageView = (ImageView) findViewById(R.id.camera_imageview_show);

        final Intent intent = getIntent();
        final String nodeName = (String) intent.getExtras().get("nodeName");

        databaseHandler = new DatabaseHandlerImplementation(this);
        node = databaseHandler.getNode(nodeName);

        idTextview.setText(node.getId());
        descriptionTextview.setText(node.getDescription());
        coordinatesTextView.setText(node.getCoordinates());

        picturePath = node.getPicturePath();
        if (picturePath != null) {
            Glide.with(this).load(node.getPicturePath()).into(cameraImageView);
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
