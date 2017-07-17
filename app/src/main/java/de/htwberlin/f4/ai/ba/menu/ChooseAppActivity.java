package de.htwberlin.f4.ai.ba.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.carol.bvg.MainActivity;
import com.example.carol.bvg.R;

import de.htwberlin.f4.ai.ba.coordinates.android.CoordinatesActivity;

public class ChooseAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_app);



        Button btnCarola = (Button) findViewById(R.id.btn_carola);
        btnCarola.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Carola", Toast.LENGTH_SHORT);
                toast.show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });

        Button btnJohann = (Button) findViewById(R.id.btn_johann);
        btnJohann.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Johann", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        Button btnBenjamin = (Button) findViewById(R.id.btn_benjamin);
        btnBenjamin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Benjamin", Toast.LENGTH_SHORT);
                toast.show();

                Intent intent = new Intent(getApplicationContext(), CoordinatesActivity.class);
                startActivity(intent);
            }
        });


    }



}
