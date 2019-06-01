package com.example.applicenta.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.applicenta.Fragment.BookingActivityFragment;
import com.example.applicenta.R;
import com.example.applicenta.general.Constants;

import java.util.Objects;


public class BookingActivity extends AppCompatActivity {

    private static final String TAG = "BookingActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle bundle = new Bundle();
        bundle.putString(Constants.FIREBASE_DOCTOR_ID, getIntent().getStringExtra(Constants.FIREBASE_DOCTOR_ID));

        if(savedInstanceState == null) {
            Fragment fragment = new BookingActivityFragment();
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.fragmentContainer, fragment, fragment.getTag())
                    .commit();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

    }
}
