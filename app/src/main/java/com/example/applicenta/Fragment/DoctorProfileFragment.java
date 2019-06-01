package com.example.applicenta.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.applicenta.R;
import com.example.applicenta.activity.BookingActivity;
import com.example.applicenta.general.Constants;
import com.example.applicenta.general.DoctorCheck;

import de.hdodenhof.circleimageview.CircleImageView;


public class DoctorProfileFragment extends Fragment implements View.OnClickListener{


    private static final String TAG = "DoctorProfileActivity";



    private CircleImageView profileImage;
    private TextView profileName, profileSpecialty;


    Button bookingBtn;
    ToggleButton addToFavoriteBtn;

    public DoctorProfileFragment() {
        // Required empty public constructor
    }

    public static DoctorProfileFragment newInstance(String param1, String param2) {
        DoctorProfileFragment fragment = new DoctorProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_doctor_profile, container, false);


        bookingBtn = view.findViewById(R.id.bookingBtn);
        addToFavoriteBtn = view.findViewById(R.id.addFavBtn);
        profileImage = view.findViewById(R.id.doctorProfilePicture);
        profileName = view.findViewById(R.id.doctorProfileName);
        profileSpecialty = view.findViewById(R.id.doctorProfileSpecialty);

        profileName.setText("Dr. " + getArguments().getString(Constants.FIREBASE_FIRST_NAME) + " " + getArguments().getString(Constants.FIREBASE_LAST_NAME));
        profileSpecialty.setText(getArguments().getString(Constants.FIREBASE_SPECIALTY));


        if(!getArguments().getString(Constants.FIREBASE_PHOTO_PATH).equals(Constants.FIREBASE_DEFAULT)) {
            Uri uri = Uri.parse(getArguments().getString(Constants.FIREBASE_PHOTO_PATH));
            Glide.with(getActivity().getApplicationContext()).load(uri).into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.circle_profile_placeholder);
        }

        bookingBtn.setOnClickListener(this);
        addToFavoriteBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                addToFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.baseline_favorite_black_24));
            } else {
                addToFavoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.baseline_favorite_border_black_24));
            }
        });

        DoctorCheck.checkIfDoctor(value -> {
            if(value) {
                bookingBtn.setVisibility(View.INVISIBLE);
                addToFavoriteBtn.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bookingBtn:
                Intent intent = new Intent(getContext(), BookingActivity.class);
                intent.putExtra(Constants.FIREBASE_DOCTOR_ID, getArguments().getString(Constants.FIREBASE_DOCTOR_ID));
                startActivity(intent);
                break;
            default:
                break;
        }
    }






}
