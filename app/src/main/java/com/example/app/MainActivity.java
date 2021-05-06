package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.activities.GradesFormActivity;
import com.example.app.activities.PhonesDatabaseBrowsingActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.gradesAverageAppButton).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, GradesFormActivity.class)));

        findViewById(R.id.phonesDatabaseAppButton).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PhonesDatabaseBrowsingActivity.class)));

        findViewById(R.id.placeholder1Button).setOnClickListener(view -> {
            Toast.makeText(this, "Someday there will be something here", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(MainActivity.this, Placeholder1.class));
        });

        findViewById(R.id.placeholder2Button).setOnClickListener(view -> {
            Toast.makeText(this, "Someday there will be something here", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(MainActivity.this, Placeholder2.class));
        });
    }
}
