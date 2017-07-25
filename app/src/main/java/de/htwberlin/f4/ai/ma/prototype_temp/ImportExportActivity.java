package de.htwberlin.f4.ai.ma.prototype_temp;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.example.carol.bvg.R;

/**
 * Created by Johann Winter
 */

public class ImportExportActivity extends Activity {

    Button importButton;
    Button exportButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        importButton = (Button) findViewById(R.id.import_button);
        exportButton = (Button) findViewById(R.id.export_button);

    }
}
