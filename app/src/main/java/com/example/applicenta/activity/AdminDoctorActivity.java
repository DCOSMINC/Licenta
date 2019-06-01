package com.example.applicenta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.applicenta.Model.DoctorAddress;
import com.example.applicenta.R;
import com.example.applicenta.general.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminDoctorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AdminDoctorActivity";

    private TextInputLayout emailDocInputLayout, passwordDocInputLayout, checkPasswordDocInputLayout,
            firstNameDocInputLayout, lastNameDocInputLayout, telephoneDocInputLayout, specialtyDocInputLayout,
            streetNameDocInputLayout, streetNumberDocInputLayout, cityDocInputLayout, countyDocInputLayout;

    private FirebaseAuth mFirebaseAuth;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_doctor);

        emailDocInputLayout = findViewById(R.id.emailDocInputLayout);
        passwordDocInputLayout = findViewById(R.id.passwordDocInputLayout);
        checkPasswordDocInputLayout = findViewById(R.id.checkPasswordDocInputLayout);
        firstNameDocInputLayout = findViewById(R.id.firstNameDocInputLayout);
        lastNameDocInputLayout = findViewById(R.id.lastNameDocInputLayout);
        telephoneDocInputLayout = findViewById(R.id.telephoneDocInputLayout);
        specialtyDocInputLayout = findViewById(R.id.specialtyDocInputLayout);
        streetNameDocInputLayout = findViewById(R.id.streetDocInputLayout);
        streetNumberDocInputLayout = findViewById(R.id.numberDocInputLayout);
        cityDocInputLayout = findViewById(R.id.cityDocInputLayout);
        countyDocInputLayout = findViewById(R.id.countyDocInputLayout);

        Button registerDocBtn = findViewById(R.id.registerDocBtn);
        Button backDocBtn = findViewById(R.id.signUpDocBackBtn);

        registerDocBtn.setOnClickListener(this);
        backDocBtn.setOnClickListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void registerUser() {
        final String email = emailDocInputLayout.getEditText().getText().toString().trim();
        final String password = passwordDocInputLayout.getEditText().getText().toString().trim();
        final String checkPassword = checkPasswordDocInputLayout.getEditText().getText().toString().trim();
        final String firstName = firstNameDocInputLayout.getEditText().getText().toString().trim();
        final String lastName = lastNameDocInputLayout.getEditText().getText().toString().trim();
        final String telephoneNumber = telephoneDocInputLayout.getEditText().getText().toString().trim();
        final String specialty = specialtyDocInputLayout.getEditText().getText().toString().trim();
        final String streetName = streetNameDocInputLayout.getEditText().getText().toString().trim();
        final String streetNumber = streetNumberDocInputLayout.getEditText().getText().toString().trim();
        final String county = countyDocInputLayout.getEditText().getText().toString().trim();
        final String city = cityDocInputLayout.getEditText().getText().toString().trim();

        if(email.isEmpty()) {
            emailDocInputLayout.setError("Email required");
            emailDocInputLayout.requestFocus();
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailDocInputLayout.setError("Enter a valid email");
            emailDocInputLayout.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordDocInputLayout.setError("Password required");
            passwordDocInputLayout.requestFocus();
            return;
        }

        if(password.length() < 6) {
            passwordDocInputLayout.setError("password should be at least 6 characters long");
            passwordDocInputLayout.requestFocus();
            return;
        }

        if(checkPassword.isEmpty()) {
            checkPasswordDocInputLayout.setError("Password required");
            checkPasswordDocInputLayout.requestFocus();
            return;
        }
        if(!password.equals(checkPassword)) {
            passwordDocInputLayout.setError("Doesn't match");
            passwordDocInputLayout.requestFocus();
            return;
        }

        if(firstName.isEmpty()) {
            firstNameDocInputLayout.setError("First name required");
            firstNameDocInputLayout.requestFocus();
            return;
        }

        if(firstName.matches("\\d+")) {
            firstNameDocInputLayout.setError("First name shouldn't contain a number");
            firstNameDocInputLayout.requestFocus();
            return;
        }

        if(lastName.isEmpty()) {
            lastNameDocInputLayout.setError("Last name required");
            lastNameDocInputLayout.requestFocus();
            return;
        }

        if(lastName.matches("\\d+")) {
            lastNameDocInputLayout.setError("Last name shouldn't contain a number");
            lastNameDocInputLayout.requestFocus();
            return;
        }

        if(telephoneNumber.isEmpty()) {
            telephoneDocInputLayout.setError("Phone number required");
            telephoneDocInputLayout.requestFocus();
            return;
        }

        if(!PhoneNumberUtils.isGlobalPhoneNumber(telephoneNumber)) {
            telephoneDocInputLayout.setError("Enter a valid phone number");
            telephoneDocInputLayout.requestFocus();
            return;
        }

        if(specialty.isEmpty()) {
            specialtyDocInputLayout.setError("Enter your specialty");
            specialtyDocInputLayout.requestFocus();
            return;
        }

        if(streetName.isEmpty()) {
            streetNameDocInputLayout.setError("Street name required");
            streetNameDocInputLayout.requestFocus();
        }

        if(streetNumber.isEmpty()) {
            streetNumberDocInputLayout.setError("Street number required");
            streetNumberDocInputLayout.requestFocus();
        }

        if(county.isEmpty()) {
            countyDocInputLayout.setError("County required");
            countyDocInputLayout.requestFocus();
        }

        if(city.isEmpty()) {
            cityDocInputLayout.setError("City required");
            cityDocInputLayout.requestFocus();
        }

        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            final Map<String, Object> regUser = new HashMap<>();
            regUser.put(Constants.FIREBASE_ID, Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid());
            regUser.put(Constants.FIREBASE_EMAIL, email);
            regUser.put(Constants.FIREBASE_FIRST_NAME, firstName);
            regUser.put(Constants.FIREBASE_LAST_NAME, lastName);
            regUser.put(Constants.FIREBASE_TELEPHONE_NUMBER, telephoneNumber);
            regUser.put(Constants.FIREBASE_SPECIALTY, specialty);
            regUser.put(Constants.FIREBASE_PHOTO_PATH, Constants.FIREBASE_DEFAULT);
            regUser.put(Constants.FIREBASE_DOCTOR_CHECK, Constants.IS_DOCTOR);

            DoctorAddress doctorAddress = new DoctorAddress(String.format("%s %s", streetName, streetNumber), city, county);

            regUser.put(Constants.FIREBASE_DOCTOR_ADDRESS, doctorAddress);

            db.collection(Constants.FIREBASE_DOCTORS).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(Objects.requireNonNull(documentSnapshot.getData()).containsValue(mFirebaseAuth.getCurrentUser().getUid())) {
                                Toast.makeText(AdminDoctorActivity.this, "This account already exists", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if(documentSnapshot.get(Constants.FIREBASE_TELEPHONE_NUMBER).toString().equals(telephoneNumber)) {
                                telephoneDocInputLayout.setError("This phone number already exists");
                                telephoneDocInputLayout.requestFocus();
                                return;
                            }
                        }

                        db.collection(Constants.FIREBASE_DOCTORS)
                                .add(regUser)
                                .addOnSuccessListener(documentReference -> {
                                    Intent intent = new Intent(AdminDoctorActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra(Constants.FIREBASE_EMAIL, email);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));

                    })
                    .addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: ", e);
            Toast.makeText(getApplicationContext(), "This e-mail is already in use", Toast.LENGTH_LONG).show();
        });

        mFirebaseAuth.signOut();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerDocBtn:
                registerUser();
                break;
            case R.id.signUpDocBackBtn:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
