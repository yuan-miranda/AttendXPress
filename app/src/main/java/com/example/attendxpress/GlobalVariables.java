package com.example.attendxpress;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GlobalVariables {
    public static String email;

    static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        return dateFormat.format(calendar.getTime());
    }
}