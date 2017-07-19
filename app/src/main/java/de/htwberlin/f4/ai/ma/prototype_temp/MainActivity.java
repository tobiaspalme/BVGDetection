package de.htwberlin.f4.ai.ma.prototype_temp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

//import com.example.carol.bvg.Location;
import com.example.carol.bvg.R;
//import com.example.carol.bvg.RecordActivity;
//import com.example.carol.bvg.SettingsActivity;

import de.htwberlin.f4.ai.ma.prototype_temp.*;

public class MainActivity extends AppCompatActivity {
    String[] permissions;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(!hasPermissions(MainActivity.this, permissions)){
            ActivityCompat.requestPermissions(MainActivity.this, permissions, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }
        else {
            //TODO: Warnmeldung
        }

        final Button buttonSetting = (Button) findViewById(R.id.b_Settings);

        final Button buttonLocation = (Button) findViewById(R.id.b_location);

        final Button buttonRecord = (Button) findViewById(R.id.b_record);

        if (buttonSetting != null) {
            buttonSetting.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivities(new Intent[]{intent});
                }
            });
        }

        if (buttonLocation != null) {
            buttonLocation.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Location.class);
                    startActivities(new Intent[]{intent});
                }
            });
        }

        if (buttonRecord != null) {
            buttonRecord.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                    startActivities(new Intent[]{intent});
                }
            });
        }

    }

    /**
     * check permission
     * @param context
     * @param permissions
     * @return true or false
     */
    private boolean hasPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
