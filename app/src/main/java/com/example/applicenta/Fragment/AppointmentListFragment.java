package com.example.applicenta.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.applicenta.Adapter.MyAppointmentRecyclerViewAdapter;
import com.example.applicenta.Interface.OnAppointmentListInteractionListener;
import com.example.applicenta.Model.AppointmentModel;
import com.example.applicenta.Model.DoctorAddress;
import com.example.applicenta.Model.UserModel;
import com.example.applicenta.R;
import com.example.applicenta.general.Appointment;
import com.example.applicenta.general.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppointmentListFragment extends Fragment {

    private static final String TAG = "AppointmentListFragment";

    private OnAppointmentListInteractionListener mListener;
    private MyAppointmentRecyclerViewAdapter myAppointmentRecyclerViewAdapter;


    public AppointmentListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            myAppointmentRecyclerViewAdapter = new MyAppointmentRecyclerViewAdapter(new ArrayList<>(), mListener);


//            HandlerThread handlerThread = new HandlerThread("");
//            handlerThread.start();
//            Handler handler = new Handler(handlerThread.getLooper());
//            handler.post(() -> {
//                while(true) {
//                    myAppointmentRecyclerViewAdapter.checkHour();
//                }
//            });

            recyclerView.setAdapter(myAppointmentRecyclerViewAdapter);
        }
        setUpdateListener();

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAppointmentListInteractionListener) {
            mListener = (OnAppointmentListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAppointmentListInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


//    private void populateAppointmentList() {
//        FirebaseFirestore.getInstance().collection(Constants.FIREBASE_USERS).get().addOnSuccessListener(queryDocumentSnapshots ->  {
//            for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
//                if(documentSnapshot.get(Constants.FIREBASE_ID).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
////                    ArrayList<AppointmentModel> appointments = (ArrayList<AppointmentModel>)documentSnapshot.get(Constants.FIREBASE_BOOKINGS);
//                    updateList(documentSnapshot);
//                }
//            }
//        }).addOnFailureListener(e -> {
//            Log.e(TAG, "populateAppointmentList: ", e);
//        });
//    }

    private void setUpdateListener() {
        Log.d(TAG, "setUpdateListener: hmm de cate ori");
        FirebaseFirestore.getInstance().collection(Constants.FIREBASE_USERS).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if(documentSnapshot.get(Constants.FIREBASE_ID).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    documentSnapshot.getReference().addSnapshotListener((documentSnapshot1, e) -> {
                        if(e != null) {
                            Log.e(TAG, "onEvent: ", e);
                            return;
                        }
                        Log.d(TAG, "setUpdateListener: aici");
                        updateList(documentSnapshot1);
                    });
                }
            }
        });
    }


    private void updateList(DocumentSnapshot documentSnapshot) {
        List<Appointment> list = new ArrayList<>();

        UserModel userModel = documentSnapshot.toObject(UserModel.class);

        ArrayList<AppointmentModel> appointments = userModel.getBookings();

        if(appointments.size() == 0) {
            myAppointmentRecyclerViewAdapter.loadNewData(new ArrayList<>());
            return;
        }

        for (AppointmentModel appointment : appointments) {
            String id = appointment.getDoctorId();
            FirebaseFirestore.getInstance().collection(Constants.FIREBASE_DOCTORS).document(id).get().addOnSuccessListener(documentSnapshot1 -> {
                String date = appointment.getDate();
                String hour = appointment.getHour();
                String firstName = documentSnapshot1.get(Constants.FIREBASE_FIRST_NAME).toString();
                String lastName = documentSnapshot1.get(Constants.FIREBASE_LAST_NAME).toString();
                String name = firstName + " " + lastName;
                String specialty = documentSnapshot1.get(Constants.FIREBASE_SPECIALTY).toString();
                HashMap<String, String> addressMap = (HashMap<String, String>) documentSnapshot1.get(Constants.FIREBASE_DOCTOR_ADDRESS);

                DoctorAddress address = new DoctorAddress(addressMap.get(Constants.FIREBASE_ADDRESS_STREET), addressMap.get(Constants.FIREBASE_ADDRESS_CITY), addressMap.get(Constants.FIREBASE_ADDRESS_COUNTY));

                list.add(new Appointment(id, name, specialty, address, date, hour));
                myAppointmentRecyclerViewAdapter.loadNewData(list);
            });

        }
    }

}
