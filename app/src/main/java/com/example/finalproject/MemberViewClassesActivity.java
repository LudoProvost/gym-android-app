package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MemberViewClassesActivity extends AppCompatActivity {

    DB_Management myDB;
    Button unenrollButton;
    String username;
    Spinner enrolledClassSpinner;
    EditText commentEdit;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_view_classes);

        myDB = new DB_Management(this);
        unenrollButton = findViewById(R.id.unenrollButton);
        enrolledClassSpinner = findViewById(R.id.enrolledClassSpinner);
        commentEdit = findViewById(R.id.classCommentEditText);
        backButton = findViewById(R.id.backButton99);

        username = LoginActivity.getUser();
        loadEnrolledClassSpinnerData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        unenrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                areYouSureAlert();
            }
        });

    }

    /**
     *  lol
     */
    private void processComment() {
        // we don't care about their comments B)

        String comment = commentEdit.getText().toString();
        comment = "";
        commentEdit.setText(comment);
    }

    /**
     * makes an alert pop on the screen to prompt the user
     */
    private void areYouSureAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setTitle("You are about to cancel the selected class.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String selectedItem = enrolledClassSpinner.getSelectedItem().toString();
                        int classId = Integer.parseInt(selectedItem.split(" ")[0]);
                        myDB.unenrolUser(username, classId);
                        processComment();
                        Toast.makeText(MemberViewClassesActivity.this, "Unenrolled form class successfully.", Toast.LENGTH_SHORT).show();
                        finish();
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

    /**
     * makes an alert pop on the screen to prompt the user
     */
    private void noClassAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("")
                .setTitle("You are currently enrolled in 0 classes.")
                .setPositiveButton("Go back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("Enroll to a class", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        List<String> scheduledClasses = myDB.getAllScheduledClassesWithID();
                        if (!scheduledClasses.isEmpty()) {
                            finish();
                            Intent intent = new Intent(getApplicationContext(),MemberSearchClassesActivity.class);
                            startActivity(intent);
                        } else {
                            finish();
                        }
                    }
                });
        builder.create();
        builder.show();
    }

    /**
     * loads spinner data from SQL database
     */
    private void loadEnrolledClassSpinnerData() {

        List<String> enrolledClasses = myDB.getAllClassesByEnrolment(username);
        List<String> labels = new ArrayList<>();

        if (enrolledClasses.isEmpty()) {
            noClassAlert();
            return;
        }

        for (String s : enrolledClasses) {
            labels.add(s+" "+myDB.getClassByClassId(s));
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        enrolledClassSpinner.setAdapter(dataAdapter);
    }

}