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

public class MaxPictureActivity extends Activity {

    ImageView maxImageView;
    File sdCard = Environment.getExternalStorageDirectory();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.max_picture_activity);

        maxImageView = (ImageView) findViewById(R.id.maxImageView);

        Intent intent = getIntent();
        String pictureName = intent.getExtras().get("pictureName").toString();

        String filePath = sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/Node_" + pictureName + ".jpg";
        Glide.with(this).load(filePath).into(maxImageView);
    }
}
