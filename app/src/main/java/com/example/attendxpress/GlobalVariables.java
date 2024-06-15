package com.example.attendxpress;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GlobalVariables {
    public static String email = "";

    static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        return dateFormat.format(calendar.getTime());
    }

    static boolean isTableExists(SQLiteDatabase db, String tableName) {
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'", null);
        boolean tableExists = c.getCount() > 0;
        c.close();
        return tableExists;
    }
}
