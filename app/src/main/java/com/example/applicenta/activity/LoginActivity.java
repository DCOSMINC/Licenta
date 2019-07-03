package com.example.applicenta.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.applicenta.R;
import com.example.applicenta.general.Constants;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends Activity implements View.OnClickListener {

    private Button signInBtn, signUpBtn;

    private TextInputLayout emailSignInInputLayout, passwordSignInInputLayout;


    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signInBtn = findViewById(R.id.signInBtn);
        signUpBtn = findViewById(R.id.signUpBtn);
        emailSignInInputLayout = findViewById(R.id.emailSignInInputLayout);
        passwordSignInInputLayout = findViewById(R.id.passwordSignInInputLayout);


        mFirebaseAuth = FirebaseAuth.getInstance();

        signInBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);

        Objects.requireNonNull(emailSignInInputLayout.getEditText()).setText(getIntent().getStringExtra(Constants.FIREBASE_EMAIL));
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        outState.putString(Constants.LOGIN_EMAIL, emailSignInInputLayout.getEditText().getText().toString());
//        outState.putString(Constants.LOGIN_PASSWORD, passwordSignInInputLayout.getEditText().getText().toString());
//
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        if(savedInstanceState != null) {
//            emailSignInInputLayout.getEditText().setText(savedInstanceState.get(Constants.LOGIN_EMAIL).toString());
//            passwordSignInInputLayout.getEditText().setText(savedInstanceState.get(Constants.LOGIN_PASSWORD).toString());
//        }
//    }

    @Override
    protected void onStart() {
        if(mFirebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        super.onStart();
    }

    private boolean signIn() {
        if(emailSignInInputLayout.getEditText().getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailSignInInputLayout.getEditText().getText().toString()).matches()) {
            emailSignInInputLayout.setError("Enter a valid email");
            emailSignInInputLayout.requestFocus();
            return false;
        }

        if(passwordSignInInputLayout.getEditText().getText().toString().isEmpty()) {
            passwordSignInInputLayout.setError("password required");
            passwordSignInInputLayout.requestFocus();
            return false;
        }

        mFirebaseAuth.signInWithEmailAndPassword(emailSignInInputLayout.getEditText().getText().toString(), passwordSignInInputLayout.getEditText().getText().toString())
                .addOnSuccessListener(authResult -> {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                })
                .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, getApplicationContext().getString(R.string.incorrect_login), Toast.LENGTH_LONG).show());
        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.signInBtn:
                signIn();
                break;
            case R.id.signUpBtn:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                break;
            default:
                break;
        }
    }
}
