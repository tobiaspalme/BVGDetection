package de.htwberlin.f4.ai.ma.prototype_temp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;

import java.io.File;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.NodeFactory;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;

public class MaxPictureActivity extends Activity {

    ImageView maxImageView;
    File sdCard = Environment.getExternalStorageDirectory();
    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.max_picture_activity);

        maxImageView = (ImageView) findViewById(R.id.maxImageView);

        databaseHandler = new DatabaseHandler(this);

        Intent intent = getIntent();
        //String nodeName = intent.getExtras().get("nodeName").toString();
        String picturePath = intent.getExtras().get("picturePath").toString();


        //Node node = databaseHandler.getNode(nodeName);

        //Glide.with(this).load(node.getPicturePath()).into(maxImageView);
        Glide.with(this).load(picturePath).into(maxImageView);
    }
}
