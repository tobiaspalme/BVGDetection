package de.htwberlin.f4.ai.ma.prototype_temp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;

public class MaxPictureActivity extends Activity {

    ImageView maxImageView;
    //File sdCard = Environment.getExternalStorageDirectory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maximize_picture);

        maxImageView = (ImageView) findViewById(R.id.maxImageView);

        Intent intent = getIntent();
        //String nodeName = intent.getExtras().get("nodeName").toString();
        String picturePath = intent.getExtras().get("picturePath").toString();

        //Glide.with(this).load(node.getPicturePath()).into(maxImageView);
        Glide.with(this).load(picturePath).into(maxImageView);
    }
}
