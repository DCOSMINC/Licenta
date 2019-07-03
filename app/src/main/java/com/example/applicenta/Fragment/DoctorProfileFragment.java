package com.example.applicenta.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import com.example.applicenta.general.FavoriteCheck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class DoctorProfileFragment extends Fragment implements View.OnClickListener{


    private static final String TAG = "DoctorProfileActivity";



    private CircleImageView profileImage;
    private TextView profileName, profileSpecialty;


    Button bookingBtn;
    Button addToFavoriteBtn;

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
        profileImage = view.findViewById(R.id.doctorProfilePicture);
        profileName = view.findViewById(R.id.doctorProfileName);
        profileSpecialty = view.findViewById(R.id.doctorProfileSpecialty);

        FavoriteCheck.checkIfFavorite(value -> {
            if(value) {
                addToFavoriteBtn = view.findViewById(R.id.favoriteBtn);
                addToFavoriteBtn.setText(getResources().getString(R.string.remove_favorite_text));
                addToFavoriteBtn.setOnClickListener(v -> {
                    deleteFavorite();
                    getFragmentManager().popBackStackImmediate();
                });
            } else {
                addToFavoriteBtn = view.findViewById(R.id.favoriteBtn);
                addToFavoriteBtn.setText(getResources().getString(R.string.add_favorite_text));
                addToFavoriteBtn.setOnClickListener(v -> {
                    addFavorite();
                });
            }
        }, getArguments().getString(Constants.FIREBASE_DOCTOR_ID));

        profileName.setText(String.format("Dr. %s %s", getArguments().getString(Constants.FIREBASE_FIRST_NAME), getArguments().getString(Constants.FIREBASE_LAST_NAME)));
        profileSpecialty.setText(getArguments().getString(Constants.FIREBASE_SPECIALTY));


        if(!getArguments().getString(Constants.FIREBASE_PHOTO_PATH).equals(Constants.FIREBASE_DEFAULT)) {
            Uri uri = Uri.parse(getArguments().getString(Constants.FIREBASE_PHOTO_PATH));
            Glide.with(getActivity().getApplicationContext()).load(uri).into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.circle_profile_placeholder);
        }

        bookingBtn.setOnClickListener(this);


        DoctorCheck.checkIfDoctor(value -> {
            if(value) {
                bookingBtn.setVisibility(View.INVISIBLE);
                addToFavoriteBtn.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }

    private void addFavorite() {
        FirebaseFirestore.getInstance().collection(Constants.FIREBASE_USERS).get().addOnSuccessListener(queryDocumentSnapshots -> {
           for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
               if(documentSnapshot.get(Constants.FIREBASE_ID).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    if(!((List<String>)documentSnapshot.get(Constants.FIREBASE_FAVORITES)).contains(getArguments().getString(Constants.FIREBASE_DOCTOR_ID))) {
                        documentSnapshot.getReference().update(Constants.FIREBASE_FAVORITES, FieldValue.arrayUnion(getArguments().getString(Constants.FIREBASE_DOCTOR_ID)));
                    }
               }
           }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "addFavorite: ", e);
        });
    }

    private void deleteFavorite() {
        FirebaseFirestore.getInstance().collection(Constants.FIREBASE_USERS).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if(documentSnapshot.get(Constants.FIREBASE_ID).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    for(String id : (List<String>)documentSnapshot.get(Constants.FIREBASE_FAVORITES)) {
                        if(id.equals(getArguments().getString(Constants.FIREBASE_DOCTOR_ID))) {
                            documentSnapshot.getReference().update(Constants.FIREBASE_FAVORITES, FieldValue.arrayRemove(getArguments().getString(Constants.FIREBASE_DOCTOR_ID)));
                        }
                    }
                }
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "deleteFavorite: ", e);
        });
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
