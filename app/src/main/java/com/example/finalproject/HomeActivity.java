package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    Button selectAdmin;
    Button selectMember;
    Button selectInstructor;
    TextView userTextView;
    DB_Management myDB;
    static boolean finalRole = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        myDB = new DB_Management(this );


        selectAdmin = findViewById(R.id.selectAdmin);
        selectInstructor = findViewById(R.id.selectInstructor);
        selectMember = findViewById(R.id.selectMember);

        userTextView = findViewById(R.id.userTextView);

        selectAdmin.setEnabled(false);
        selectInstructor.setEnabled(false);
        selectMember.setEnabled(false);


        String username = LoginActivity.getUser();

        userTextView.setText(username);

        String[] role = myDB.getUserRoles(username);
        for(int i = 0; i < role.length ; i++){
            if(role[i] != null) {
                if (role[i].contains("1")) {
                    selectAdmin.setEnabled(true);
                }
                if (role[i].contains("2")) {
                    selectInstructor.setEnabled(true);
                }
                if (role[i].contains("3")) {
                    selectMember.setEnabled(true);
                }
            }
        }

        selectMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalRole = true;
                Intent intent = new Intent(getApplicationContext(),MemberActivity.class);
                startActivity(intent);
            }
        });

        selectInstructor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalRole = false;
                Intent intent = new Intent(getApplicationContext(),InstructorActivity.class);
                startActivity(intent);
            }
        });

        selectAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AdminActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * getter for the role of the user
     * @return boolean variable:
     * returns true if user selects member
     * returns false if user selects instructor
     */
    public static boolean getRole() {
        return finalRole;
    }

}