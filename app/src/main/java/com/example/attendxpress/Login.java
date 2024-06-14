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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        bLogin = findViewById(R.id.bLogin);
        bRegister = findViewById(R.id.bRegister);
        bForgotPassword = findViewById(R.id.bForgotPassword);

        SQLiteDatabase db;
        db = openOrCreateDatabase("AttendXPressDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS attendxpressdb(email VARCHAR, password VARCHAR, name VARCHAR, profile_picture TEXT);");

        bLogin.setOnClickListener(v -> {
            if (inputEmail.length() == 0 || inputPassword.length() == 0) {
                Toast.makeText(this, "Input fields are empty.", Toast.LENGTH_SHORT).show();
                return;
            }
            Cursor verifyEmail = db.rawQuery("SELECT * FROM attendxpressdb WHERE email='" + inputEmail.getText() + "'", null);
            if (verifyEmail.getCount() == 0) {
                Toast.makeText(this, "The email given does not exist on our database.", Toast.LENGTH_SHORT).show();
                return;
            }
            Cursor verifyPassword = db.rawQuery("SELECT * FROM attendxpressdb WHERE password='" + inputPassword.getText() + "'", null);
            if (verifyPassword.getCount() == 0) {
                Toast.makeText(this, "The password given is incorrect.", Toast.LENGTH_SHORT).show();
                return;
            }
            String email = inputEmail.getText().toString();
            Intent i = new Intent(Login.this, Loading.class);
            i.putExtra("email", email);
            startActivity(i);

        });
        bRegister.setOnClickListener(v -> {
            if (inputEmail.length() == 0 || inputPassword.length() == 0) {
                Toast.makeText(this, "Input fields are empty.", Toast.LENGTH_SHORT).show();
                return;
            }
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();

            Cursor verifyEmail = db.rawQuery("SELECT * FROM attendxpressdb WHERE email='" + email + "'", null);
            if (verifyEmail.getCount() > 0) {
                Toast.makeText(this, "The email is already registered.", Toast.LENGTH_SHORT).show();
                return;
            }

            db.execSQL("INSERT INTO attendxpressdb VALUES('" + email + "', '" + password + "', '" + "User" + password + "', NULL);");
            Toast.makeText(this, "Registered successfully.", Toast.LENGTH_SHORT).show();
        });
        bForgotPassword.setOnClickListener(v -> {
            if (inputEmail.length() == 0) {
                Toast.makeText(this, "Input fields are empty.", Toast.LENGTH_SHORT).show();
                return;
            }
            Cursor verifyEmail = db.rawQuery("SELECT * FROM attendxpressdb WHERE email='" + inputEmail.getText() + "'", null);
            if (verifyEmail.getCount() == 0) {
                Toast.makeText(this, "The email given does not exist on our database.", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyEmail.moveToFirst();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Password");
            builder.setMessage(verifyEmail.getString(verifyEmail.getColumnIndex("password")));
            builder.setPositiveButton(android.R.string.ok, ((dialog, which) -> {
                dialog.dismiss();
            }));
            builder.show();
        });

    }
}
