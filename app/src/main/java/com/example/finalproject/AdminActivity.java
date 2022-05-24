package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminActivity extends AppCompatActivity {

    Button addUserButton;
    Button removeUserButton;
    Button editUserButton;
    Button addClassButton;

    Button editClassButton;
    Button removeClassTypeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        addUserButton = findViewById(R.id.addUserButton);
        removeUserButton = findViewById(R.id.removeUserButton);
        editUserButton = findViewById(R.id.editUserButton);
        addClassButton = findViewById(R.id.addClassButton);

        addUserButton.setEnabled(false);

        editClassButton = findViewById(R.id.editClassButton);
        removeClassTypeButton = findViewById(R.id.removeClassTypeButton);

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddUserActivity.class);
                startActivity(intent);
            }
        });

        removeUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RemoveUserActivity.class);
                startActivity(intent);
            }
        });

        editUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),EditUserActivity.class);
                startActivity(intent);
            }
        });

        addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddClassActivity.class);
                startActivity(intent);
            }
        });


        editClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),EditClassActivity.class);
                startActivity(intent);
            }
        });

        removeClassTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RemoveClassTypeActivity.class);
                startActivity(intent);
            }
        });
    }

}