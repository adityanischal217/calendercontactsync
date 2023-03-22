package com.igp.phonedatasync;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        findViewById(R.id.button1).setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, MainActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.button2).setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, CalenderBirthday.class);
            startActivity(intent);
        });
        findViewById(R.id.button3).setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, NewContactSync.class);
            startActivity(intent);
        });
    }
}