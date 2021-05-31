package com.example.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.utils.Utilities;

import java.util.HashMap;
import java.util.Objects;


public class GradesFormActivity extends AppCompatActivity {

    private EditText gradesNameInput;
    private EditText gradesSurnameInput;
    private EditText gradesAmountInput;
    private Button gradesProceedButton;
    private TextView meanResultMessage;
    private double meanValue;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Returning to previous activity
        if (item.getItemId() == android.R.id.home) {
            if (meanValue != 0) {
                Toast.makeText(this, meanValue >= 3 ? getString(R.string.message_grades_passed) : getString(R.string.message_grades_failed), Toast.LENGTH_SHORT).show();
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades_form);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        gradesNameInput = findViewById(R.id.gradesNameInput);
        gradesSurnameInput = findViewById(R.id.gradesSurnameInput);
        gradesAmountInput = findViewById(R.id.gradesAmountInput);
        gradesProceedButton = findViewById(R.id.gradesProceedButton);
        meanResultMessage = findViewById(R.id.meanResult);

        // listener set on screen that on screen touch clears focus of current input field and closes keyboard
        findViewById(R.id.gradesFormLayout).setOnTouchListener(((view, event) -> {
            Utilities.clearFocusAndHideKeyboard(view);
            view.performClick();
            return false;
        }));

        // setting listener on input fields
        gradesNameInput.setOnFocusChangeListener(inputListener);
        gradesSurnameInput.setOnFocusChangeListener(inputListener);
        gradesAmountInput.setOnFocusChangeListener(inputListener);

        // listener on done keyboard button that clears focus of current input field and closes keyboard
        gradesAmountInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Utilities.clearFocusAndHideKeyboard(view);
            }
            return false;
        });

        // on click listener that if entered data is valid
        // launches new activity and passes to it value of grades to display
        // else clears focus and closes keyboard
        gradesProceedButton.setOnClickListener(view -> {
            // if value of mean haven't been calculated yet
            if (meanValue == 0) {
                if (isAllInputValid()) {
                    Intent intent = new Intent(GradesFormActivity.this, GradesSelectionActivity.class);
                    intent.putExtra("gradesAmount", Integer.parseInt(gradesAmountInput.getText().toString()));
                    startActivityForResult(intent, 1);
                } else {
                    Utilities.clearFocusAndHideKeyboard(findViewById(R.id.gradesFormLayout));
                }
            }
            // if value of mean was calculated
            // send message based on value of mean and finish activity
            else {
                Toast.makeText(this, meanValue >= 3 ? getString(R.string.message_grades_passed) : getString(R.string.message_grades_failed), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // checking if given word is invalid
    private static boolean isInvalidWord(String input) {
        // check if string is null or empty
        if (input == null || input.trim().isEmpty()) {
            return true;
        }
        // require to start with uppercase letter (word is invalid if it doesn't start with uppercase letter)
        if (!Character.isUpperCase(input.codePointAt(0))) {
            return true;
        }
        // check if string matches pattern (string must start with letter, then it might consist of letters, space, dash and apostrophe)
        // if string doesn't match pattern it's invalid
        return !input.matches("^[\\p{L}][\\p{L} \\-']*$");
    }

    // method that checks if all inputs are correct
    private boolean isAllInputValid() {
        if (TextUtils.isEmpty(gradesNameInput.getText()) || TextUtils.isEmpty(gradesSurnameInput.getText()) || TextUtils.isEmpty(gradesAmountInput.getText())) {
            return false;
        }
        if (isInvalidWord(gradesNameInput.getText().toString()) || isInvalidWord(gradesSurnameInput.getText().toString())) {
            return false;
        }
        int gradesNumber = Integer.parseInt(gradesAmountInput.getText().toString());
        return gradesNumber >= 5 && gradesNumber <= 15;
    }

    // listener that listens for focus change on chosen input fields
    private final View.OnFocusChangeListener inputListener = (view, hasFocus) -> {
        // if current input field lost focus and every chosen input field contains valid data
        // gradesProceedButton is set to Visible and listener ends listening
        if (!hasFocus && isAllInputValid()) {
            gradesProceedButton.setVisibility(View.VISIBLE);
            return;
        }

        // when current input field loses focus
        if (!hasFocus) {
            // different handling for numeric field
            if (view.getId() == gradesAmountInput.getId()) {
                // if gradesAmountInput field is empty
                if (TextUtils.isEmpty(gradesAmountInput.getText())) {
                    // setting appropriate error message
                    gradesAmountInput.setError(getString(R.string.error_grades_amount_input_empty));
                    // setting button to Invisible
                    gradesProceedButton.setVisibility(View.INVISIBLE);
                } else {
                    // checking if input in gradesAmount is in range of [5;15]
                    int gradesNumber = Integer.parseInt(gradesAmountInput.getText().toString());
                    if (gradesNumber < 5 || gradesNumber > 15) {
                        // setting appropriate error message
                        gradesAmountInput.setError(getString(R.string.error_grades_amount_input_invalid_range));
                        // setting button to Invisible
                        gradesProceedButton.setVisibility(View.INVISIBLE);
                    }
                }
            }
            // handling text fields
            else {
                // if entered word isn't valid
                if (isInvalidWord(((EditText) view).getText().toString())) {
                    // error messages when input is incorrect for selected fields
                    HashMap<Integer, String> errorMessage = new HashMap<Integer, String>() {{
                        put(R.id.gradesNameInput, getString(R.string.error_grades_name_input_invalid));
                        put(R.id.gradesSurnameInput, getString(R.string.error_grades_surname_input_invalid));
                    }};

                    // setting appropriate error message
                    ((EditText) view).setError(errorMessage.get(view.getId()));
                    // setting button to Invisible
                    gradesProceedButton.setVisibility(View.INVISIBLE);
                }
            }
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putDouble("meanValue", meanValue);
        outState.putInt("proceedButtonVisibility", gradesProceedButton.getVisibility());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        meanValue = savedInstanceState.getDouble("meanValue");
        gradesProceedButton.setVisibility(savedInstanceState.getInt("proceedButtonVisibility"));

        // if value of mean was calculated
        if (meanValue != 0) {
            // setting input fields to not enabled
            gradesNameInput.setEnabled(false);
            gradesSurnameInput.setEnabled(false);
            gradesAmountInput.setEnabled(false);
            // setting text and visibility of message
            meanResultMessage.setText(String.format(getString(R.string.message_mean_result), meanValue));
            meanResultMessage.setVisibility(View.VISIBLE);
            // setting text of button based on value of mean
            gradesProceedButton.setText(meanValue >= 3 ? R.string.button_grades_passed : R.string.button_grades_failed);
        }
    }

    // getting the value of mean from given grades from started activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                // returned value of mean
                meanValue = data.getDoubleExtra("meanValue", 0);
                // setting input fields to not enabled
                gradesNameInput.setEnabled(false);
                gradesSurnameInput.setEnabled(false);
                gradesAmountInput.setEnabled(false);

                // setting text and visibility of message
                meanResultMessage.setText(String.format(getString(R.string.message_mean_result), meanValue));
                meanResultMessage.setVisibility(View.VISIBLE);

                // setting text of button based on value of mean
                gradesProceedButton.setText(meanValue >= 3 ? R.string.button_grades_passed : R.string.button_grades_failed);
            }
        }
    }
}