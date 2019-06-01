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

import com.example.applicenta.Model.AppointmentModel;
import com.example.applicenta.R;
import com.example.applicenta.general.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";

//    private EditText usernameEditText, emailEditText, passwordEditText, checkPasswordEditText,
//        firstNameEditText, lastNameEditText, telephoneEditText;

    private TextInputLayout usernameInputLayout, emailInputLayout, passwordInputLayout, checkPasswordInputLayout, firstNameInputLayout, lastNameInputLayout, telephoneInputLayout;

    private Button registerBtn, backBtn;

    private FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameInputLayout = findViewById(R.id.usernameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        checkPasswordInputLayout = findViewById(R.id.checkPasswordInputLayout);
        firstNameInputLayout = findViewById(R.id.firstNameInputLayout);
        lastNameInputLayout = findViewById(R.id.lastNameInputLayout);
        telephoneInputLayout = findViewById(R.id.telephoneInputLayout);

//        usernameEditText = findViewById(R.id.usernameEditText);
//        emailEditText = findViewById(R.id.emailEditText);
//        passwordEditText = findViewById(R.id.passwordEditText);
//        checkPasswordEditText = findViewById(R.id.checkPasswordEditText);
//        firstNameEditText = findViewById(R.id.firstNameEditText);
//        lastNameEditText = findViewById(R.id.lastNameEditText);
//        telephoneEditText = findViewById(R.id.telephoneEditText);

        registerBtn = findViewById(R.id.registerBtn);
        backBtn = findViewById(R.id.signUpBackBtn);

        registerBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void registerUser() {

        final String username = usernameInputLayout.getEditText().getText().toString().trim();
        final String email = emailInputLayout.getEditText().getText().toString().trim();
        final String password = passwordInputLayout.getEditText().getText().toString().trim();
        final String checkPassword = checkPasswordInputLayout.getEditText().getText().toString().trim();
        final String firstName = firstNameInputLayout.getEditText().getText().toString().trim();
        final String lastName = lastNameInputLayout.getEditText().getText().toString().trim();
        final String telephoneNumber = telephoneInputLayout.getEditText().getText().toString().trim();

        if(username.isEmpty()) {
            usernameInputLayout.setError("Username required");
            usernameInputLayout.requestFocus();
            return;
        }

        if(username.length() > 15) {
            usernameInputLayout.setError("Username too long");
            usernameInputLayout.requestFocus();
            return;
        }

        if(email.isEmpty()) {
            emailInputLayout.setError("Email required");
            emailInputLayout.requestFocus();
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Enter a valid email");
            emailInputLayout.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordInputLayout.setError("Password required");
            passwordInputLayout.requestFocus();
            return;
        }

        if(password.length() < 6) {
            passwordInputLayout.setError("password should be at least 6 characters long");
            passwordInputLayout.requestFocus();
            return;
        }

        if(checkPassword.isEmpty()) {
            checkPasswordInputLayout.setError("Password required");
            checkPasswordInputLayout.requestFocus();
            return;
        }
        if(!password.equals(checkPassword)) {
            checkPasswordInputLayout.setError("Doesn't match");
            checkPasswordInputLayout.requestFocus();
            return;
        }

        if(firstName.isEmpty()) {
            firstNameInputLayout.setError("First name required");
            firstNameInputLayout.requestFocus();
            return;
        }

        if(firstName.matches("\\d+")) {
            firstNameInputLayout.setError("First name shouldn't contain a number");
            firstNameInputLayout.requestFocus();
            return;
        }

        if(lastName.isEmpty()) {
            lastNameInputLayout.setError("Last name required");
            lastNameInputLayout.requestFocus();
            return;
        }

        if(lastName.matches("\\d+")) {
            lastNameInputLayout.setError("Last name shouldn't contain a number");
            lastNameInputLayout.requestFocus();
            return;
        }

        if(telephoneNumber.isEmpty()) {
            telephoneInputLayout.setError("Phone number required");
            telephoneInputLayout.requestFocus();
            return;
        }

        if(!PhoneNumberUtils.isGlobalPhoneNumber(telephoneNumber) || telephoneNumber.length() != 10) {
            telephoneInputLayout.setError("Enter a valid phone number");
            telephoneInputLayout.requestFocus();
            return;
        }


        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            final Map<String, Object> regUser = new HashMap<>();
            regUser.put(Constants.FIREBASE_ID, mFirebaseAuth.getCurrentUser().getUid());
            regUser.put(Constants.FIREBASE_USERNAME, username);
            regUser.put(Constants.FIREBASE_EMAIL, email);
            regUser.put(Constants.FIREBASE_FIRST_NAME, firstName);
            regUser.put(Constants.FIREBASE_LAST_NAME, lastName);
            regUser.put(Constants.FIREBASE_TELEPHONE_NUMBER, telephoneNumber);
            regUser.put(Constants.FIREBASE_DOCTOR_CHECK, Constants.IS_NOT_DOCTOR);
            regUser.put(Constants.FIREBASE_PHOTO_PATH, Constants.FIREBASE_DEFAULT);
            regUser.put(Constants.FIREBASE_BOOKINGS, new ArrayList<AppointmentModel>());
            regUser.put(Constants.FIREBASE_FAVORITES, new ArrayList<>());

            db.collection(Constants.FIREBASE_USERS).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.getData().containsValue(mFirebaseAuth.getCurrentUser().getUid())) {
                                Toast.makeText(SignUpActivity.this, "This account already exists", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if(documentSnapshot.get(Constants.FIREBASE_TELEPHONE_NUMBER).toString().equals(telephoneNumber)) {
                                telephoneInputLayout.setError("This phone number already exists");
                                telephoneInputLayout.requestFocus();
                                return;
                            }
                        }
                        db.collection(Constants.FIREBASE_USERS)
                                .add(regUser)
                                .addOnSuccessListener(documentReference -> {
                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra(Constants.FIREBASE_EMAIL, email);

                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerBtn:
                registerUser();
                break;
            case R.id.signUpBackBtn:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
