package com.example.app.drawing_app.activities;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.utils.Constants;
import com.example.app.drawing_app.views.DrawingSurface;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import yuku.ambilwarna.AmbilWarnaDialog;


public class DrawingActivity extends AppCompatActivity {
    private DrawingSurface surface;

    // Creating menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawing_activity_menu, menu);
        return true;
    }

    // Handle menu item selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        // Clearing screen
        else if (itemId == R.id.clearScreenMenuButton) {
            surface.clearScreen();
            return true;
        }
        // Changing paint width with seekbar
        else if (itemId == R.id.changePaintWidthMenuButton) {

            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(getString(R.string.dialog_item_set_paint_width));

            LinearLayout linear = new LinearLayout(this);
            linear.setOrientation(LinearLayout.VERTICAL);

            SeekBar seek = new SeekBar(this);
            seek.setMin(1);
            seek.setMax(50);
            seek.setProgress(surface.getPaintWidth());
            seek.setPadding(40, 10, 40, 10);

            TextView text = new TextView(this);
            text.setText(String.valueOf(surface.getPaintWidth()));
            text.setPadding(50, 10, 10, 10);

            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    text.setText(String.valueOf(progress));
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

            alert.setPositiveButton(getString(R.string.information_ok), (dialog, id) -> surface.setPaintWidth(seek.getProgress()));
            alert.setNegativeButton(getString(R.string.information_cancel), (dialog, id) -> {
            });
            alert.show();
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
                    surface.changeBackgroundColor(color);
                }
            });
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        surface = findViewById(R.id.drawingSurface);

        // creating home button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // compressing drawingBitmap to byte array and saving to bundle
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        surface.getDrawingBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] compDrawingBitmap = stream.toByteArray();
        outState.putByteArray(Constants.SAVED_COMPRESSED_DRAWING_BITMAP, compDrawingBitmap);

        // compressing backgroundBitmap to byte array and saving to bundle
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        surface.getBackgroundBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream2);
        byte[] compBackgroundBitmap = stream2.toByteArray();
        outState.putByteArray(Constants.SAVED_COMPRESSED_BACKGROUND_BITMAP, compBackgroundBitmap);

        outState.putInt(Constants.SAVED_PAINT_COLOR, surface.getPaintColor());
        outState.putInt(Constants.SAVED_PAINT_WIDTH, surface.getPaintWidth());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        surface.setDrawingBitmap(savedInstanceState.getByteArray(Constants.SAVED_COMPRESSED_DRAWING_BITMAP));
        surface.setBackgroundBitmap(savedInstanceState.getByteArray(Constants.SAVED_COMPRESSED_BACKGROUND_BITMAP));
        surface.setPaintColor(savedInstanceState.getInt(Constants.SAVED_PAINT_COLOR));
        surface.setPaintWidth(savedInstanceState.getInt(Constants.SAVED_PAINT_WIDTH));
    }
}