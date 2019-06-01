package com.example.applicenta.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.applicenta.Adapter.MyDoctorsRecyclerViewAdapter;
import com.example.applicenta.Interface.DoctorListFragmentInteractionListener;
import com.example.applicenta.R;
import com.example.applicenta.general.Constants;
import com.example.applicenta.general.Doctor;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class DoctorListFragment extends Fragment {

    private static final String TAG = "DoctorListFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DoctorListFragmentInteractionListener mListener;
    private MyDoctorsRecyclerViewAdapter myDoctorsRecyclerViewAdapter;
    private RecyclerView recyclerView;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DoctorListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateDoctorList();

        FirebaseFirestore.getInstance().collection(Constants.FIREBASE_DOCTORS).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if(e != null) {
                Log.w(TAG, "onEvent: ", e);
                return;
            }

            for(DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                populateDoctorList();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctors_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            myDoctorsRecyclerViewAdapter = new MyDoctorsRecyclerViewAdapter(new ArrayList<>(), mListener, getContext());
            recyclerView.setAdapter(myDoctorsRecyclerViewAdapter);

        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DoctorListFragmentInteractionListener) {
            mListener = (DoctorListFragmentInteractionListener) context;
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

    private void populateDoctorList() {
        final List<Doctor> doctorList = new ArrayList<>();

        db.collection(Constants.FIREBASE_DOCTORS).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                try {
                    doctorList.add(new Doctor(Objects.requireNonNull(documentSnapshot.get(Constants.FIREBASE_FIRST_NAME)).toString(),
                            Objects.requireNonNull(documentSnapshot.get(Constants.FIREBASE_LAST_NAME)).toString(),
                            Objects.requireNonNull(documentSnapshot.get(Constants.FIREBASE_EMAIL)).toString(),
                            Objects.requireNonNull(documentSnapshot.get(Constants.FIREBASE_TELEPHONE_NUMBER)).toString(),
                            Objects.requireNonNull(documentSnapshot.get(Constants.FIREBASE_SPECIALTY)).toString(),
                            Objects.requireNonNull(documentSnapshot.get(Constants.FIREBASE_PHOTO_PATH)).toString()));
                } catch(NullPointerException e) {
                    Log.e(TAG, "populateDoctorList: ", e);
                }
            }

            myDoctorsRecyclerViewAdapter.loadNewData(doctorList);

        }).addOnFailureListener(e -> {
            Log.e(TAG, "populateDoctorList: ", e);
        });
    }

    public void search(String searchString) {
        final List<Doctor> doctorList = new ArrayList<>();

        db.collection(Constants.FIREBASE_DOCTORS).get().addOnSuccessListener(queryDocumentSnapshots -> {
           for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
               if(documentSnapshot.get(Constants.FIREBASE_SPECIALTY).toString().toLowerCase().contains(searchString.toLowerCase())) {
                   doctorList.add(new Doctor(Objects.requireNonNull(documentSnapshot.get(Constants.FIREBASE_FIRST_NAME)).toString(),
                           Objects.requireNonNull(documentSnapshot.get(Constants.FIREBASE_LAST_NAME)).toString(),
                           Objects.requireNonNull(documentSnapshot.get(Constants.FIREBASE_EMAIL)).toString(),
                           Objects.requireNonNull(documentSnapshot.get(Constants.FIREBASE_TELEPHONE_NUMBER)).toString(),
                           Objects.requireNonNull(documentSnapshot.get(Constants.FIREBASE_SPECIALTY)).toString(),
                           Objects.requireNonNull(documentSnapshot.get(Constants.FIREBASE_PHOTO_PATH)).toString()));
               }
           }
           myDoctorsRecyclerViewAdapter.loadNewData(doctorList);
        });
    }

}
