package de.htwberlin.f4.ai.ma.prototype_temp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.carol.bvg.R;

import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerImplementation;

/**
 * Created by Johann Winter
 */

public class ImportExportActivity extends Activity {

    private Button importButton;
    private Button exportButton;
    //private DatabaseHandlerImplementation databaseHandlerImplementation;
    private DatabaseHandler databaseHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        importButton = (Button) findViewById(R.id.import_button);
        exportButton = (Button) findViewById(R.id.export_button);

        //databaseHandlerImplementation = new DatabaseHandlerImplementation(this);
        databaseHandler = new DatabaseHandlerImplementation(this);


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
