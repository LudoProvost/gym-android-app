package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InstructorActivity extends AppCompatActivity {

    DB_Management myDB;
    String username;
    Button viewScheduledButton;
    Button scheduleNewClassButton;
    Button editClassButton;
    Button instructorDeleteClassButton;
    Button instructorViewMembersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor);

        myDB = new DB_Management(this );
        username = LoginActivity.getUser();

        TextView welcome = findViewById(R.id.textView9);
        welcome.setText("Welcome, " + username);

        viewScheduledButton = findViewById(R.id.viewScheduledClassButton);
        scheduleNewClassButton = findViewById(R.id.scheduleNewClassButton);
        editClassButton = findViewById(R.id.instructorEditClassButton);
        instructorDeleteClassButton = findViewById(R.id.instructorDeleteClassButton);
        instructorViewMembersButton = findViewById(R.id.instructorViewMembersButton);

        viewScheduledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ViewScheduledClassActivity.class);
                startActivity(intent);
            }
        });

        scheduleNewClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ScheduleClassActivity.class);
                startActivity(intent);
            }
        });

        editClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),InstructorEditClassActivity.class);
                startActivity(intent);
            }
        });

        instructorDeleteClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),InstructorDeleteClassActivity.class);
                startActivity(intent);
            }
        });

        instructorViewMembersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),InstructorViewMembersActivity.class);
                startActivity(intent);
            }
        });


    }


}