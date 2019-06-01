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

import com.example.applicenta.Adapter.MyFavoritesRecyclerViewAdapter;
import com.example.applicenta.Interface.DoctorListFragmentInteractionListener;
import com.example.applicenta.R;
import com.example.applicenta.general.Constants;
import com.example.applicenta.general.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FavoritesListFragment extends Fragment {

    public static int count = 0;

    private static final String TAG = "FavoritesListFragment";

    private DoctorListFragmentInteractionListener mListener;
    private MyFavoritesRecyclerViewAdapter myFavoritesRecyclerViewAdapter;
    private RecyclerView recyclerView;

    public FavoritesListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFavoritesListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {

            recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            myFavoritesRecyclerViewAdapter = new MyFavoritesRecyclerViewAdapter(new ArrayList<>(), mListener, getContext());

            recyclerView.setAdapter(myFavoritesRecyclerViewAdapter);
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

//    private void populateFavoritesList() {
//
//        FirebaseFirestore.getInstance().collection(Constants.FIREBASE_USERS).get().addOnSuccessListener(queryDocumentSnapshots -> {
//            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                if(documentSnapshot.get(Constants.FIREBASE_ID).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                    updateList(documentSnapshot);
//                }
//            }
//        }).addOnFailureListener(e -> {
//            Log.e(TAG, "populateFavoritesList: ", e);
//        });
//    }

    private void setFavoritesListener() {
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
        List<Doctor> list = new ArrayList<>();

        if(((ArrayList<String>)documentSnapshot.get(Constants.FIREBASE_FAVORITES)).size() == 0) {
            myFavoritesRecyclerViewAdapter.loadNewData(new ArrayList<>());
            return;
        }

        for(String id : (List<String>) documentSnapshot.get(Constants.FIREBASE_FAVORITES)) {
            FirebaseFirestore.getInstance().collection(Constants.FIREBASE_DOCTORS).document(id).get().addOnSuccessListener(documentSnapshot1 -> {
                try {
                    list.add(new Doctor(Objects.requireNonNull(documentSnapshot1.get(Constants.FIREBASE_FIRST_NAME)).toString(),
                            Objects.requireNonNull(documentSnapshot1.get(Constants.FIREBASE_LAST_NAME)).toString(),
                            Objects.requireNonNull(documentSnapshot1.get(Constants.FIREBASE_EMAIL)).toString(),
                            Objects.requireNonNull(documentSnapshot1.get(Constants.FIREBASE_TELEPHONE_NUMBER)).toString(),
                            Objects.requireNonNull(documentSnapshot1.get(Constants.FIREBASE_SPECIALTY)).toString(),
                            Objects.requireNonNull(documentSnapshot1.get(Constants.FIREBASE_PHOTO_PATH)).toString()));
                } catch(NullPointerException e) {
                    Log.e(TAG, "populateFavoritesList: ", e);
                }
                myFavoritesRecyclerViewAdapter.loadNewData(list);
            });
        }
    }

}
