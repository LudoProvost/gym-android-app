package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class RemoveUserActivity extends AppCompatActivity {

    Button backButton;
    Button btn_delete;
    Spinner spinner;
    DB_Management myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_user);

        backButton = findViewById(R.id.backButton3);
        btn_delete = findViewById(R.id.btn_delete);
        spinner = findViewById(R.id.userSpinner);

        loadSpinnerData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
                finish();
            }
        });


    }

    /**
     * deletes the selected user
     */
    public void deleteUser(){
        String username = spinner.getSelectedItem().toString();

        Boolean deleteUser = myDB.deleteUser(username);

        if(deleteUser)
            Toast.makeText(RemoveUserActivity.this, username + " was deleted successfully.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(RemoveUserActivity.this, username + " was not deleted.", Toast.LENGTH_SHORT).show();

    }

    /**
     * Function to load the spinner data from SQLite database
     * */
    private void loadSpinnerData() {
        myDB = new DB_Management(this );
        List<String> labels = myDB.getAllUsers();
        labels.add(0,"");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }
}