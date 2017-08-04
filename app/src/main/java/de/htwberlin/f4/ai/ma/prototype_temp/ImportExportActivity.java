package de.htwberlin.f4.ai.ma.prototype_temp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.carol.bvg.R;

import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerImplementation;

/**
 * Created by Johann Winter
 */

public class ImportExportActivity extends Activity {

    Button importButton;
    Button exportButton;
    private DatabaseHandler databaseHandler;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        context = getApplicationContext();

        importButton = (Button) findViewById(R.id.import_button);
        exportButton = (Button) findViewById(R.id.export_button);

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

                databaseHandler.exportDatabase();
                Toast.makeText(context, "Datenbank exportiert!",
                        Toast.LENGTH_LONG).show();
            }
        });

    }
}