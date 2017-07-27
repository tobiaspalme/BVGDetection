package de.htwberlin.f4.ai.ma.prototype_temp;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.example.carol.bvg.R;

import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;

/**
 * Created by Johann Winter
 */

public class ImportExportActivity extends Activity {

    private Button importButton;
    private Button exportButton;
    private DatabaseHandler databaseHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        importButton = (Button) findViewById(R.id.import_button);
        exportButton = (Button) findViewById(R.id.export_button);

        databaseHandler = new DatabaseHandler(this);

        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Import

            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Export
            }
        });

    }
}
