package com.example.applicenta.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applicenta.Decorator.AvailableDayDecorator;
import com.example.applicenta.Decorator.WeekendDecorator;
import com.example.applicenta.Model.AppointmentModel;
import com.example.applicenta.R;
import com.example.applicenta.general.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookingActivityFragment extends Fragment {

    private static final String TAG = "BookingActivityFragment";

    public BookingActivityFragment() {
    }

    private List<String> hour_list;
    private int currentPosition = 0;

    MaterialCalendarView calendarView;

    private TextView back, forward, selectedHour;
    private Button completeBooking;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);


        back = view.findViewById(R.id.goBack);
        forward = view.findViewById(R.id.goForward);
        selectedHour = view.findViewById(R.id.selectedHour);

        completeBooking = view.findViewById(R.id.completeBooking);

        calendarView = view.findViewById(R.id.calendarWeekView);

        calendarView
                .state()
                .edit()
                .setFirstDayOfWeek(CalendarDay.today().getDate().getDayOfWeek())
                .setMinimumDate(CalendarDay.today())
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();

        calendarView
                .addDecorators(new WeekendDecorator(getContext()), new AvailableDayDecorator(getContext()));


        calendarView.setOnDateChangedListener((materialCalendarView, calendarDay, b) -> {
            initializeList();
            setHour_list(calendarDay);
            Toast.makeText(getContext(), "Date " + calendarDay, Toast.LENGTH_LONG).show();
            back.setVisibility(View.VISIBLE);
            forward.setVisibility(View.VISIBLE);
            selectedHour.setVisibility(View.VISIBLE);
            completeBooking.setVisibility(View.VISIBLE);

            currentPosition = 0;

            if(hour_list.size() > 0) {
                selectedHour.setText(hour_list.get(currentPosition));
            } else {
                Toast.makeText(getContext(), "Nu exista ore valabile pentru rezervare", Toast.LENGTH_LONG).show();
                return;
            }

            back.setOnClickListener(v -> {
                if(currentPosition == 0) {
                    currentPosition = hour_list.size() - 1;
                } else {
                    currentPosition--;
                }
                selectedHour.setText(hour_list.get(currentPosition));
            });

            forward.setOnClickListener(v -> {
                if(currentPosition == hour_list.size() - 1) {
                    currentPosition = 0;
                } else {
                    currentPosition++;
                }
                selectedHour.setText(hour_list.get(currentPosition));
            });

            completeBooking.setOnClickListener(v -> {
                makeBooking(selectedHour.getText().toString(), calendarDay);
                getFragmentManager().popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            });



//            Thread thread = new Thread(() -> {
//                Fragment fragment = new BookingActivityTimeSelectionFragment();
//
//                Bundle bundle = new Bundle();
//                assert getArguments() != null;
//                bundle.putString(Constants.FIREBASE_DOCTOR_ID, getArguments().getString(Constants.FIREBASE_DOCTOR_ID));
//                bundle.putString(Constants.DATE_SELECTED, calendarDay.toString());
//                fragment.setArguments(bundle);
//
//
//                FragmentManager fm = getFragmentManager();
//
//                assert fm != null;
//                FragmentTransaction transaction = fm.beginTransaction();
//                transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
//                transaction.replace(R.id.fragmentContainer, fragment, fragment.getTag());
//                transaction.addToBackStack(null);
//                transaction.commit();
//            });
//            thread.start();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }



    private void makeBooking(String hour, CalendarDay calendarDay) {
        assert getArguments() != null;
        FirebaseFirestore.getInstance()
                .collection(Constants.FIREBASE_USERS)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if(documentSnapshot.get(Constants.FIREBASE_ID).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    if(documentSnapshot.contains(Constants.FIREBASE_BOOKINGS)) {
                        if (((List<HashMap>) documentSnapshot.get(Constants.FIREBASE_BOOKINGS)).size() > 2) {
                            Toast.makeText(getContext(), "Nu poti avea mai mult de 3 programari simultan", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    AppointmentModel appointmentModel = new AppointmentModel(getArguments().getString(Constants.FIREBASE_DOCTOR_ID), calendarDay.toString(), hour);
//                    HashMap<String, String> booking = new HashMap<>();
//                    booking.put(Constants.DATE, calendarDay.toString());
//                    booking.put(Constants.HOUR, hour);
//                    booking.put(Constants.FIREBASE_DOCTOR_ID, getArguments().getString(Constants.FIREBASE_DOCTOR_ID));

                    documentSnapshot.getReference().update(Constants.FIREBASE_BOOKINGS, FieldValue.arrayUnion(appointmentModel));

                    FirebaseFirestore.getInstance()
                            .collection(Constants.FIREBASE_DOCTORS)
                            .document(Objects.requireNonNull(getArguments().getString(Constants.FIREBASE_DOCTOR_ID))).get()
                            .addOnSuccessListener(documentSnapshot1 -> {
                                documentSnapshot1.getReference()
                                        .update(Objects.requireNonNull(calendarDay.toString()), FieldValue.arrayUnion(hour));
                                setHour_list(calendarDay);
                                initializeList();
                            }).addOnFailureListener(e -> Log.e(TAG, "makeBooking: ", e));

                }
            }
        });
    }


    private void setHour_list(CalendarDay calendarDay) {

        FirebaseFirestore.getInstance().collection(Constants.FIREBASE_DOCTORS).document(getArguments().getString(Constants.FIREBASE_DOCTOR_ID)).get().addOnSuccessListener(documentSnapshot -> {
            List<String> testList = new ArrayList<>();

            if(documentSnapshot.contains(calendarDay.toString())) {
                testList = (List<String>) documentSnapshot.get(calendarDay.toString());
            }

            for(int i = 0; i < testList.size(); i++) {
                if(hour_list.contains(testList.get(i))) {
                    hour_list.remove(testList.get(i));
                }
            }
        }).addOnFailureListener(e-> Log.e(TAG, "setButtons: ", e));


    }

    private void initializeList() {
        hour_list = new ArrayList<>();
        hour_list.add(Constants.HOUR_ONE);
        hour_list.add(Constants.HOUR_TWO);
        hour_list.add(Constants.HOUR_THREE);
        hour_list.add(Constants.HOUR_FOUR);
        hour_list.add(Constants.HOUR_FIVE);
        hour_list.add(Constants.HOUR_SIX);
        hour_list.add(Constants.HOUR_SEVEN);
        hour_list.add(Constants.HOUR_EIGHT);
    }

}
