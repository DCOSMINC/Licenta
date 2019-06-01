package com.example.applicenta.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.applicenta.R;
import com.example.applicenta.general.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BookingActivityTimeSelectionFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = "BookingActivityTimeSele";
    Button firstBtn, secondBtn, thirdBtn, fourthBtn, fifthBtn, sixthBtn, seventhBtn, eighthBtn;

    public BookingActivityTimeSelectionFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_time_selection, container, false);

        firstBtn = view.findViewById(R.id.rowOneBtn);
        secondBtn = view.findViewById(R.id.rowTwoBtn);
        thirdBtn = view.findViewById(R.id.rowThreeBtn);
        fourthBtn = view.findViewById(R.id.rowFourBtn);
        fifthBtn = view.findViewById(R.id.rowFiveBtn);
        sixthBtn = view.findViewById(R.id.rowSixBtn);
        seventhBtn = view.findViewById(R.id.rowSevenBtn);
        eighthBtn = view.findViewById(R.id.rowEightBtn);

        setButtons();

        firstBtn.setOnClickListener(this);
        secondBtn.setOnClickListener(this);
        thirdBtn.setOnClickListener(this);
        fourthBtn.setOnClickListener(this);
        fifthBtn.setOnClickListener(this);
        sixthBtn.setOnClickListener(this);
        seventhBtn.setOnClickListener(this);
        eighthBtn.setOnClickListener(this);



        //Toast.makeText(getContext(), "A intrat", Toast.LENGTH_LONG).show();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.rowOneBtn:
                makeBooking(Constants.HOUR_ONE);
                break;
            case R.id.rowTwoBtn:
                makeBooking(Constants.HOUR_TWO);
                break;
            case R.id.rowThreeBtn:
                makeBooking(Constants.HOUR_THREE);
                break;
            case R.id.rowFourBtn:
                makeBooking(Constants.HOUR_FOUR);
                break;
            case R.id.rowFiveBtn:
                makeBooking(Constants.HOUR_FIVE);
                break;
            case R.id.rowSixBtn:
                makeBooking(Constants.HOUR_SIX);
                break;
            case R.id.rowSevenBtn:
                makeBooking(Constants.HOUR_SEVEN);
                break;
            case R.id.rowEightBtn:
                makeBooking(Constants.HOUR_EIGHT);
                break;
            default:
                Log.e(TAG, "onClick: eroare");
                break;
        }
    }

    private void makeBooking(String hour) {
        assert getArguments() != null;
        FirebaseFirestore.getInstance()
                .collection(Constants.FIREBASE_USERS)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if(documentSnapshot.get(Constants.FIREBASE_ID).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            if(((List<HashMap>)documentSnapshot.get(Constants.FIREBASE_BOOKINGS)).size() > 2) {
                                Toast.makeText(getContext(), "Nu poti avea mai mult de 3 programari simultan", Toast.LENGTH_LONG).show();
                                return;
                            } else {

                                HashMap<String, String> booking = new HashMap<>();
                                booking.put(Constants.DATE, getArguments().getString(Constants.DATE_SELECTED));
                                booking.put(Constants.HOUR, hour);
                                booking.put(Constants.FIREBASE_DOCTOR_ID, getArguments().getString(Constants.FIREBASE_DOCTOR_ID));

                                documentSnapshot.getReference().update(Constants.FIREBASE_BOOKINGS, FieldValue.arrayUnion(booking));

                                FirebaseFirestore.getInstance()
                                        .collection(Constants.FIREBASE_DOCTORS)
                                        .document(Objects.requireNonNull(getArguments().getString(Constants.FIREBASE_DOCTOR_ID))).get()
                                        .addOnSuccessListener(documentSnapshot1 -> {
                                            documentSnapshot1.getReference()
                                                    .update(Objects.requireNonNull(getArguments().getString(Constants.DATE_SELECTED)), FieldValue.arrayUnion(hour));
                                            setButtons();
                                        }).addOnFailureListener(e -> Log.e(TAG, "makeBooking: ", e));
                            }
                        }
                    }
                });


    }


    private void setButtons() {

        FirebaseFirestore.getInstance().collection(Constants.FIREBASE_DOCTORS).document(getArguments().getString(Constants.FIREBASE_DOCTOR_ID)).get().addOnSuccessListener(documentSnapshot -> {
            List<String> testList = new ArrayList<>();

            if(documentSnapshot.contains(getArguments().getString(Constants.DATE_SELECTED))) {
                testList = (List<String>) documentSnapshot.get(Objects.requireNonNull(getArguments().getString(Constants.DATE_SELECTED)));
            }

                for(int i = 0; i < testList.size(); i++) {

                    switch (testList.get(i)) {
                        case Constants.HOUR_ONE:
                            setNegativeButtonState(firstBtn);
                            break;
                        case Constants.HOUR_TWO:
                            setNegativeButtonState(secondBtn);
                            break;
                        case Constants.HOUR_THREE:
                            setNegativeButtonState(thirdBtn);
                            break;
                        case Constants.HOUR_FOUR:
                            setNegativeButtonState(fourthBtn);
                            break;
                        case Constants.HOUR_FIVE:
                            setNegativeButtonState(fifthBtn);
                            break;
                        case Constants.HOUR_SIX:
                            setNegativeButtonState(sixthBtn);
                            break;
                        case Constants.HOUR_SEVEN:
                            setNegativeButtonState(seventhBtn);
                            break;
                        case Constants.HOUR_EIGHT:
                            setNegativeButtonState(eighthBtn);
                            break;
                        default:

                            break;
                    }
                }
        }).addOnFailureListener(e-> Log.e(TAG, "setButtons: ", e));


    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        setButtons();
        super.onViewStateRestored(savedInstanceState);
    }

    @SuppressLint("ResourceAsColor")
    private void setNegativeButtonState(Button btn) {
        btn.setBackgroundColor(Color.GREEN);
        btn.setText(R.string.booking_state_negative);
    }
}
