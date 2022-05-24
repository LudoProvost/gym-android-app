package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private static String user;
    EditText username,password;
    Button Login2Button;
    Button registerButton;
    DB_Management myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.usernameLogin);
        password = findViewById(R.id.passwordLogin);
        Login2Button = findViewById(R.id.btn_login);
        registerButton = findViewById(R.id.btn_register);

        myDB = new DB_Management(this);

        Login2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                String pass = password.getText().toString();

                if (user.equals("") || pass.equals("")) {
                    Toast.makeText(LoginActivity.this, "Username or Password cannot be blank.", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean result = myDB.checkUsernameAndPassword(user, pass);
                    if (result) {
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Login.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
            }
        });




    }

    /**
     * getter for the user's username
     * @return String of the user's full username
     */
    public static String getUser() {
        return user;
    }

    /**
     * for quick testing and debugging, allows faster access
     * to app.
     * sets username to admin
     * sets password to admin123
     */
    public void testing() {
        username.setText("admin");
        password.setText("admin123");
    }

    /**
     * creates member user zor with password 1234
     */
    public void createTestAccount() {
        myDB.insertNewUser("zor","1234",true,true);
    }

}