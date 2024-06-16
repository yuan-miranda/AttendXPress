package com.example.attendxpress;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {
    EditText inputEmail;
    EditText inputPassword;
    Button bLogin;
    Button bRegister;
    Button bForgotPassword;
    SQLiteDatabase AttendXPressDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        bLogin = findViewById(R.id.bLogin);
        bRegister = findViewById(R.id.bRegister);
        bForgotPassword = findViewById(R.id.bForgotPassword);

        AttendXPressDB = openOrCreateDatabase("AttendXPressDB", Context.MODE_PRIVATE, null);
        AttendXPressDB.execSQL("CREATE TABLE IF NOT EXISTS users(email VARCHAR PRIMARY KEY, password VARCHAR, name VARCHAR, profile_picture TEXT);");

        // add admin account.
        Cursor addAdmin = AttendXPressDB.rawQuery("SELECT * FROM users WHERE email=?", new String[]{"admin@gmail.com"});
        if (addAdmin.getCount() == 0) {
            AttendXPressDB.execSQL("INSERT INTO users VALUES('admin@gmail.com', 'admin', 'Admin', NULL);");
        }
        addAdmin.close();

        bLogin.setOnClickListener(v -> {
            if (inputEmail.length() == 0 || inputPassword.length() == 0) {
                Toast.makeText(this, "Input fields are empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (inputEmail.getText().toString().equals("admin@gmail.com")) {
                Intent i = new Intent(Login.this, AdminPanel.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
            else {
                // check for user email if it is registered.
                String email = inputEmail.getText().toString();
                Cursor findUserEmail = AttendXPressDB.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});
                if (findUserEmail.getCount() == 0) {
                    Toast.makeText(this, "The email given does not exist on our database.", Toast.LENGTH_SHORT).show();
                    findUserEmail.close();
                    return;
                }
                findUserEmail.close();

                // check if the user input password is correct.
                String password = inputPassword.getText().toString();
                Cursor checkUserPassword = AttendXPressDB.rawQuery("SELECT * FROM users WHERE email=? AND password=?", new String[]{email, password});
                if (checkUserPassword.getCount() == 0) {
                    Toast.makeText(this, "The password given is incorrect.", Toast.LENGTH_SHORT).show();
                    checkUserPassword.close();
                    return;
                }
                checkUserPassword.close();

                GlobalVariables.email = email;
                Intent i = new Intent(Login.this, Loading.class);
                startActivity(i);
            }
        });
        bRegister.setOnClickListener(v -> {
            if (inputEmail.length() == 0 || inputPassword.length() == 0) {
                Toast.makeText(this, "Input fields are empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (inputEmail.getText().toString().equals("admin@gmail.com")) {
                Toast.makeText(this, "Admin account cannot be registered.", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = inputEmail.getText().toString();
            GlobalVariables.email = email;
            String password = inputPassword.getText().toString();

            // change this to handle valid email format instead later.
            if (email.contains(" ") || password.contains(" ")) {
                Toast.makeText(this, "Input fields cannot contain spaces.", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor checkUserExists = AttendXPressDB.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});
            if (checkUserExists.getCount() != 0) {
                Toast.makeText(this, "The email is already registered.", Toast.LENGTH_SHORT).show();
                checkUserExists.close();
                return;
            }
            checkUserExists.close();

            AttendXPressDB.execSQL("INSERT INTO users VALUES(?, ?, ?, ?)", new Object[]{email, password, "user" + password, null});
            Toast.makeText(this, "Registered successfully.", Toast.LENGTH_SHORT).show();
        });
        bForgotPassword.setOnClickListener(v -> {
            if (inputEmail.length() == 0) {
                Toast.makeText(this, "Input fields are empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (inputEmail.getText().toString().equals("admin@gmail.com")) {
                Toast.makeText(this, "Admin password cannot be viewed.", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = inputEmail.getText().toString();

            // check if the user exist in the database before getting the password.
            Cursor checkUserExists = AttendXPressDB.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});
            if (checkUserExists.getCount() == 0) {
                Toast.makeText(this, "The email given does not exist on our database.", Toast.LENGTH_SHORT).show();
                checkUserExists.close();
                return;
            }
            checkUserExists.close();

            GlobalVariables.email = "";

            // get the user password.
            Cursor getUserPassword = AttendXPressDB.rawQuery("SELECT password FROM users WHERE email=?", new String[]{email});
            if (getUserPassword.moveToFirst()) {
                String password = getUserPassword.getString(getUserPassword.getColumnIndex("password"));
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Password");
                builder.setMessage(password);
                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
                builder.show();
            } else {
                Toast.makeText(this, "Failed to retrieve password.", Toast.LENGTH_SHORT).show();
            }
            getUserPassword.close();
        });
    }
}
