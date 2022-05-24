package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class EditUserActivity extends AppCompatActivity {

    Button backButton;
    Button btn_saveChanges;
    CheckBox isMemberBox;
    CheckBox isInstructorBox;

    Spinner spinner;
    DB_Management myDB;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        backButton = findViewById(R.id.backButton2);
        btn_saveChanges = findViewById(R.id.btn_saveChanges);
        isMemberBox = findViewById(R.id.isMemberBox);
        isInstructorBox = findViewById(R.id.isInstructorBox);

        spinner = findViewById(R.id.userSpinner);

        loadSpinnerData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myDB.editUserRoles(username, isInstructorBox.isChecked(), isMemberBox.isChecked())){
                    Toast.makeText(EditUserActivity.this, username + " was edited successfully.", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(EditUserActivity.this, username + " was not updated.", Toast.LENGTH_SHORT).show();
                }


            }
        });



        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        Object item = parent.getItemAtPosition(pos);

                        username = item.toString();

                        String[] roles = myDB.getUserRoles(username);

                        isInstructorBox.setChecked(false);
                        isMemberBox.setChecked(false);

                        for(int i = 0;i<roles.length; i++){
                            if(roles[i] != null) {
                                if (roles[i].contains("2")) {
                                    isInstructorBox.setChecked(true);
                                }

                                if (roles[i].contains("3")) {
                                    isMemberBox.setChecked(true);
                                }
                            }
                        }

                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }


    /**
     * loads spinner data from SQLite database
     * */
    private void loadSpinnerData() {
        myDB = new DB_Management(this );
        List<String> labels = myDB.getAllUsers();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }
}