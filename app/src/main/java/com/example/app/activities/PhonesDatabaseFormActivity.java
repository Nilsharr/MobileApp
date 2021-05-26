package com.example.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.phone_database.Phone;
import com.example.app.utils.Constants;
import com.example.app.utils.Utilities;

import java.util.HashMap;
import java.util.Objects;

public class PhonesDatabaseFormActivity extends AppCompatActivity {
    private AutoCompleteTextView phoneManufacturerInput;
    private EditText phoneModelInput;
    private EditText phoneAndroidVersionInput;
    private EditText phoneWebsiteInput;
    private Phone phone;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Returning to previous activity
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phones_database_form);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        phoneManufacturerInput = findViewById(R.id.phoneManufacturerInput);
        phoneModelInput = findViewById(R.id.phoneModelInput);
        phoneAndroidVersionInput = findViewById(R.id.phoneAndroidVersionInput);
        phoneWebsiteInput = findViewById(R.id.phoneWebsiteInput);

        phoneManufacturerInput.setOnFocusChangeListener(inputListener);
        phoneModelInput.setOnFocusChangeListener(inputListener);
        phoneAndroidVersionInput.setOnFocusChangeListener(inputListener);
        phoneWebsiteInput.setOnFocusChangeListener(inputListener);

        // String array with manufacturers names
        String[] manufacturers = getResources().getStringArray(R.array.phone_manufacturers_list);
        // Creating the adapter and setting it to the AutoCompleteTextView
        phoneManufacturerInput.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, manufacturers));

        // Allows to lose focus on view and closes keyboard on screen touch
        findViewById(R.id.PhonesDatabaseFormActivity).setOnTouchListener((view, event) -> {
            Utilities.clearFocusAndHideKeyboard(view);
            view.performClick();
            return false;
        });

        // Lose focus on view when done key pressed on keyboard
        phoneWebsiteInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Utilities.clearFocusAndHideKeyboard(view);
            }
            return false;
        });

        // if this activity has been started to edit phone
        // and phone object is passed from previous activity
        // sets EditText fields to given phone values
        // and cursor to suitable position
        if (getIntent().hasExtra(Constants.INTENT_PHONE_TO_EDIT_OBJECT)) {
            phone = getIntent().getParcelableExtra(Constants.INTENT_PHONE_TO_EDIT_OBJECT);
            phoneManufacturerInput.setText(phone.getManufacturer());
            phoneManufacturerInput.setSelection(phone.getManufacturer().length());
            phoneModelInput.setText(phone.getModel());
            phoneModelInput.setSelection(phone.getModel().length());
            phoneAndroidVersionInput.setText(phone.getAndroidVersion());
            phoneAndroidVersionInput.setSelection(phone.getAndroidVersion().length());
            phoneWebsiteInput.setText(phone.getWebAddress());
            phoneWebsiteInput.setSelection(phone.getWebAddress().length());
        }

        // handles click on www button
        // if given url address is valid
        // adds https:// prefix if it's not present
        // and opens given web address in browser
        // else shows toast with error message
        findViewById(R.id.phoneWebsiteButton).setOnClickListener(view -> {
            String urlAddress = phoneWebsiteInput.getText().toString();
            if (Patterns.WEB_URL.matcher(urlAddress).matches()) {
                if (!urlAddress.startsWith("https://") && !urlAddress.startsWith("http://")) {
                    urlAddress = "https://" + urlAddress;
                }
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress)));
            } else {
                Toast.makeText(this, getString(R.string.error_phone_website_input_incorrect), Toast.LENGTH_SHORT).show();
            }
        });

        // cancel button finishes current activity
        findViewById(R.id.phoneCancelButton).setOnClickListener(view -> finish());

        // if all entered data is valid returns new or edited phone object
        // to previous activity
        // else clears focus and shows toast with error message
        findViewById(R.id.phoneSaveButton).setOnClickListener(view -> {
            if (isAllInputValid()) {
                // if phone is null new phone object is created
                if (phone == null) {
                    phone = new Phone(phoneManufacturerInput.getText().toString(), phoneModelInput.getText().toString(), phoneAndroidVersionInput.getText().toString(), phoneWebsiteInput.getText().toString());
                }
                // if phone object exists (it was obtained from previous activity) it's fields are updated
                else {
                    phone.setManufacturer(phoneManufacturerInput.getText().toString());
                    phone.setModel(phoneModelInput.getText().toString());
                    phone.setAndroidVersion(phoneAndroidVersionInput.getText().toString());
                    phone.setWebAddress(phoneWebsiteInput.getText().toString());
                }
                Intent replyIntent = new Intent();
                replyIntent.putExtra(Constants.INTENT_PHONE_OBJECT, phone);
                setResult(RESULT_OK, replyIntent);
                Toast.makeText(this, getString(R.string.message_item_saved), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Utilities.clearFocusAndHideKeyboard(findViewById(R.id.PhonesDatabaseFormActivity));
                Toast.makeText(this, getString(R.string.message_entered_phone_data_invalid), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // true if all inputs match equivalent regex
    private boolean isAllInputValid() {
        return phoneManufacturerInput.getText().toString().matches("^[A-Z][A-Za-z .]+$") &&
                phoneModelInput.getText().toString().matches("^[A-Z][A-Za-z .\\d/+]+$") &&
                phoneAndroidVersionInput.getText().toString().matches("^\\d+(?:\\.\\d+)?$") &&
                Patterns.WEB_URL.matcher(phoneWebsiteInput.getText().toString()).matches();
    }

    //
    private final View.OnFocusChangeListener inputListener = (view, hasFocus) -> {
        // when current input field loses focus
        if (!hasFocus) {
            // hash map with error messages for adequate input field
            HashMap<Integer, String> errorMessage = new HashMap<Integer, String>() {{
                put(R.id.phoneManufacturerInput, getString(R.string.error_phone_manufacturer_input_incorrect));
                put(R.id.phoneModelInput, getString(R.string.error_phone_model_input_incorrect));
                put(R.id.phoneAndroidVersionInput, getString(R.string.error_phone_android_version_incorrect));
                put(R.id.phoneWebsiteInput, getString(R.string.error_phone_website_input_incorrect));
            }};
            // hash map with regex for adequate input field
            HashMap<Integer, String> regex = new HashMap<Integer, String>() {{
                put(R.id.phoneManufacturerInput, "^[A-Z][A-Za-z .]+$");
                put(R.id.phoneModelInput, "^[A-Z][A-Za-z .\\d/+]+$");
                put(R.id.phoneAndroidVersionInput, "^\\d+(?:\\.\\d+)?$");
                put(R.id.phoneWebsiteInput, Patterns.WEB_URL.toString());
            }};

            // setting appropriate error message if input doesn't match regex
            if (!((EditText) view).getText().toString().matches(Objects.requireNonNull(regex.get(view.getId()), "No regex found for given view"))) {
                ((EditText) view).setError(errorMessage.get(view.getId()));
            }
        }
    };
}
