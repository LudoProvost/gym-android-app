package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;


import java.util.List;

public class RemoveClassActivity extends AppCompatActivity {

    Button backButton;
    Button classDeleteButton;
    Spinner classDeleteSpinner;
    DB_Management myDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_class);

        backButton = findViewById(R.id.backButton);
        classDeleteButton = findViewById(R.id.classDeleteButton);
        classDeleteSpinner = findViewById(R.id.classDeleteSpinner);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        classDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}