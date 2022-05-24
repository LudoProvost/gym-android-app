package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InstructorDeleteClassActivity extends AppCompatActivity {

    Button backButton, classCancelButton;
    DB_Management myDB;
    String username;
    Spinner classSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_delete_class);
        backButton = findViewById(R.id.backButton7);
        classCancelButton = findViewById(R.id.classCancelButton);
        username = LoginActivity.getUser();
        classSpinner = findViewById(R.id.classSpinner3);

        loadSpinnerData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        classCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                areYouSureAlert();
            }
        });

    }

    /**
     * cancels a class and creates toast indicating if the class
     * was successfully or unsuccessfully cancelled
     */
    public void cancelClass() {
        int classID = Integer.parseInt(classSpinner.getSelectedItem().toString().split(" ")[1]);

        Boolean cancelClass = myDB.deleteClass(classID);

        if(cancelClass) {
            Toast.makeText(InstructorDeleteClassActivity.this, "Class was cancelled successfully.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(InstructorDeleteClassActivity.this, "Class was not cancelled.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * loads spinner data
     */
    private void loadSpinnerData() {

        myDB = new DB_Management(this );
        List<String> temp = myDB.getAllClassesByInstructorName(username);
        List<String> labels = new ArrayList<>();

        if (temp.get(0) == null) {
            noClassAlert();
            return;
        }

        for (String classInfo : temp) {
            List<String> splitClass = Arrays.asList(classInfo.split(" "));
            String shortenedLevel = (splitClass.get(2).substring(0,3)+".");
            labels.add("ID: "+splitClass.get(0)+" "+splitClass.get(1) +" "+shortenedLevel+" "+splitClass.get(3)+" at "+splitClass.get(4));
        }

        if (labels.isEmpty()) {
            noClassAlert();
            return;
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        classSpinner.setAdapter(dataAdapter);

    }

    /**
     * makes an alert pop on the screen to prompt the user
     */
    private void noClassAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("")
                .setTitle("You currently have no classes.")
                .setPositiveButton("Go back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("Add a class", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        Intent intent = new Intent(getApplicationContext(),ScheduleClassActivity.class);
                        startActivity(intent);
                    }
                });
        builder.create();
        builder.show();
    }

    /**
     * makes an alert pop on the screen to prompt the user
     */
    private void areYouSureAlert() {
        String[] temp = classSpinner.getSelectedItem().toString().split(" ");
        String msg = ("ID: "+temp[1]+" "+temp[2]+" "+temp[3]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setTitle("You are about to cancel the following class: "+msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cancelClass();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
        builder.create();
        builder.show();
    }

}