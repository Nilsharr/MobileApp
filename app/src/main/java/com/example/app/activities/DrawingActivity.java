package com.example.app.activities;


import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.views.DrawingSurface;

import java.util.Objects;

import yuku.ambilwarna.AmbilWarnaDialog;


public class DrawingActivity extends AppCompatActivity {
    private DrawingSurface surface;
    private int paintWidth;

    // Creating menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawing_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int itemId = item.getItemId();
        // Returning to previous activity
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        // Changing paint color
        else if (itemId == R.id.changePaintColorMenuButton) {
            AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, surface.getPaintColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {
                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    surface.setPaintColor(color);
                }
            });
            dialog.show();
            return true;
        }
        // Changing background color
        else if (itemId == R.id.changeBackgroundColorMenuButton) {
            AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, surface.getBackgroundColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {
                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    surface.setBackgroundColor(color);
                    surface.refreshBackgroundColor();
                }
            });
            dialog.show();
            return true;
        }
        // Clearing screen
        else if (itemId == R.id.clearScreenMenuButton) {
            surface.clearScreen();
            return true;
        }
        // Changing paint width
        else if (itemId == R.id.changePaintWidthMenuButton) {

            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Set paint width");

            LinearLayout linear = new LinearLayout(this);
            linear.setOrientation(LinearLayout.VERTICAL);

            SeekBar seek = new SeekBar(this);
            seek.setMin(1);
            seek.setProgress(paintWidth = (int) surface.getPaintWidth());
            seek.setPadding(40, 10, 40, 10);

            TextView text = new TextView(this);
            text.setPadding(50, 10, 10, 10);
            text.setText(String.valueOf(paintWidth));

            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    paintWidth = progress;
                    text.setText(String.valueOf(paintWidth));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            linear.addView(seek);
            linear.addView(text);
            alert.setView(linear);

            alert.setPositiveButton("Ok", (dialog, id) -> surface.setPaintWidth(paintWidth));
            alert.setNegativeButton("Cancel", (dialog, id) -> {
            });
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        surface = findViewById(R.id.drawingSurface);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_NO:
                surface.setBackgroundColor(Color.WHITE);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                surface.setBackgroundColor(Color.BLACK);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        surface.resumeDrawing();
    }

    @Override
    protected void onPause() {
        super.onPause();
        surface.pauseDrawing();
    }

}
