package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.List;

public class EditClassActivity extends AppCompatActivity {

    EditText newName, newDescription;
    Button backButton;
    Button addChangesButton;
    Spinner spinner;
    DB_Management myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        newName = findViewById(R.id.newNameEditText);
        newDescription = findViewById(R.id.newDescriptionEditText);
        backButton = findViewById(R.id.backButton);
        addChangesButton = findViewById(R.id.addChangesButton);
        spinner = findViewById(R.id.classSpinner);
        myDB = new DB_Management(this);

        loadSpinnerData();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String selectedItemName = "";
                int index = 0;
                List<String> allClassTypes = myDB.getAllClassTypes();

                if (spinner.getSelectedItem().toString().length() == 0) {
                    newName.setText("");
                    newDescription.setText("");
                    return;
                }

                for (String classType : allClassTypes) {
                    if (classType.contains(spinner.getSelectedItem().toString())) {
                        selectedItemName = classType;
                        break;
                    }
                    index++;
                }

                List<String> allClassDescriptions = myDB.getAllClassDescriptions();

                newName.setText(selectedItemName);
                newDescription.setText(allClassDescriptions.get(index));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        addChangesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = newName.getText().toString();
                String description = newDescription.getText().toString();

                if ((name.length() == 0) && (description.length() == 0)) {
                    Toast.makeText(EditClassActivity.this, "Please enter a name and a description.", Toast.LENGTH_SHORT).show();
                    newName.setHintTextColor(Color.RED);
                    newDescription.setHintTextColor(Color.RED);
                    return;
                } else if (description.length() == 0) {
                    Toast.makeText(EditClassActivity.this, "Please enter a description.", Toast.LENGTH_SHORT).show();
                    newDescription.setHintTextColor(Color.RED);
                    return;
                } else if (name.length() == 0) {
                    Toast.makeText(EditClassActivity.this, "Please enter a name.", Toast.LENGTH_SHORT).show();
                    newName.setHintTextColor(Color.RED);
                    return;
                }

                String oldClassType = spinner.getSelectedItem().toString();

                Boolean editClassType = myDB.editClassType(oldClassType,name,description);

                if(editClassType) {
                    Toast.makeText(EditClassActivity.this, "Changes were successful.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditClassActivity.this, "Could not save changes.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * loads spinner data from the SQL database
     */
    private void loadSpinnerData() {
        List<String> labels = myDB.getAllClassTypes();
        labels.add(0,"");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }
}