package de.htwberlin.f4.ai.ma;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;

import de.htwberlin.f4.ai.ma.android.BaseActivity;

public class MaxPictureActivity extends BaseActivity {

    ImageView maxImageView;
    //File sdCard = Environment.getExternalStorageDirectory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_maximize_picture, contentFrameLayout);

        maxImageView = (ImageView) findViewById(R.id.maxImageView);

        Intent intent = getIntent();
        String picturePath = (String) intent.getExtras().get("picturePath");

        if (picturePath != null) {
            Glide.with(this).load(picturePath).into(maxImageView);
        } else {
            Glide.with(this).load(R.drawable.unknown).into(maxImageView);
        }
    }
}
