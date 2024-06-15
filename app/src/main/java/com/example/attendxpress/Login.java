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

        AttendXPressDB.execSQL("CREATE TABLE IF NOT EXISTS admin(email VARCHAR, password VARCHAR, name VARCHAR, profile_picture TEXT);");
        Cursor adminCursor = AttendXPressDB.rawQuery("SELECT COUNT(*) FROM admin WHERE email = 'admin@gmail.com'", null);
        if (adminCursor.moveToFirst()) {
            int count = adminCursor.getInt(0);
            if (count == 0) {
                AttendXPressDB.execSQL("INSERT INTO admin VALUES('admin@gmail.com', 'admin', 'Admin', NULL);");
            }
        }

        bLogin.setOnClickListener(v -> {
            if (inputEmail.length() == 0 || inputPassword.length() == 0) {
                Toast.makeText(this, "Input fields are empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (inputEmail.getText().toString().equals("admin")) {
                Intent i = new Intent(Login.this, AdminPanel.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
            else {
                String email = inputEmail.getText().toString();
                GlobalVariables.email = email;

                if (!GlobalVariables.isTableExists(AttendXPressDB, GlobalVariables.email)) {
                    Toast.makeText(this, "The email given does not exist on our database.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Cursor verifyPassword = AttendXPressDB.rawQuery("SELECT * FROM " + GlobalVariables.email + " WHERE password='" + inputPassword.getText() + "'", null);

                if (verifyPassword.getCount() == 0) {
                    Toast.makeText(this, "The password given is incorrect.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i = new Intent(Login.this, Loading.class);
                startActivity(i);
            }

        });
        bRegister.setOnClickListener(v -> {
            if (inputEmail.length() == 0 || inputPassword.length() == 0) {
                Toast.makeText(this, "Input fields are empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (inputEmail.getText().toString().equals("admin")) {
                Toast.makeText(this, "Admin account be registered.", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = inputEmail.getText().toString();
            GlobalVariables.email = email;
            String password = inputPassword.getText().toString();

            if (email.contains(" ") || password.contains(" ")) {
                Toast.makeText(this, "Input fields cannot contain spaces.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (inputEmail.getText().toString().equals("admin") || GlobalVariables.isTableExists(AttendXPressDB, GlobalVariables.email)) {
                Toast.makeText(this, "The email is already registered.", Toast.LENGTH_SHORT).show();
                return;
            }

            AttendXPressDB.execSQL("CREATE TABLE IF NOT EXISTS " + GlobalVariables.email + "(email VARCHAR, password VARCHAR, name VARCHAR, profile_picture TEXT);");
            AttendXPressDB.execSQL("INSERT INTO " + GlobalVariables.email + " VALUES('" + email + "', '" + password + "', '" + "User" + password + "', NULL);");
            Toast.makeText(this, "Registered successfully.", Toast.LENGTH_SHORT).show();
        });
        bForgotPassword.setOnClickListener(v -> {
            if (inputEmail.length() == 0) {
                Toast.makeText(this, "Input fields are empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (inputEmail.getText().toString().equals("admin")) {
                Toast.makeText(this, "Admin password cannot be viewed.", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = inputEmail.getText().toString();

            if (!GlobalVariables.isTableExists(AttendXPressDB, email)) {
                Toast.makeText(this, "The email given does not exist on our database.", Toast.LENGTH_SHORT).show();
                return;
            }

            GlobalVariables.email = "";
            Cursor c = AttendXPressDB.rawQuery("SELECT * FROM " + email + " WHERE email='" + inputEmail.getText() + "'", null);
            c.moveToFirst();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Password");
            builder.setMessage(c.getString(c.getColumnIndex("password")));
            builder.setPositiveButton(android.R.string.ok, ((dialog, which) -> dialog.dismiss()));
            builder.show();
        });
    }
}
