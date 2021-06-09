package com.example.app.grades_average_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.grades_average_app.adapters.GradesAdapter;

import java.util.Arrays;
import java.util.Objects;

public class GradesSelectionActivity extends AppCompatActivity {

    private static final String SAVED_GRADES_ARRAY_STATE = "com.example.app.grades_average_app.activities.savedGradesArrayState";

    // array that saves value of checked grade
    private int[] gradesArray;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Returning to previous activity
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades_selection);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            gradesArray = savedInstanceState.getIntArray(SAVED_GRADES_ARRAY_STATE);
        } else {
            // initializing array to size of value (gradesAmountInput) received from previous activity
            gradesArray = new int[getIntent().getIntExtra(GradesFormActivity.GRADES_AMOUNT, 0)];
            // setting initial values to 2
            Arrays.fill(gradesArray, 0);
        }
        // creating recyclerview
        RecyclerView gradesRecyclerView = findViewById(R.id.gradesList);
        // dividing rows with divider lines
        gradesRecyclerView.addItemDecoration(new DividerItemDecoration(gradesRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        // setting adapter
        gradesRecyclerView.setAdapter(new GradesAdapter(GradesSelectionActivity.this, gradesArray));
        // setting layout manager to linear
        gradesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // listener on readyButton that on click
        // if all grades are checked
        // returns value of mean to parent activity and finishes this activity
        // else notifies user that there are not checked grades
        findViewById(R.id.gradesReadyButton).setOnClickListener(view -> {
            // checking if all grades are checked by the user
            if (Arrays.stream(gradesArray).anyMatch(x -> x == 0)) {
                Toast.makeText(this, getString(R.string.error_grades_not_checked), Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent();
            intent.putExtra(GradesFormActivity.MEAN_VALUE_FROM_ACTIVITY_RESULT, countMean());
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
    }

    // method to calculate mean of given grades
    private double countMean() {
        return Arrays.stream(gradesArray).average().orElse(Double.NaN);
    }

    // handling screen orientation change
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(SAVED_GRADES_ARRAY_STATE, gradesArray);
    }
}