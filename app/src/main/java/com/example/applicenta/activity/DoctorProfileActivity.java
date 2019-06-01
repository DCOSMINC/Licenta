package com.example.applicenta.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.applicenta.R;
public class DoctorProfileActivity extends AppCompatActivity  {

    private static final String TAG = "DoctorProfileActivity";

//    Button bookingBtn;
//    ToggleButton addToFavoriteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

//        bookingBtn = findViewById(R.id.bookingBtn);
//        addToFavoriteBtn = findViewById(R.id.addFavBtn);
//
//        bookingBtn.setOnClickListener(this);
//        addToFavoriteBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if(isChecked) {
//                addToFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.baseline_favorite_black_24));
//            } else {
//                addToFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.baseline_favorite_border_black_24));
//            }
//        });
//        Fragment fragment = new DoctorProfileFragment();
//        Bundle bundle = new Bundle();
//        fragment.setArguments(bundle);
//
//        FragmentManager fragmentManager = getSupportFragmentManager();
//
//        fragmentManager.beginTransaction()
//                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//                .add(R.id.doctorProfileContainer, fragment, fragment.getTag())
//                .addToBackStack(null)
//                .commit();
    }

//    @Override
//    public void onClick(View v) {
//        switch(v.getId()) {
//            case R.id.bookingBtn:
//                Intent intent = new Intent(DoctorProfileActivity.this, BookingActivity.class);
//                intent.putExtra(Constants.FIREBASE_DOCTOR_ID, getIntent().getStringExtra(Constants.FIREBASE_DOCTOR_ID));
//                startActivity(intent);
//                break;
//            default:
//                break;
//        }
//    }
}
