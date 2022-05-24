package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class RemoveClassTypeActivity extends AppCompatActivity {

    Button backButton;
    Button classTypeDeleteButton;
    Spinner classTypeDeleteSpinner;
    DB_Management myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_class_type);

        backButton = findViewById(R.id.backButton);
        classTypeDeleteButton = findViewById(R.id.classTypeDeleteButton);
        classTypeDeleteSpinner = findViewById(R.id.classTypeDeleteSpinner);

        loadSpinnerData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        classTypeDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClassType();
            }
        });
    }

    /**
     * deletes the selected class type from the SQL database
     */
    public void deleteClassType(){
        String class_type = classTypeDeleteSpinner.getSelectedItem().toString();

        Boolean deleteClassType = myDB.deleteClassType(class_type);

        if(deleteClassType) {
            Toast.makeText(RemoveClassTypeActivity.this, class_type + " was deleted successfully.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(RemoveClassTypeActivity.this, class_type + " was not deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * loads spinner data from SQL database
     */
    private void loadSpinnerData() {

        myDB = new DB_Management(this );
        List<String> labels = myDB.getAllClassTypes();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        classTypeDeleteSpinner.setAdapter(dataAdapter);

    }

}
